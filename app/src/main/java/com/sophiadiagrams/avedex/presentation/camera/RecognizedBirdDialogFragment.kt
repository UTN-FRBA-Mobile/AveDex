package com.sophiadiagrams.avedex.presentation.camera

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
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
import kotlinx.coroutines.*
import java.util.*

class RecognizedBirdDialogFragment(
    private val bird: BirdsResponse, private val birdPicture: Bitmap, private val activity: Activity
) : DialogFragment() {

    private var _binding: FragmentRecognizedBirdDialogBinding? = null
    private val binding get() = _binding!!

    private var user = User()
    private lateinit var fb: FirebaseService
    private lateinit var l: LocationService

    private val analyzePictureJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + analyzePictureJob)

    private var act: Activity? = null

    override fun onAttach( context: Context) {
        super.onAttach(context)
        act = if (context is Activity) context else null
    }

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
                uiScope.launch(Dispatchers.IO) {
                    handleAcceptRecognition()
                }
            }

            btnNo.setOnClickListener {
                Toast.makeText(
                    context,
                    "Thanks for your feedback, we will use it to improve our AI",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }

    private suspend fun handleAcceptRecognition() {
        var alreadyOnAvedex = false
        coroutineScope {
            val document = hashMapOf(
                "user" to user.uid,
                "name" to bird.name,
                "discoveryTime" to SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date()),
                "description" to bird.description
            )
            val documentReference = fb.db.collection("birds").document()
            fb.db.collection(FirebaseConstants.BIRDS_COLLECTION)
                .whereEqualTo("user", user.uid).whereEqualTo("name", bird.name).get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        documentReference.set(document)
                            .addOnSuccessListener {
                                Log.d("FB", "Bird successfully written in the db!")
                                l.updateLocation(act!!.applicationContext, documentReference)
                            }
                            .addOnFailureListener { e -> Log.w("FB", "Error writing document", e) }
                    } else alreadyOnAvedex = true
                }

        }.addOnCompleteListener {
            if (alreadyOnAvedex)
                Toast.makeText(
                    context,
                    "The bird is already on your Avedex",
                    Toast.LENGTH_LONG
                ).show()
            else
                Toast.makeText(
                    context,
                    "The recognized bird was added to you AveDex",
                    Toast.LENGTH_LONG
                ).show()
            dismiss()
        }

    }

    private fun populateDialog() {
        with(binding) {
            tvTitle.text = bird.name
            ivBird.setImageBitmap(birdPicture)
            Picasso.get().load("https://avedex.bepi.tech/${bird.url}")
                .placeholder(R.drawable.ic_bird).into(ivRecognizedBird)
            // tvDescription.text = bird.description
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