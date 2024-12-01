package com.example.tasktracker.services.firebase

import androidx.lifecycle.ViewModel
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.CompanyRepository
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class CompanyViewModel
@Inject
constructor(
    private val companyRepository: CompanyRepository,
) : ViewModel() {
    private val _dataCompany = MutableStateFlow(Company())
    val dataCompany = _dataCompany.asStateFlow()

    /** Создание новой организации */
    suspend fun add(nameCompany: String, userData: User) {
        companyRepository.add(nameCompany, userData)
    }

    /** Получение информации о компании текущего пользователя, если он в ней состоит */
    suspend fun getCurrentCompany(userData: User) {
        _dataCompany.update { companyRepository.getCurrentCompany(userData) }
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
