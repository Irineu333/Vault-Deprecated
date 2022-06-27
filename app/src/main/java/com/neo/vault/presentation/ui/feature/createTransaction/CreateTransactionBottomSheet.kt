package com.neo.vault.presentation.ui.feature.createTransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neo.vault.R
import com.neo.vault.databinding.FragmentTransactionBinding
import com.neo.vault.presentation.ui.component.Item
import com.neo.vault.presentation.ui.feature.createTransaction.viewModel.TransactionUiEffect
import com.neo.vault.presentation.ui.feature.createTransaction.viewModel.TransactionViewModel
import com.neo.vault.utils.VibratorCompat
import com.neo.vault.utils.extension.behavior
import com.neo.vault.utils.extension.dpToPx
import com.neo.vault.utils.extension.expanded
import com.neo.vault.utils.extension.showSnackbar
import com.neo.vault.utils.extension.toRaw
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CreateTransactionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels()

    private val vibrator by lazy {
        VibratorCompat.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(
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

        behavior?.expanded()
        behavior?.isDraggable = false
        behavior?.isHideable = false

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val itemSpace = Item.Space(
            16.dpToPx().toInt()
        )

        binding.btnWithdraw.setOnClickListener {
            viewModel.toWithdrawTransaction()
        }

        binding.btnSave.setOnClickListener {
            viewModel.toDepositTransaction()
        }

        binding.keyboard.setKeys(
            Item.Group.vertical(
                Item.Group.horizontal(
                    Item.Key(
                        "1"
                    ) {
                        insertNumber(1)
                        vibrate()
                    },
                    Item.Key(
                        "2"
                    ) {
                        insertNumber(2)
                        vibrate()
                    },
                    Item.Key(
                        "3"
                    ) {
                        insertNumber(3)
                        vibrate()
                    },
                    itemSpace,
                    Item.Key(
                        "+"
                    ) {
                        viewModel.insertPlus()
                        vibrate()
                    },
                ),
                Item.Group.horizontal(
                    Item.Key(
                        "4"
                    ) {
                        insertNumber(4)
                        vibrate()
                    },
                    Item.Key(
                        "5"
                    ) {
                        insertNumber(5)
                        vibrate()
                    },
                    Item.Key(
                        "6"
                    ) {
                        insertNumber(6)
                        vibrate()
                    },
                    itemSpace,
                    Item.Key(
                        "-"
                    ) {
                        viewModel.insertMinus()
                        vibrate()
                    },
                ),
                Item.Group.horizontal(
                    Item.Key(
                        "7"
                    ) {
                        insertNumber(7)
                        vibrate()
                    },
                    Item.Key(
                        "8"
                    ) {
                        insertNumber(8)
                        vibrate()
                    },
                    Item.Key(
                        "9"
                    ) {
                        insertNumber(9)
                        vibrate()
                    },
                    itemSpace,
                    Item.Key(
                        "*"
                    ) {
                        viewModel.insertTimes()
                        vibrate()
                    }
                ),
                Item.Group.horizontal(
                    Item.Key(
                        R.drawable.ic_backspace,
                        onClick = {
                            viewModel.backSpace()
                            vibrate()
                        },
                        onLongClick = {
                            viewModel.clearAll()
                            vibrate()
                        }
                    ),
                    Item.Key(
                        "0"
                    ) {
                        insertNumber(0)
                        vibrate()
                    },
                    Item.Key(
                        "="
                    ) {
                        viewModel.toResolve()
                        vibrate()
                    },
                    itemSpace,
                    Item.Key(
                        "/"
                    ) {
                        viewModel.insertDivider()
                        vibrate()
                    }
                )
            ),
        )
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.etValuePortrait?.apply {
                            setText(state.formatted())
                            setSelection(length())
                        }
                        binding.etValueLand?.apply {
                            setText(state.formatted(true))
                            setSelection(length())
                        }
                    }
                }

                launch {
                    viewModel.uiEffect.collect { effect ->
                        val message = when (effect) {
                            is TransactionUiEffect.Error.InvalidOperation ->
                                "Operação inválida".toRaw()
                            is TransactionUiEffect.Error.Notice ->
                                effect.message
                        }

                        binding.showSnackbar(message)
                    }
                }
            }
        }
    }

    private fun insertNumber(number: Int) {
        viewModel.insertNumber(
            number = number
        )
    }

    private fun vibrate() {
        VibratorCompat.vibrate(vibrator, 60)
    }
}