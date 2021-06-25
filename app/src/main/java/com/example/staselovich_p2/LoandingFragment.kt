package com.example.staselovich_p2

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.staselovich_p2.BaceFragment.BaceFragment
import com.example.staselovich_p2.databinding.FragmentLoandingBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoandingFragment : BaceFragment<FragmentLoandingBinding>() {
    override fun getBinding() = R.layout.fragment_loanding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.animate().translationY(-2500f).setDuration(1500).setStartDelay(5000)
        binding.loteri.animate().translationY(2000f).setDuration(1500).setStartDelay(5000)
        GlobalScope.launch {
            delay(6500)
            getActivity()?.runOnUiThread {
                val direction = LoandingFragmentDirections.actionLoandingFragmentToRegistrationFragment()
                findNavController().navigate(direction)
            }
        }
    }
}

