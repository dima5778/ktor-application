package com.example.directoryapplication.di

import com.example.directoryapplication.data.repository.EmployeeRepositoryImpl
import com.example.directoryapplication.domain.repository.EmployeeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmployeeRepository(
        impl: EmployeeRepositoryImpl
    ): EmployeeRepository
}