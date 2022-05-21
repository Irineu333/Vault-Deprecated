package com.neo.vault.data.repository

import com.neo.vault.core.Resource
import com.neo.vault.data.local.dao.VaultDao
import com.neo.vault.data.local.entity.VaultEntity
import com.neo.vault.data.local.entity.toModel
import com.neo.vault.domain.model.CurrencyCompat
import com.neo.vault.domain.model.Vault
import com.neo.vault.domain.repository.VaultsRepository
import com.neo.vault.util.extension.toRaw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VaultsRepositoryIml @Inject constructor(
    private val vaultDao: VaultDao
) : VaultsRepository {

    override suspend fun createPiggyBank(
        name: String,
        currency: CurrencyCompat,
        dateToBreak: Long?
    ) = runCatching {

        if (getVaultByName(name) != null) {
            return@runCatching Resource.Error(
                "Já existe um cofre com esse nome".toRaw()
            )
        }

        withContext(Dispatchers.IO) {
            vaultDao.insertVault(
                VaultEntity(
                    name = name,
                    currency = currency,
                    dateToBreak = dateToBreak,
                    type = Vault.Type.PIGGY_BANK
                )
            )
        }

        Resource.Success(Unit)
    }.getOrElse {
        Resource.Error("${it.message}".toRaw())
    }

    override suspend fun getVaultByName(name: String): Vault? {
        return withContext(Dispatchers.IO) {
            vaultDao.findByName(
                name = name
            )
        }?.toModel()
    }

    override suspend fun loadPiggyBanks(): List<Vault> {
        return withContext(Dispatchers.IO) {
            vaultDao.loadVaultsByType(type = Vault.Type.PIGGY_BANK)
        }.toModel()
    }
}