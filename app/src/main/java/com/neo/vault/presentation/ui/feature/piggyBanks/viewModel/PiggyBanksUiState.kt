package com.neo.vault.presentation.ui.feature.piggyBanks.viewModel

import com.neo.vault.domain.model.Vault
import com.neo.vault.presentation.model.Summation

data class PiggyBanksUiState(
    val toBreakPiggyBanks : List<Vault> = emptyList(),
    val joiningPiggyBanks : List<Vault> = emptyList(),
    val summation: List<Summation> = emptyList()
)