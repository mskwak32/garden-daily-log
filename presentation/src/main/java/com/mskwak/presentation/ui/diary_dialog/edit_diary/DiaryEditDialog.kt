package com.mskwak.presentation.ui.diary_dialog.edit_diary

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.util.TypedValue
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogDiaryEditBinding
import com.mskwak.presentation.ui.base.BaseFullScreenDialog
import com.mskwak.presentation.ui.custom_component.ListItemDecoHorizontal
import com.mskwak.presentation.ui.dialog.SelectPhotoDialog
import com.mskwak.presentation.util.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class DiaryEditDialog(private val plantId: Int, private val diaryId: Int?) :
    BaseFullScreenDialog<DialogDiaryEditBinding>() {

    override val layoutRes: Int = R.layout.dialog_diary_edit
    private val viewModel by viewModels<DiaryEditViewModel>()
    private val pictureAdapter by lazy { PictureListAdapter(viewModel) }

    override fun initialize() {
        binding.viewModel = viewModel
        binding.dialog = this

        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarMessage, Snackbar.LENGTH_SHORT)
        initRecyclerView()
        initObserver()

        viewModel.loadPlantName(plantId)
        diaryId?.let {
            viewModel.loadDiary(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        viewModel.pictureList.observe(viewLifecycleOwner) {
            pictureAdapter.submitList(it)
        }
        viewModel.onSavedEvent.observe(viewLifecycleOwner) {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        val dividerWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                .toInt()
        binding.pictureListView.apply {
            adapter = pictureAdapter
            addItemDecoration(ListItemDecoHorizontal(dividerWidth))
        }
    }

    fun onNewPictureClick() {
        if (viewModel.isPictureFull()) return

        SelectPhotoDialog().apply {
            imageBitmapListener = { bitmap ->
                viewModel.saveNewPicture(bitmap)
            }
        }.show(childFragmentManager, null)
    }

    fun onDateClick() {
        val year = viewModel.diaryDate.value!!.year
        val month = viewModel.diaryDate.value!!.month.ordinal
        val day = viewModel.diaryDate.value!!.dayOfMonth
        DatePickerDialog(requireContext(), { _, y, m, d ->
            viewModel.diaryDate.value = LocalDate.of(y, m.plus(1), d)
        }, year, month, day).show()
    }

    fun onCloseClick() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.deleteTempFiles()
    }

}