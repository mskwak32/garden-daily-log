package com.mskwak.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogWateringPeriodBinding

class WateringPeriodDialog private constructor() : DialogFragment() {
    private lateinit var binding: DialogWateringPeriodBinding
    private var completeListener: ((days: Int) -> Unit)? = null
    private var days: Int = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogWateringPeriodBinding.inflate(requireActivity().layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setPositiveButton(R.string.complete) { _, _ ->
                completeListener?.invoke(days)
                dismiss()
            }
            setNegativeButton(R.string.cancel) { _, _ ->
                dismiss()
            }
            setView(binding.root)
        }
        initNumberPicker()
        return builder.create()
    }

    private fun initNumberPicker() {
        binding.numberPicker.apply {
            minValue = 0
            maxValue = 400
            wrapSelectorWheel = false
            value = days
            setOnValueChangedListener { _, _, newVal ->
                days = newVal
            }
            this.setFormatter {
                if (it == 0) getString(R.string.none) else it.toString()
            }
        }
    }

    class Builder {
        private val dialog = WateringPeriodDialog().apply {
            isCancelable = false
        }

        fun setOnCompleteListener(listener: (days: Int) -> Unit): Builder {
            dialog.completeListener = listener
            return this
        }

        fun setInitialDays(days: Int): Builder {
            dialog.days = days
            return this
        }

        fun show(fragmentManager: FragmentManager) {
            dialog.show(fragmentManager, null)
        }
    }
}