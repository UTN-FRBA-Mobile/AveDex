package com.sophiadiagrams.avedex.presentation.avedex

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentAvedexBinding
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
        _mContext = requireContext()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fb = FirebaseService(Firebase.auth, Firebase.firestore, Firebase.storage)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fb.auth.currentUser
        if (currentUser == null) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        } else {
            fb.db.collection(FirebaseConstants.USERS_COLLECTION).document(currentUser.uid).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        user = it.result.toObject(User::class.java)!!
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initAdapter()
    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(mContext, 3)

        with(binding) {
            rvAvedex.layoutManager = layoutManager
            birdsRVAdapter = BirdsRVAdapter(user.recognizedBirds, user.uid, fb.storage)
            rvAvedex.adapter = birdsRVAdapter
        }

        birdsRVAdapter.onItemClick = {
            val dialogView = view?.findViewById<ConstraintLayout>(R.id.bird_dialog)
            dialogView?.findViewById<TextView>(R.id.tv_birdName)?.text = it.name
            dialogView?.findViewById<TextView>(R.id.tv_description)?.text = "description"
//            Picasso.get().load(it.image).placeholder(R.drawable.ic_bird).into(dialogView?.findViewById(R.id.iv_bird))
            dialogView?.findViewById<TextView>(R.id.tv_location)?.text = it.discoveryLocation
            dialogView?.findViewById<TextView>(R.id.tv_time)?.text = it.discoveryTime

            MaterialAlertDialogBuilder(mContext)
                .setView(dialogView)
                .show()
        }

        user.recognizedBirds.add(Bird("Canario"))
        user.recognizedBirds.add(Bird("Cuervo"))
        user.recognizedBirds.add(Bird("Colibrí"))
        user.recognizedBirds.add(Bird("Paloma"))
        user.recognizedBirds.add(Bird("Murciélago"))
        user.recognizedBirds.add(Bird("Otro pájaro"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))
        user.recognizedBirds.add(Bird("canario"))

        birdsRVAdapter.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {

        with(binding) {
            // Gestures to swipe to camera
            tvHeader.setOnTouchListener(object : OnSwipeTouchListener(mContext) {
                override fun onSwipeDown() {
                    super.onSwipeDown()
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            })
            btnBack.setOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

}