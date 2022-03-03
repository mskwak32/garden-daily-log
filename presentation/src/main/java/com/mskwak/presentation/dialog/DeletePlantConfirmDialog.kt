package com.mskwak.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mskwak.presentation.databinding.DialogDeletePlantConfirmBinding

class DeletePlantConfirmDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDeletePlantConfirmBinding
    var deleteClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeletePlantConfirmBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            dialog = this@DeletePlantConfirmDialog
        }
        return binding.root
    }

    fun onDeleteClick() {
        deleteClickListener?.invoke()
        dismiss()
    }
}