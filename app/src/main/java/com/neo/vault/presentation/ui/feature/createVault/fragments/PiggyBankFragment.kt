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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.neo.vault.R
import com.neo.vault.databinding.FragmentPiggyBankBinding
import com.neo.vault.domain.model.Currency
import com.neo.vault.presentation.ui.feature.createVault.viewModel.CreateVaultViewModel
import com.neo.vault.util.CurrencyUtil
import com.neo.vault.util.extension.ValidationResult
import com.neo.vault.util.extension.addValidationListener
import com.neo.vault.util.extension.formatted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PiggyBankFragment : Fragment() {

    private var _binding: FragmentPiggyBankBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateVaultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPiggyBankBinding.inflate(
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
        setupCurrency()
    }

    private fun setupListeners() {
        binding.btnDateToBreak.setOnClickListener {
            showDataPicker {
                addOnPositiveButtonClickListener {
                    viewModel.setDateToBreak(it)
                }
            }
        }

        binding.btnCreateVault.setOnClickListener {
            createVault()
        }

        binding.tilName.addValidationListener(
            validation = { value ->
                when {
                    value.isBlank() -> {
                        ValidationResult.isInvalid(
                            message = "Nome do cofre não pode ser vazio"
                        )
                    }

                    viewModel.hasVaultWithName(value) -> {
                        ValidationResult.isInvalid(
                            message = "Já existe um cofre com esse nome"
                        )
                    }

                    else -> ValidationResult.isValid
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
        viewModel.dateToBreak.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).collect {
            binding.btnDateToBreak.text = it?.formatted ?: UNDEFINED_DATE_TEXT
        }
    }

    private fun setupCurrency() = with(binding.cgCurrency) {

        if (checkedChipId != View.NO_ID) return@with

        when (CurrencyUtil.currency.currencyCode) {
            "BRL" -> {
                check(R.id.chip_brl)
            }

            "USD" -> {
                check(R.id.chip_usd)
            }

            "EUR" -> {
                check(R.id.chip_eur)
            }
            else -> {
                check(R.id.chip_usd)
            }
        }
    }

    private fun createVault() {
        viewModel.createPiggyBank(
            name = binding.tilName.editText!!.toString(),
            currency = getCurrency()
        )
    }

    private fun getCurrency(): Currency {
        return when (binding.cgCurrency.checkedChipId) {
            R.id.chip_brl -> {
                Currency.BRL
            }

            R.id.chip_usd -> {
                Currency.USD
            }
            R.id.chip_eur -> {
                Currency.EUR
            }

            else -> {
                throw IllegalStateException("invalid currency")
            }
        }
    }

    private fun showDataPicker(config: MaterialDatePicker<Long>.() -> Unit) {
        MaterialDatePicker.Builder
            .datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build().also { datePicker ->
                config(datePicker)
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