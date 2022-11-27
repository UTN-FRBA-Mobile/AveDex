package com.sophiadiagrams.avedex.presentation.camera

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentRecognizedBirdDialogBinding
import com.sophiadiagrams.avedex.lib.models.User
import com.sophiadiagrams.avedex.lib.services.FirebaseService
import com.sophiadiagrams.avedex.lib.services.location.LocationService
import com.sophiadiagrams.avedex.lib.services.retrofit.BirdsResponse
import com.sophiadiagrams.avedex.lib.util.FirebaseConstants
import com.squareup.picasso.Picasso

class RecognizedBirdDialogFragment(
    private val bird: BirdsResponse, private val birdPicture: Bitmap, private val activity: Activity
) : DialogFragment() {

    private var _binding: FragmentRecognizedBirdDialogBinding? = null
    private val binding get() = _binding!!

    private var user = User()
    private lateinit var fb: FirebaseService
    private lateinit var l: LocationService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fb = FirebaseService(Firebase.auth, Firebase.firestore, Firebase.storage)
        l = LocationService(LocationServices.getFusedLocationProviderClient(activity))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecognizedBirdDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
        populateDialog()
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            btnYes.setOnClickListener {
                handleAcceptRecognition()
            }

            btnNo.setOnClickListener {
                Toast.makeText(
                    context,
                    "Thanks for your feedback, we will use it to improve our AI",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleAcceptRecognition() {
        //crear doc card
        val loc = l.getFullLocation(this.requireContext())
    }

    private fun populateDialog() {
        with(binding) {
            tvTitle.text = bird.name
            ivBird.setImageBitmap(birdPicture)
            Picasso.get().load("https://avedex.bepi.tech/${bird.url}")
                .placeholder(R.drawable.ic_bird).into(ivRecognizedBird)
            tvDescription.text = bird.description
        }
    }

    private fun getUser() {
        fb.db.collection(FirebaseConstants.USERS_COLLECTION).document(fb.auth.currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    user = it.result.toObject(User::class.java)!!
                }
            }
    }
}