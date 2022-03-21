package com.mskwak.presentation.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogSelectMonthBinding
import java.time.LocalDate

class SelectMonthDialog private constructor() : DialogFragment() {
    private lateinit var binding: DialogSelectMonthBinding
    private var completeListener: ((year: Int, month: Int) -> Unit)? = null
    private var year = LocalDate.now().year
    private var month = LocalDate.now().monthValue

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSelectMonthBinding.inflate(requireActivity().layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setPositiveButton(getString(R.string.complete)) { _, _ ->
                completeListener?.invoke(year, month)
                dismiss()
            }
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            setView(binding.root)
        }
        initNumberPicker()
        return builder.create()
    }

    private fun initNumberPicker() {
        binding.yearPicker.apply {
            minValue = 1900
            maxValue = 2100
            wrapSelectorWheel = false
            value = year
            setOnValueChangedListener { _, _, newVal ->
                year = newVal
            }
        }
        binding.monthPicker.apply {
            minValue = 1
            maxValue = 12
            wrapSelectorWheel = true
            value = month
            setOnValueChangedListener { _, _, newVal ->
                month = newVal
            }
        }
    }

    class Builder {
        private val dialog = SelectMonthDialog().apply {
            isCancelable = false
        }

        fun setOnCompleteListener(listener: ((year: Int, month: Int) -> Unit)): Builder {
            dialog.completeListener = listener
            return this
        }

        fun setInitialValue(year: Int, month: Int): Builder {
            dialog.year = year
            dialog.month = month
            return this
        }

        fun show(fragmentManager: FragmentManager) {
            dialog.show(fragmentManager, null)
        }
    }
}