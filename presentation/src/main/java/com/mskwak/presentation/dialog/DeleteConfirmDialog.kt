package com.mskwak.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mskwak.presentation.databinding.DialogDeleteConfirmBinding

class DeleteConfirmDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDeleteConfirmBinding
    var deleteClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteConfirmBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            dialog = this@DeleteConfirmDialog
        }
        return binding.root
    }

    fun onDeleteClick() {
        deleteClickListener?.invoke()
        dismiss()
    }
}