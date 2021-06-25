package com.example.staselovich_p2.Arrays

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.room.Room
import com.example.staselovich_p2.*
import com.example.staselovich_p2.BaceFragment.BaceFragment
import com.example.staselovich_p2.DataBase.User
import com.example.staselovich_p2.DataBase.UserDataBase
import com.example.staselovich_p2.DataBase.ViewModel
import com.example.staselovich_p2.databinding.FragmentArrayBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.gmail.Gmail
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

const val RQ_FIREBASE_AUTH =1
class ArrayFragment : BaceFragment<FragmentArrayBinding>() {
    lateinit var userViewModel: ViewModel
    private lateinit var mArrayViewModel: ArrayViewModel
    lateinit var credential: GoogleAccountCredential
    lateinit var service: Gmail
    lateinit var shared: SharedPreferences


    override fun getBinding() = R.layout.fragment_array
    @SuppressLint("CommitPrefEdits")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this).get(com.example.staselovich_p2.DataBase.ViewModel::class.java)
        mArrayViewModel= ViewModelProvider(this).get(ArrayViewModel::class.java)
        checkPermision()
        connectAuthenticate()
        deleteAll()
        shared = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        credential = mArrayViewModel.getCredential(requireActivity())
        service = mArrayViewModel.getServise(credential)
        val messagesDatabase = Room.databaseBuilder(requireContext(), UserDataBase::class.java, "messages_table")
                .allowMainThreadQueries()
                .build()

        val d: List<User>? = messagesDatabase.userDao().getAllMessages()

        val adapter = d?.let { CastomRecucler(it, requireContext(), service, mArrayViewModel) }

if(isOnline(requireContext())) {
    CoroutineScope(Dispatchers.Main).launch {
        binding.progressBar.visibility = View.VISIBLE
        mArrayViewModel.readEmail(service, userViewModel)
        binding.progressBar.visibility = View.INVISIBLE
        adapter?.notifyDataSetChanged()

    }
} else {
toast("No internet")
}

        binding.recucler.adapter = adapter
        binding.recucler.addItemDecoration(DividerItemDecoration(binding.recucler.context,DividerItemDecoration.VERTICAL))
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        binding.buttonExit.setOnClickListener {
            val editor : SharedPreferences.Editor? = shared.edit()
            editor?.clear()
            editor?.apply()
            UserMessagesModelClass.dataObject.clear()
            userViewModel.deleteAllUser()
            FirebaseAuth.getInstance().signOut()
            val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            googleSignInClient.signOut()
            userViewModel.deleteAllUser()
            val direction = ArrayFragmentDirections.actionArrayFragmentToRegistrationFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserMessagesModelClass.dataObject.clear()
    }

    private fun connectAuthenticate() {
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
   fun deleteAll() {
       binding.deleteButton.setOnClickListener {
           userViewModel.deleteAllUser()
       }
   }
    fun checkPermision() {
        // доступ к файлам на телефоне
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,

                    ),
                1
            )
        }
    }
    @SuppressLint("ServiceCast")
    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }
}