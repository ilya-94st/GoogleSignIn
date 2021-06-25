package com.example.staselovich_p2.Registration


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.staselovich_p2.Arrays.ArrayFragmentDirections
import com.example.staselovich_p2.BaceFragment.BaceFragment
import com.example.staselovich_p2.R
import com.example.staselovich_p2.databinding.FragmentRegistrationBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

const val RC_SIGN_IN = 0
const val RQ_FIREBASE_AUTH = 1

class RegistrationFragment : BaceFragment<FragmentRegistrationBinding>() {

    lateinit var mGoogleSignInClient: GoogleSignInClient //гугл Апи
    lateinit var auth: FirebaseAuth
    lateinit var shared: SharedPreferences
    var isremembered: Boolean = false
    private lateinit var mRegistrationViewModel: RegistrationViewModel


    override fun getBinding() = R.layout.fragment_registration

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        mRegistrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java) // инстенс viewModel чтобы вызвоать методы
        // получить гугловский клиент
        mGoogleSignInClient = mRegistrationViewModel.createRequest(
            getString(R.string.default_web_client_id),
            requireActivity()
        )
        connectAuthenticate()
        anim()
        shared = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        binding.signInButton.setOnClickListener {
            signIn()
            toast("You loged ${auth.currentUser?.email}")
        }
        binding.loginButton.setOnClickListener {
            val checked: Boolean = binding.checkBox.isChecked
            val editor: SharedPreferences.Editor = shared.edit()
            editor.putBoolean("Boo", checked)
            editor.apply()
            val direction =
                RegistrationFragmentDirections.actionRegistrationFragmentToArrayFragment()
            findNavController().navigate(direction)
        }
    }

    private fun anim() {
        val fallinAnimationM = AnimationUtils.loadAnimation(context, R.anim.falling)
        binding.imageM.startAnimation(fallinAnimationM)
        val fallingAnimationButton = AnimationUtils.loadAnimation(context, R.anim.falling)
        binding.signInButton.startAnimation(fallingAnimationButton)
    }

    private fun signIn() {
        startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        isremembered = shared.getBoolean("Boo", false)
        if (isremembered) {
            findNavController().navigate(R.id.action_registrationFragment_to_arrayFragment)
        }
    }

    fun connectAuthenticate() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            )
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RQ_FIREBASE_AUTH
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                mRegistrationViewModel.firebaseAuthWithGoogle(account.idToken!!, auth)
            } catch (e: ApiException) {
                println("/////////////////$e")
            }
        }
    }
}