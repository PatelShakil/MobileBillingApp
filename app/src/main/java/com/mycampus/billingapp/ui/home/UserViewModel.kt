package com.mycampus.billingapp.ui.home

import androidx.lifecycle.ViewModel
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    fun saveUserDetails(userDetails: UserDetails) {
        userRepository.saveUserDetails(userDetails)
    }

    fun getUserDetails(): UserDetails? {
        return userRepository.getUserDetails()
    }
}