package com.sophiadiagrams.avedex.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sophiadiagrams.avedex.R
import com.sophiadiagrams.avedex.databinding.FragmentLoginBinding
import com.sophiadiagrams.avedex.lib.models.User
import com.sophiadiagrams.avedex.lib.services.FirebaseService
import com.sophiadiagrams.avedex.lib.util.FirebaseConstants


class LoginFragment : Fragment() {

    companion object {
        const val GOOGLE_SIGN_IN = 1903
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var fb: FirebaseService
    private lateinit var googleSignInClient: GoogleSignInClient

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
        fb = FirebaseService(Firebase.auth, Firebase.firestore, Firebase.storage)
        googleSignInClient = GoogleSignIn.getClient(requireContext(), getGSO())
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fb.auth.currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
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
        handleLoading(true)

        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        fb.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    handleLoading(false)
                    Log.d("LOGIN", "signInWithEmail:success")
                    findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
                } else {
                    handleLoading(false)
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun handleGoogleLogin() {
        handleLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                btnLogin.text = ""
                btnGoogleLogin.text = ""
                btnLogin.isEnabled = false
                btnGoogleLogin.isEnabled = false
                pbLogin.visibility = View.VISIBLE
                pbGoogleLogin.visibility = View.VISIBLE
            } else {
                pbLogin.visibility = View.GONE
                pbGoogleLogin.visibility = View.GONE
                btnLogin.text = getString(R.string.login__login_button)
                btnGoogleLogin.text = getString(R.string.login__login_with_google_button)
                btnLogin.isEnabled = true
                btnGoogleLogin.isEnabled = true
            }
        }
    }

    private fun getGSO(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                fb.auth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity()) {
                        if (it.isSuccessful) {
                            Log.d("LOGIN", "signInWithEmail:success")
                            val user = User(
                                uid = it.result.user?.uid ?: "",
                                displayName = it.result.user?.displayName ?: "",
                                email = it.result.user?.email ?: "",
                                photoUrl = it.result.user?.photoUrl.toString()
                            )
                            fb.db.collection(FirebaseConstants.USERS_COLLECTION).document(user.uid)
                                .set(user, SetOptions.merge())
                            handleLoading(false)
                            findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
                        } else {
                            handleLoading(false)
                            throw Exception(it.exception)
                        }
                    }
            } catch (e: ApiException) {
                handleLoading(false)
                Log.d("LOGIN", "Google Login error: $e")
                Toast.makeText(
                    context, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}