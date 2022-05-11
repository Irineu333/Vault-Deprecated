package com.neo.vault.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neo.vault.domain.model.Currency
import com.neo.vault.domain.model.Type
import com.neo.vault.domain.model.Vault

@Entity(tableName = "vault_tb")
data class VaultEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "dateToBreak")
    val dateToBreak: Long? = null,
    @ColumnInfo(name = "currency")
    val currency: Currency,
    @ColumnInfo(name = "type")
    val type: Type
)

fun VaultEntity.toModel() = Vault(
    name = name,
    dateToBreak =dateToBreak,
    currency =currency,
    type = type
)