package com.example.tasktracker.sources

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanySources @Inject constructor() {
    private val companiesReference = FirebaseDatabase.getInstance().getReference("Сompanies")

    val companySource
        get() = companiesReference
}
