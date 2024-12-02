package com.example.tasktracker

import com.example.tasktracker.repositories.AuthRepository
import com.example.tasktracker.repositories.CompanyRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.TasksRepository
import com.example.tasktracker.repositories.UserRepository
import com.example.tasktracker.sources.AuthSources
import com.example.tasktracker.sources.CompanySources
import com.example.tasktracker.sources.TasksSources
import com.example.tasktracker.sources.UserSources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthSources(): AuthSources {
        return AuthSources()
    }

    @Provides
    @Singleton
    fun provideUserSources(): UserSources {
        return UserSources()
    }

    @Provides
    @Singleton
    fun provideTasksSources(): TasksSources {
        return TasksSources()
    }

    @Provides
    @Singleton
    fun provideCompanySources(): CompanySources {
        return CompanySources()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(provideAuthSources())
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository(provideUserSources(), provideCompanySources())
    }

    @Provides
    @Singleton
    fun provideTasksRepository(): TasksRepository {
        return TasksRepository(provideTasksSources(), provideCompanySources(), provideUserSources())
    }

    @Provides
    @Singleton
    fun provideCompanyRepository(): CompanyRepository {
        return CompanyRepository(provideCompanySources(), provideUserSources())
    }

    @Provides
    @Singleton
    fun provideSharedRepository(): SharedRepository {
        return SharedRepository(provideTasksRepository())
    }
}