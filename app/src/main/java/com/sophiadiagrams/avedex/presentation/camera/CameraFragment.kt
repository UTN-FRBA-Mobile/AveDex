package com.sophiadiagrams.avedex.presentation.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentCameraBinding
import com.sophiadiagrams.avedex.lib.models.User
import com.sophiadiagrams.avedex.lib.services.FirebaseService
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
        android.Manifest.permission.CAMERA,
    )
    private var fotoapparat: Fotoapparat? = null

    private var user = User()
    private lateinit var fb: FirebaseService

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
        fb = FirebaseService(Firebase.auth, Firebase.firestore)
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

        getUser(view)
        initCamera()
        initListeners()
    }

    private fun getUser(v: View) {
        fb.db.collection(USERS_COLLECTION).document(fb.auth.currentUser?.uid!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user = it.result.toObject(User::class.java)!!
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
        if (hasNoPermissions()) {
            requestPermission()
        }

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
        if (hasNoPermissions()) {
            requestPermission()
        } else {
            fotoapparat
                ?.takePicture()
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_camera_to_birdRecognized)
        }
    }

    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(permissions, 0)
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
    }
}