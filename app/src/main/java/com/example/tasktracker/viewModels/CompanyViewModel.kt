package com.example.tasktracker.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.repositories.CompanyRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.UserRepository
import com.example.tasktracker.services.viewModels.getUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _listMembersCompany = MutableStateFlow(listOf<User>())

    val listMembersCompany = _listMembersCompany.asStateFlow()

    init {
        viewModelScope.launch {
            sharedRepository.userData.collect {
                sharedRepository.setCompanyData(
                    companyRepository.getCurrentCompany(userRepository.get(getUser()))
                )
                Log.d("CompanyViewModel", dataCompany.value.toString())
            }
        }

        viewModelScope.launch {
            sharedRepository.companyData.collect {
                _listMembersCompany.update {
                    companyRepository.getMembersCompany(sharedRepository.companyData.value!!.id)
                }
            }
        }

        viewModelScope.launch {
            sharedRepository.updateUsers.collect {
                _listMembersCompany.update {
                    companyRepository.getMembersCompany(sharedRepository.companyData.value!!.id)
                }
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

    suspend fun setCurrentCompany(companyData: Company) {
        sharedRepository.setCompanyData(companyData)
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

    /** Получить список пользователей в компании */
    suspend fun getMembersCompany(companyId: String) {
        _listMembersCompany.update { companyRepository.getMembersCompany(companyId) }
    }

    suspend fun setCurrentUser(user: User) {
        sharedRepository.setCurrentUser(user)
    }

    /** Обновление имени компании */
    suspend fun updateNameCompany(targetCompany: String, nameCompany: String) {
        companyRepository.updateNameCompany(targetCompany, nameCompany)
    }
}
