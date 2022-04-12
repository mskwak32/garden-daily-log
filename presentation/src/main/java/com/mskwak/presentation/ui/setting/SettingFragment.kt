package com.mskwak.presentation.ui.setting

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.FragmentSettingBinding
import com.mskwak.presentation.ui.base.BaseFragment
import com.mskwak.presentation.ui.dialog.NotReadyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    override val layoutRes: Int = R.layout.fragment_setting
    private val viewModel by viewModels<SettingViewModel>()

    override fun initialize() {
        binding.fragment = this
        initVersion()
        viewModel.loadLatestAppVersion(requireContext())
        viewModel.loadUpdateContent(requireContext())

        viewModel.hasAppUpdate.observe(viewLifecycleOwner) {
            binding.updateButton.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun initVersion() {
        binding.version.text = viewModel.getAppVersionName(requireContext())
    }

    fun onUpdateContentClick() {
        if (viewModel.updateContent.isNullOrEmpty()) {
            showNotReadyDialog()
        } else {
            showUpdateContentDialog()
        }
    }

    private fun showNotReadyDialog() {
        NotReadyDialog().show(childFragmentManager, null)
    }

    private fun showUpdateContentDialog() {
        val versionName = viewModel.getAppVersionName(requireContext())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.setting_update_content_title, versionName))
            .setMessage(viewModel.updateContent)
            .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun showMarket() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
        startActivity(intent)
    }
}