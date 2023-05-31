package com.banklannister.flowwithroomdatabase.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banklannister.flowwithroomdatabase.utils.Constants.CONTACTS_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContacts(contactsEntity: ContactsEntity)

    @Query("SELECT * FROM  $CONTACTS_TABLE")
    fun getAllContacts(): Flow<MutableList<ContactsEntity>>

    @Query("DELETE FROM $CONTACTS_TABLE")
    fun deleteAllContacts()

    @Query("SELECT * FROM $CONTACTS_TABLE ORDER BY name ASC")
    fun sortASC(): Flow<MutableList<ContactsEntity>>

    @Query("SELECT * FROM $CONTACTS_TABLE ORDER BY name DESC")
    fun sortDESC(): Flow<MutableList<ContactsEntity>>

    @Query("SELECT * FROM $CONTACTS_TABLE WHERE name LIKE '%' || :name || '%' ")
    fun searchContacts(name: String): Flow<MutableList<ContactsEntity>>

    @Update
    suspend fun updateContacts(entity: ContactsEntity)

    @Delete
    suspend fun deleteContacts(entity: ContactsEntity)

    @Query("SELECT * FROM $CONTACTS_TABLE WHERE id == :id")
    fun getContacts(id: Int): Flow<ContactsEntity>
}