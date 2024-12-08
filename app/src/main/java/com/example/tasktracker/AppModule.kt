package com.example.tasktracker

import com.example.tasktracker.repositories.AuthRepository
import com.example.tasktracker.repositories.CommentsRepository
import com.example.tasktracker.repositories.CompanyRepository
import com.example.tasktracker.repositories.NotificationsRepository
import com.example.tasktracker.repositories.SharedRepository
import com.example.tasktracker.repositories.TasksRepository
import com.example.tasktracker.repositories.UserRepository
import com.example.tasktracker.sources.AuthSources
import com.example.tasktracker.sources.CommentsSources
import com.example.tasktracker.sources.CompanySources
import com.example.tasktracker.sources.NotificationsSource
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
    fun provideCommentsSources(): CommentsSources {
        return CommentsSources()
    }

    @Provides
    @Singleton
    fun provideNotificationsSource(): NotificationsSource {
        return NotificationsSource()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(provideAuthSources())
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository(
            provideUserSources(),
            provideCompanySources(),
            provideCommentsSources(),
            provideTasksSources(),
        )
    }

    @Provides
    @Singleton
    fun provideTasksRepository(): TasksRepository {
        return TasksRepository(
            provideTasksSources(),
            provideCompanySources(),
            provideUserSources(),
            provideCommentsSources()
        )
    }

    @Provides
    @Singleton
    fun provideCompanyRepository(): CompanyRepository {
        return CompanyRepository(provideCompanySources(), provideUserSources())
    }

    @Provides
    @Singleton
    fun provideCommentsRepository(): CommentsRepository {
        return CommentsRepository(provideCommentsSources())
    }

    @Provides
    @Singleton
    fun provideNotificationsRepository(): NotificationsRepository {
        return NotificationsRepository(provideNotificationsSource())
    }

    @Provides
    @Singleton
    fun provideSharedRepository(): SharedRepository {
        return SharedRepository(provideNotificationsRepository())
    }
}
