package com.windrr.mindbank.util.alarm

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AlarmModule {
    @Provides
    fun provideAlarmRepository(
        @ApplicationContext context: Context
    ): AlarmRepository = AlarmRepositoryImpl(context)
}

