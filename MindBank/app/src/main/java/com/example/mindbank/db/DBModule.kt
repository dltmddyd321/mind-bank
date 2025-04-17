package com.example.mindbank.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    ): DatastoreRepo = DatastoreRepoImpl(context)

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE save_model ADD COLUMN link TEXT")
        }
    }

    @Singleton
    @Provides
    fun provideSaveDatabase(
        @ApplicationContext context: Context
    ): MemoDatabase =
        Room.databaseBuilder(context, MemoDatabase::class.java, "memo.db").addMigrations(
            MIGRATION_1_2
        ).build()

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