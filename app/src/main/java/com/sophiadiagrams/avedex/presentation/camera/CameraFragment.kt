package com.sophiadiagrams.avedex.presentation.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
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
import io.fotoapparat.result.PhotoResult
import io.fotoapparat.selector.back
import kotlinx.coroutines.*

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var _mContext: Context? = null
    private val mContext get() = _mContext!!

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private var fotoapparat: Fotoapparat? = null

    private var user = User()
    private lateinit var fb: FirebaseService
    private lateinit var ia: ImageAnalyzerService
    var fotoapparatState: FotoapparatState? = null

    private val analyzePictureJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + analyzePictureJob)

    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var allPermissionsGranted = true
            for (b in result.values) {
                allPermissionsGranted = allPermissionsGranted && b
            }
            if (allPermissionsGranted)
                Navigation.findNavController(binding.root).navigate(R.id.cameraFragment)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        _mContext = requireContext()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fb = FirebaseService(Firebase.auth, Firebase.firestore, Firebase.storage)
    }

    override fun onResume() {
        super.onResume()
        if (!hasNoPermissions() && fotoapparatState == FotoapparatState.OFF) {
            val intent = Intent(mContext, CameraFragment::class.java)
            startActivity(intent)
        }
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
        else {
            initCamera()
            fotoapparatState = FotoapparatState.ON
            initListeners()
        }
    }

    private fun getUser(v: View) {
        fb.db.collection(USERS_COLLECTION).document(fb.auth.currentUser?.uid!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user = it.result.toObject(User::class.java)!!
                    if (user.photoUrl.isNotEmpty()) Picasso.get().load(user.photoUrl)
                        .placeholder(R.drawable.ic_account)
                        .into(v.findViewById(R.id.iv_account) as ImageView)
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        with(binding) {
            // Button to take picture
            fabCamera.setOnClickListener { uiScope.launch(Dispatchers.IO) { takePhoto() } }

            // Account menu button
            ivAccount.setOnClickListener { v: View -> showAccountMenu(v, R.menu.account_menu) }

            // Gestures to swipe to avedex
            root.setOnTouchListener(object : OnSwipeTouchListener(mContext) {
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    Toast.makeText(
                        mContext, "Swipe up to open AveDex", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    Toast.makeText(
                        mContext, "Swipe up to open AveDex", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSwipeUp() {
                    super.onSwipeUp()
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_camera_to_avedex)
                }

                override fun onSwipeDown() {
                    super.onSwipeDown()
                    Toast.makeText(mContext, "Swipe up to open AveDex", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun initCamera() {
        fotoapparat = Fotoapparat(context = mContext,
            view = binding.cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            logger = loggers(
                logcat()
            ),
            cameraErrorCallback = { error ->
                println("Recorder errors: $error")
            })
    }

    private fun showAccountMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(mContext, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.getItem(0).title = user.displayName
        popup.menu.getItem(1).setOnMenuItemClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_camera_to_avedex)
            true
        }
        popup.menu.getItem(2).setOnMenuItemClickListener {
            fb.auth.signOut()
            activity?.onBackPressedDispatcher?.onBackPressed()
            true
        }

        popup.show()
    }

    private suspend fun takePhoto() {
        if (fotoapparat == null) {
            Toast.makeText(
                mContext,
                "Initializing camera, please wait a second and try again",
                Toast.LENGTH_LONG
            ).show()
            initCamera()
            return
        }

        if (hasNoPermissions()) requestPermissions()
        else {
            val picture = fotoapparat!!.takePicture()
            analyzePhoto(picture)
        }
    }

    private suspend fun recognitionFlow(b: Bitmap) {
        coroutineScope {
            val birdRecognized = ia.classify(b)
            withContext(Dispatchers.Main) {
                if (birdRecognized == null) {
                    MaterialAlertDialogBuilder(mContext).setTitle("Whoops")
                        .setIcon(R.drawable.ic_bird)
                        .setMessage("Our AI was unable to recognize the species of the bird on the photo you took.")
                        .setPositiveButton("Ok", null).show()
                } else {
                    RecognizedBirdDialogFragment(
                        birdRecognized, b, requireActivity()
                    ).show(
                        requireActivity().supportFragmentManager, "Bird Recognition Dialog"
                    )
                }
            }
        }
    }

    private suspend fun analyzePhoto(picture: PhotoResult) {
        val it = picture.toBitmap().await()

        val bitmap = if (it.rotationDegrees != 0) ia.utils.rotateBitmap(
            it.bitmap, it.rotationDegrees
        ) else it.bitmap
        val detected = ia.detect(bitmap)

        withContext(Dispatchers.Main) {
            Toast.makeText(
                mContext,
                "Your photo is being processed. Please wait...",
                Toast.LENGTH_LONG
            ).show()
            if (detected == null) {
                MaterialAlertDialogBuilder(mContext).setTitle("No bird found")
                    .setIcon(R.drawable.ic_bird).setMessage(
                        "Our AI was unable to find any bird on the photo you took." + "If you think this is a mistake you can continue but there is no guarantee " + "that the species recognition will work properly."
                    ).setPositiveButton("Take another picture") { dialog, _ -> dialog.cancel() }
                    .setNegativeButton("Continue anyways") { _, _ ->
                        uiScope.launch(Dispatchers.IO) {
                            recognitionFlow(bitmap)
                        }
                    }.show()
            } else {
                uiScope.launch(Dispatchers.IO) {
                    recognitionFlow(detected)
                }
            }
        }
    }

    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.INTERNET
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        MaterialAlertDialogBuilder(mContext).setTitle("About permissions")
            .setMessage("Please allow Avedex to use your device's location and camera. We will not share your data with anyone and it will ONLY be stored when you recognize a bird and accept its recognition.")
            .setIcon(R.drawable.ic_logo).setPositiveButton("Continue") { dialog, _ ->
                activityResultLauncher.launch(permissions)
                dialog.cancel()
            }.show()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
        fotoapparatState = FotoapparatState.OFF
    }
}

enum class FotoapparatState {
    ON, OFF
}