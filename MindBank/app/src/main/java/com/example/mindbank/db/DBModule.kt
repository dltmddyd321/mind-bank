package com.example.mindbank.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DBModule {

    @Singleton
    @Provides
    fun providesDataStoreRepo(
        @ApplicationContext context: Context
    ) : DatastoreRepo = DatastoreRepoImpl(context)

    @Singleton
    @Provides
    fun provideSaveDatabase(
        @ApplicationContext context: Context
    ): MemoDatabase = Room.databaseBuilder(context, MemoDatabase::class.java, "memo.db").build()

    @Singleton
    @Provides
    fun provideSaveDataDao(memoDatabase: MemoDatabase): SaveDataDao = memoDatabase.saveDataDao()

    @Singleton
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext context: Context
    ): TodoDatabase = Room.databaseBuilder(context, TodoDatabase::class.java, "todo.db").build()

    @Singleton
    @Provides
    fun provideTodoDao(todoDatabase: TodoDatabase): TodoDao = todoDatabase.todoDao()
}