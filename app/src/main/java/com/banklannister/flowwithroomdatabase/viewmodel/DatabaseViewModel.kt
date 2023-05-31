package com.banklannister.flowwithroomdatabase.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banklannister.flowwithroomdatabase.db.ContactsEntity
import com.banklannister.flowwithroomdatabase.repository.DatabaseRepository
import com.banklannister.flowwithroomdatabase.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _contactList = MutableLiveData<DataStatus<List<ContactsEntity>>>()
    val contactList: LiveData<DataStatus<List<ContactsEntity>>>
        get() = _contactList

    private val _contactsDetail = MutableLiveData<DataStatus<ContactsEntity>>()
    val contactsDetail: LiveData<DataStatus<ContactsEntity>>
        get() = _contactsDetail

    fun saveContact(entity: ContactsEntity, isEdited: Boolean) = viewModelScope.launch {
        if (isEdited) {
            repository.updateContacts(entity)
        } else {
            repository.saveContacts(entity)
        }
    }

    fun getAllContact() = viewModelScope.launch {
        repository.getAllContacts()
            .catch { _contactList.postValue(DataStatus.error(it.message.toString())) }
            .collect { _contactList.postValue(DataStatus.success(it, it.isEmpty())) }
    }

    fun deleteAllContacts() = viewModelScope.launch {
        repository.deleteAllContacts()
    }


    fun sortASC() = viewModelScope.launch {
        repository.sortASC()
            .catch { _contactList.postValue(DataStatus.error(it.message.toString())) }
            .collect { _contactList.postValue(DataStatus.success(it, it.isEmpty())) }
    }


    fun sortDESC() = viewModelScope.launch {
        repository.sortDESC()
            .catch { _contactList.postValue(DataStatus.error(it.message.toString())) }
            .collect { _contactList.postValue(DataStatus.success(it, it.isEmpty())) }
    }


    fun searchContacts(name: String) = viewModelScope.launch {
        repository.searchContacts(name)
            .collect { _contactList.postValue(DataStatus.success(it, it.isEmpty())) }
    }

    fun deleteContacts(entity: ContactsEntity) = viewModelScope.launch {
        repository.deleteContacts(entity)
    }

    fun getContacts(id: Int) = viewModelScope.launch {
        repository.getContacts(id).collect {
            _contactsDetail.postValue(DataStatus.success(it, false))
        }
    }

}