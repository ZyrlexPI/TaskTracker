package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.CompanyRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.UserRepository
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class CompanyViewModel
@Inject
constructor(
    private val sharedRepository: SharedRepository,
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
) : ViewModel() {

    val dataCompany = sharedRepository.companyData

    init {
        viewModelScope.launch {
            sharedRepository.userData.collect {
                sharedRepository.setCompanyData(
                    companyRepository.getCurrentCompany(userRepository.get(getUser()))
                )
            }
        }
    }

    /** Создание новой организации */
    suspend fun add(nameCompany: String, userData: User) {
        companyRepository.add(nameCompany, userData)
    }

    /** Получение информации о компании текущего пользователя, если он в ней состоит */
    suspend fun getCurrentCompany(userData: User) {
        sharedRepository.setCompanyData(companyRepository.getCurrentCompany(userData))
    }

    /** Получить список компаний существующих в БД */
    suspend fun getListCompany(): MutableList<Company> {
        return companyRepository.getListCompany()
    }

    /** Присоединение пользователя к существующей компании */
    suspend fun joinCompany(targetCompany: String, userData: User) {
        companyRepository.joinCompany(targetCompany, userData)
    }

    /** Удаление текущего пользователя из организации в которой состоит */
    suspend fun deleteCurrentUser(userData: User) {
        companyRepository.deleteCurrentUser(userData)
    }
}
