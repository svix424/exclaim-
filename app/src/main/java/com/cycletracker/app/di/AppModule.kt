package com.cycletracker.app.di

import android.content.Context
import androidx.room.Room
import com.cycletracker.app.data.db.AppDatabase
import com.cycletracker.app.data.db.dao.DailyEntryDao
import com.cycletracker.app.data.security.DatabaseKeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyManager: DatabaseKeyManager
    ): AppDatabase {
        val passphrase = keyManager.getOrCreatePassphrase()
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "luna_cycle.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDailyEntryDao(db: AppDatabase): DailyEntryDao = db.dailyEntryDao()
}
