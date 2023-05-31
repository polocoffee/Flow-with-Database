package com.banklannister.flowwithroomdatabase.repository

import com.banklannister.flowwithroomdatabase.db.ContactsDao
import com.banklannister.flowwithroomdatabase.db.ContactsEntity
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val dao: ContactsDao
) {
    suspend fun saveContacts(entity: ContactsEntity) = dao.saveContacts(entity)

    fun getAllContacts() = dao.getAllContacts()

    fun deleteAllContacts() = dao.deleteAllContacts()

    fun sortASC() = dao.sortASC()

    fun sortDESC() = dao.sortDESC()

    fun searchContacts(name: String) = dao.searchContacts(name)

    suspend fun updateContacts(entity: ContactsEntity) = dao.updateContacts(entity)

    suspend fun deleteContacts(entity: ContactsEntity) = dao.deleteContacts(entity)

    fun getContacts(id: Int) = dao.getContacts(id)


}