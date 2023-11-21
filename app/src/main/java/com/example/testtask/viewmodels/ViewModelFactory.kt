package com.example.testtask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testtask.viewmodels.PexelsViewModel

class ViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PexelsViewModel::class.java)){
            return PexelsViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}