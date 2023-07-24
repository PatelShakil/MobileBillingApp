package com.mycampus.billingapp.di

import android.content.Context
import android.content.SharedPreferences
import com.mycampus.billingapp.data.bluetooth.AndroidBluetoothController
import com.mycampus.billingapp.data.repo.UserRepository
import com.mycampus.billingapp.domain.bluetooth.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideUserRepository(sharedPreferences: SharedPreferences): UserRepository {
        return UserRepository(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }
}