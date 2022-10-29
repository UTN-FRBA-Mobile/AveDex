package com.sophiadiagrams.avedex.presentation.login

import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentLoginBinding
import com.sophiadiagrams.avedex.lib.services.FirebaseService


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var fb: FirebaseService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fb = FirebaseService(Firebase.auth, Firebase.firestore)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fb.auth.currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
            Log.d("LOGIN", "user: ${currentUser.email}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLogin.setOnClickListener { handleLogin() }
            btnGoogleLogin.setOnClickListener { handleGoogleLogin() }
            btnSignUp.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_signUpFragment) }
            btnPasswordRecovery.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_passwordRecoveryFragment) }
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        fb.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LOGIN", "signInWithEmail:success")
                    findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
                } else {
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun handleGoogleLogin() {
        val a = requireActivity()
        val oneTapClient = Identity.getSignInClient(a)

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("712370312423-2ie9esvtbu9sdhujt140mkhpvba3dvee.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(a) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, 1,
                        null, 0, 0, 0, null
                    )
                } catch (e: SendIntentException) {
                    Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(a) { e ->

                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d("TAG", e.localizedMessage)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                try {
                    val a = requireActivity()
                    val oneTapClient = Identity.getSignInClient(a)
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            Log.d("TAG", "Got ID token.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("TAG", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}