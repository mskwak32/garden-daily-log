package com.mskwak.presentation.setting

import androidx.fragment.app.viewModels
import com.mskwak.presentation.R
import com.mskwak.presentation.base.BaseFragment
import com.mskwak.presentation.databinding.FragmentSettingBinding
import com.mskwak.presentation.dialog.NotReadyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    override val layoutRes: Int = R.layout.fragment_setting
    private val viewModel by viewModels<SettingViewModel>()

    override fun initialize() {
        binding.fragment = this
        initVersion()
    }

    private fun initVersion() {
        binding.version.text = viewModel.getAppVersionName(requireContext())
    }

    fun onUpdateBreakdownClick() {
        showNotReadyDialog()
    }

    fun onSuggestClick() {
        showNotReadyDialog()
    }

    fun onEstimateClick() {
        showNotReadyDialog()
    }

    private fun showNotReadyDialog() {
        NotReadyDialog().show(childFragmentManager, null)
    }
}