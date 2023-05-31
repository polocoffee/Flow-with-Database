package com.banklannister.flowwithroomdatabase.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.banklannister.flowwithroomdatabase.utils.Constants.CONTACTS_TABLE

@Entity(tableName = CONTACTS_TABLE)
data class ContactsEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var phone: String = ""
)
