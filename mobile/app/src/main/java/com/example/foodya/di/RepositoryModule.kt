package com.example.foodya.di

import com.example.foodya.data.repository.AuthRepositoryImpl
import com.example.foodya.data.repository.MerchantRepositoryImpl
import com.example.foodya.data.repository.RestaurantRepositoryImpl
import com.example.foodya.domain.repository.AuthRepository
import com.example.foodya.domain.repository.MerchantRepository
import com.example.foodya.domain.repository.RestaurantRepository
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
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        impl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Binds
    @Singleton
    abstract fun bindMerchantRepository(
        impl: MerchantRepositoryImpl
    ): MerchantRepository
}