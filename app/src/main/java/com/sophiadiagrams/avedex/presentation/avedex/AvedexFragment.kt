package com.sophiadiagrams.avedex.presentation.avedex

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private var recognizedBirds: MutableList<Bird> = mutableListOf()
    private lateinit var fb: FirebaseService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvedexBinding.inflate(inflater, container, false)
        _mContext = requireContext()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fb = FirebaseService(Firebase.auth, Firebase.firestore, Firebase.storage)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fb.auth.currentUser
        if (currentUser == null) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        } else {
            fb.db.collection(FirebaseConstants.USERS_COLLECTION).document(currentUser.uid).get()
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        user = it.result.toObject(User::class.java)!!
                        fb.db.collection(FirebaseConstants.BIRDS_COLLECTION)
                            .whereEqualTo("user", user.uid).get()
                            .addOnSuccessListener { documents ->
                                if (documents.size() != 0) {
                                    recognizedBirds.addAll(documents.toObjects(Bird::class.java))
                                    birdsRVAdapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        val layoutManager = GridLayoutManager(mContext, 3)
        with(binding) {
            rvAvedex.layoutManager = layoutManager
            birdsRVAdapter = BirdsRVAdapter(recognizedBirds, user.uid)
            rvAvedex.adapter = birdsRVAdapter
        }
        birdsRVAdapter.onItemClick = {
            BirdDialogFragment(
                it,
                "https://avedex.bepi.tech/birds/${it.name}.jpg"
            ).show(requireActivity().supportFragmentManager, "Bird Dialog")
        }
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