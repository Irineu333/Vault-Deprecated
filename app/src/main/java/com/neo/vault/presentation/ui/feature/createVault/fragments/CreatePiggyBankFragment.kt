package com.neo.vault.presentation.ui.feature.createVault.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import com.neo.vault.R
import com.neo.vault.databinding.FragmentCreatePiggyBankBinding
import com.neo.vault.domain.model.CurrencySupport
import com.neo.vault.presentation.model.UiText
import com.neo.vault.presentation.ui.feature.createVault.CreateVaultBottomSheet
import com.neo.vault.presentation.ui.feature.createVault.viewModel.CreateVaultUiEffect
import com.neo.vault.presentation.ui.feature.createVault.viewModel.CreateVaultViewModel
import com.neo.vault.util.TimeUtils
import com.neo.vault.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePiggyBankFragment : Fragment() {

    private var _binding: FragmentCreatePiggyBankBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateVaultViewModel by viewModels()

    private val createVaultBottomSheet
        get() = parentFragment?.parentFragment as? CreateVaultBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePiggyBankBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListeners()
        setupObservers()
    }

    private fun setupView() {
        binding.tieName.requestFocus()
        setupCurrency()
    }

    private fun setupListeners() {
        binding.btnDateToBreak.setOnClickListener {
            showDataPicker()
        }

        binding.btnCreateVault.setOnClickListener {
            it.isEnabled = false
            createVault()
        }

        binding.tilName.addValidationListener(
            validation = { value ->
                when {
                    value.isBlank() -> {
                        ValidationResult.IsInvalid(
                            message = "Nome do cofre não pode ser vazio"
                        )
                    }

                    viewModel.hasVaultWithName(value) -> {
                        ValidationResult.IsInvalid(
                            message = "Já existe um cofre com esse nome"
                        )
                    }

                    else -> ValidationResult.IsValid
                }
            },
            isValid = {

                error = null
                isErrorEnabled = false

                boxStrokeColor = Color.GREEN
                hintTextColor = ColorStateList.valueOf(Color.GREEN)

                binding.btnCreateVault.isEnabled = true
            },
            isInvalid = { textError ->

                error = textError
                isErrorEnabled = true

                binding.btnCreateVault.isEnabled = false
            }
        )
    }

    private fun setupObservers() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED
        ) {
            launch {
                viewModel.uiState.collect {
                    binding.btnDateToBreak.text =
                        it.dateToBreak?.formatted ?: UNDEFINED_DATE_TEXT
                }
            }

            launch {
                viewModel.uiEffect.collectLatest {
                    when (it) {
                        CreateVaultUiEffect.Success -> {
                            binding.showSnackbar(
                                message =  "Success".toRaw()
                            )
                            createVaultBottomSheet?.dismiss()
                        }
                        CreateVaultUiEffect.Error -> {
                            binding.btnCreateVault.isEnabled = true
                            binding.showSnackbar(
                                message = "Erro ao criar cofre".toRaw()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupCurrency() = with(binding.cgCurrency) {

        if (checkedChipId != View.NO_ID) return@with

        when (CurrencySupport.default()) {
            CurrencySupport.BRL -> {
                check(R.id.chip_brl)
            }

            CurrencySupport.USD -> {
                check(R.id.chip_usd)
            }

            CurrencySupport.EUR -> {
                check(R.id.chip_eur)
            }
        }
    }

    private fun createVault() {
        viewModel.createPiggyBank(
            name = binding.tilName.editText!!.text.toString(),
            currency = getCurrency()
        )
    }

    private fun getCurrency(): CurrencySupport {
        return when (binding.cgCurrency.checkedChipId) {
            R.id.chip_brl -> {
                CurrencySupport.BRL
            }

            R.id.chip_usd -> {
                CurrencySupport.USD
            }
            R.id.chip_eur -> {
                CurrencySupport.EUR
            }

            else -> {
                throw IllegalStateException("invalid currency")
            }
        }
    }

    private fun showDataPicker() {
        MaterialDatePicker.Builder
            .datePicker()
            .setSelection(viewModel.dateToBreak?.time)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build().also { datePicker ->
                datePicker.addOnPositiveButtonClickListener { utcTimeMillis ->
                    viewModel.setDateToBreak(TimeUtils.utcToLocal(utcTimeMillis))
                }
                datePicker.show(
                    parentFragmentManager,
                    DATA_PICKER_TAG
                )
            }
    }

    companion object {
        const val UNDEFINED_DATE_TEXT = "Indefinido"
        const val DATA_PICKER_TAG = "DATA_PICKER"
    }

}