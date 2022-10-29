package com.sophiadiagrams.avedex.presentation.avedex

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentAvedexBinding
import com.sophiadiagrams.avedex.databinding.FragmentCameraBinding
import com.sophiadiagrams.avedex.lib.models.Bird
import com.sophiadiagrams.avedex.lib.models.User
import com.sophiadiagrams.avedex.lib.services.FirebaseService
import com.sophiadiagrams.avedex.lib.util.FirebaseConstants
import com.sophiadiagrams.avedex.presentation.util.OnSwipeTouchListener

class AvedexFragment : Fragment() {

    private var _binding: FragmentAvedexBinding? = null
    private val binding get() = _binding!!
    private var _mContext: Context? = null
    private val mContext get() = _mContext!!
    private lateinit var birdsRVAdapter: BirdsRVAdapter
    private var user = User()
    private lateinit var fb: FirebaseService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvedexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fb = FirebaseService(Firebase.auth, Firebase.firestore)

        _mContext = requireContext()
        val layoutManager = GridLayoutManager(context, 3)

        with(binding){
            rvAvedex.layoutManager = layoutManager
            birdsRVAdapter = BirdsRVAdapter(user.recognizedBirds, mContext)
            rvAvedex.adapter = birdsRVAdapter
        }
        val imageLink =
            "https://firebasestorage.googleapis.com/v0/b/avedex-1915b.appspot.com/o/028.jpg?alt=media&token=5a28992c-5ec8-4d98-b167-90d211a72f0f"

        user.recognizedBirds.add(Bird("Canario", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("Cuervo", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("Colibrí", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("Paloma", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("Murciélago", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("Otro pájaro", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("canario", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("canario", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("canario", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("canario", imageLink, "Una pajarito sensual"))
        user.recognizedBirds.add(Bird("canario", imageLink, "Una pajarito sensual"))

        birdsRVAdapter.notifyDataSetChanged()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fb.auth.currentUser
        if (currentUser == null) {
            findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)//TODO: corregir accion
        } else {
            fb.db.collection(FirebaseConstants.USERS_COLLECTION).document(currentUser.uid).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        user = it.result.toObject(User::class.java)!!
                    }
                }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        // Gestures to swipe to camera
        binding.tvAvedexHeader.setOnTouchListener(object : OnSwipeTouchListener(mContext) {
            override fun onSwipeDown() {
                super.onSwipeDown()
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        })
    }

}