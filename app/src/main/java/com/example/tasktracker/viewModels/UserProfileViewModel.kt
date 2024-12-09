package com.example.tasktracker.viewModels

import androidx.lifecycle.ViewModel
import com.example.tasktracker.repositories.SharedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel
@Inject
constructor(
    private val sharedRepository: SharedRepository,
) : ViewModel() {

    val userData = sharedRepository.userData
}
