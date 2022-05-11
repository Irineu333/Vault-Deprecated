package com.neo.vault.presentation.ui.feature.createVault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neo.vault.R
import com.neo.vault.databinding.FragmentCreateVaultBinding

class CreateVaultBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateVaultBinding? = null
    private val binding get() = _binding!!

    private val navHostFragment
        get() = childFragmentManager
            .findFragmentById(R.id.create_vault_host) as NavHostFragment

    private val navController
        get() = navHostFragment.navController

    private val NavDestination.isInitialFragment
        get() = id == R.id.chooseTypeFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateVaultBinding.inflate(
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
    }

    private fun setupView() = with(binding) {

        btnToBack.setOnClickListener {
            navController.popBackStack()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupListeners() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.btnToBack.isVisible = !destination.isInitialFragment
            binding.tvTitle.text = destination.label
        }
    }
}