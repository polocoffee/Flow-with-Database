package com.banklannister.flowwithroomdatabase.di

import android.content.Context
import androidx.room.Room
import com.banklannister.flowwithroomdatabase.db.ContactsDB
import com.banklannister.flowwithroomdatabase.db.ContactsEntity
import com.banklannister.flowwithroomdatabase.utils.Constants.CONTACTS_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, ContactsDB::class.java, CONTACTS_DATABASE
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: ContactsDB) = db.contactDao()

    @Provides
    @Singleton
    fun provideEntity() = ContactsEntity()

}