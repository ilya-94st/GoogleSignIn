package com.example.staselovich_p2.BaceFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.staselovich_p2.Tools.hideKeyboard
import com.example.staselovich_p2.Tools.showAlertDialog
import com.example.staselovich_p2.Tools.toast

abstract class BaceFragment<B: ViewBinding> : Fragment() {
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getBinding(),container, false)
        return binding.root

    }
    fun toast(message: String) {
        requireContext().toast(message)
    }

    fun keyBoard() {
        view?.let{
            activity?.hideKeyboard(it)
        }
    }

    fun showAlertDialog(title: String, message: String){
      requireContext().showAlertDialog(title, message)
    }


    abstract fun getBinding(): Int

}