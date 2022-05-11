package com.neo.vault.presentation.ui.feature.createVault.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.vault.domain.model.Currency
import com.neo.vault.domain.repository.VaultsRepository
import com.neo.vault.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateVaultViewModel @Inject constructor(
    private val vaultsRepository: VaultsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateVaultUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CreateVaultUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    val dateToBreak get() = uiState.value.dateToBreak

    fun setDateToBreak(
        timeInMillis: Long
    ) {
        _uiState.update {
            it.copy(
                dateToBreak = Calendar.getInstance().apply {
                    this.timeInMillis = timeInMillis
                }
            )
        }
    }

    fun createPiggyBank(
        name: String,
        currency: Currency,
        dateToBreak: Long? = this.dateToBreak?.timeInMillis
    ) = viewModelScope.launch {

        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        val success = vaultsRepository.createPiggyBank(
            name = name,
            currency = currency,
            dateToBreak = dateToBreak
        )

        _uiState.update {
            it.copy(
                isLoading = false
            )
        }

        if (success) {
            _uiEffect.emit(CreateVaultUiEffect.Success)
        } else {
            _uiEffect.emit(
                CreateVaultUiEffect.Message(
                    UiText.Raw("Erro ao tentar criar cofre")
                )
            )
        }
    }

    suspend fun hasVaultWithName(name: String): Boolean {
        return vaultsRepository.getVaultByName(name) != null
    }
}