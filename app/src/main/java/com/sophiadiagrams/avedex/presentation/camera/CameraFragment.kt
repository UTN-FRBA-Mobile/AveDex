package com.sophiadiagrams.avedex.presentation.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentCameraBinding
import com.sophiadiagrams.avedex.lib.models.User
import com.sophiadiagrams.avedex.lib.services.FirebaseService
import com.sophiadiagrams.avedex.lib.services.image_analyzer.ImageAnalyzerService
import com.sophiadiagrams.avedex.lib.util.FirebaseConstants.USERS_COLLECTION
import com.sophiadiagrams.avedex.presentation.util.OnSwipeTouchListener
import com.squareup.picasso.Picasso
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var _mContext: Context? = null
    private val mContext get() = _mContext!!

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET
    )
    private var fotoapparat: Fotoapparat? = null

    private var user = User()
    private lateinit var fb: FirebaseService
    private lateinit var ia: ImageAnalyzerService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        _mContext = requireContext()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fb = FirebaseService(Firebase.auth, Firebase.firestore, Firebase.storage)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat?.start()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fb.auth.currentUser
        if (currentUser == null) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Está acá y no en el onCreate porque necesita que el contexto esté inicializado
        ia = ImageAnalyzerService(mContext)
        getUser(view)
        if (hasNoPermissions())
            requestPermissions()
        initCamera()
        initListeners()
    }

    private fun getUser(v: View) {
        fb.db.collection(USERS_COLLECTION).document(fb.auth.currentUser?.uid!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user = it.result.toObject(User::class.java)!!
                    if (user.photoUrl.isNotEmpty())
                        Picasso.get().load(user.photoUrl).placeholder(R.drawable.ic_account)
                            .into(v.findViewById(R.id.iv_account) as ImageView)
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        with(binding) {
            // Button to take picture
            fabCamera.setOnClickListener { takePhoto() }

            // Account menu button
            ivAccount.setOnClickListener { v: View -> showAccountMenu(v, R.menu.account_menu) }

            // Gestures to swipe to avedex
            root.setOnTouchListener(object : OnSwipeTouchListener(mContext) {
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    Toast.makeText(
                        mContext, "Swipe up to open AveDex",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    Toast.makeText(
                        mContext,
                        "Swipe up to open AveDex",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSwipeUp() {
                    super.onSwipeUp()
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_camera_to_avedex)
                }

                override fun onSwipeDown() {
                    super.onSwipeDown()
                    Toast.makeText(mContext, "Swipe up to open AveDex", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    private fun initCamera() {
        fotoapparat = Fotoapparat(
            context = mContext,
            view = binding.cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            logger = loggers(
                logcat()
            ),
            cameraErrorCallback = { error ->
                println("Recorder errors: $error")
            }
        )
    }

    private fun showAccountMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(mContext, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.getItem(0).title = user.displayName
        popup.menu.getItem(1).setOnMenuItemClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_camera_to_avedex)
            true
        }
        popup.menu.getItem(2).setOnMenuItemClickListener {
            fb.auth.signOut()
            activity?.onBackPressedDispatcher?.onBackPressed()
            true
        }

        popup.show()
    }

    private fun takePhoto() {
        if (fotoapparat == null) {
            Toast.makeText(
                mContext,
                "Initializing camera, please wait a second and try again",
                Toast.LENGTH_SHORT
            )
                .show()
            initCamera()
            return
        }

        if (hasNoPermissions())
            requestPermissions()
        else {
            val picture = fotoapparat!!.takePicture()
            picture.toBitmap().whenAvailable {
                val bitmap = if (it!!.rotationDegrees != 0) ia.utils.rotateBitmap(
                    it.bitmap,
                    it.rotationDegrees
                ) else it.bitmap
                val detected = ia.detect(bitmap)
                if (detected == null) {
                    //handle no bird state, probably a toast or something like that
                } else {
                    // CHeuquera que teng a internet porque no se va a poder hacer nada sino
                    // ACCESS_NETWORK_STATE permission
                    val recognized = ia.classify(detected)
                    if (recognized != null) {
                        RecognizedBirdDialogFragment(recognized.label, detected, requireActivity()).show(
                            requireActivity().supportFragmentManager,
                            "Bird Recognition Dialog"
                        )
                    }
                }
            }
        }
    }

    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        MaterialAlertDialogBuilder(mContext)
            .setTitle("About permissions")
            .setMessage("Please allow Avedex to use your device's location and camera. We will not share your data with anyone and it will ONLY be stored when you recognize a bird and accept its recognition.")
            .setIcon(R.drawable.ic_logo)
            .setPositiveButton("Continue") { dialog, _ ->
                ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
                dialog.cancel()
            }
            .show()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
    }
}