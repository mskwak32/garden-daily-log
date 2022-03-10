package com.mskwak.presentation.plant.edit_plant

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.mskwak.presentation.R
import com.mskwak.presentation.base.BaseFullScreenDialog
import com.mskwak.presentation.databinding.DialogEditPlantBinding
import com.mskwak.presentation.dialog.SelectPhotoDialog
import com.mskwak.presentation.dialog.WateringPeriodDialog
import com.mskwak.presentation.util.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class EditPlantDialog(private val plantId: Int?) : BaseFullScreenDialog<DialogEditPlantBinding>() {
    override val layoutRes: Int = R.layout.dialog_edit_plant
    private val viewModel by viewModels<EditPlantViewModel>()

    override fun initialize() {
        binding.viewModel = viewModel
        binding.dialog = this

        plantId?.let {
            viewModel.loadPlant(it)
        }
        viewModel.wateringAlarmOnOff.observe(viewLifecycleOwner) {
            binding.wateringAlarmSwitch.isChecked = it
        }
        viewModel.onSavedEvent.observe(viewLifecycleOwner) {
            dismiss()
        }
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarMessage, Snackbar.LENGTH_SHORT)
    }

    fun onPhotoClick() {
        SelectPhotoDialog().apply {
            imageBitmapListener = { bitmap ->
                binding.picture.setImageBitmap(bitmap)
                viewModel.setNewPicture(bitmap)
            }
        }.show(childFragmentManager, null)
    }

    fun onPlantDateClick() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val date = LocalDate.of(y, m.plus(1), d)
            viewModel.plantDate.value = date
        }
        openDatePicker(viewModel.plantDate.value!!, listener)
    }

    fun onLastWateringDateClick() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val date = LocalDate.of(y, m.plus(1), d)
            viewModel.lastWateringDate.value = date
        }
        openDatePicker(viewModel.lastWateringDate.value!!, listener)
    }

    fun onWateringPeriodClick() {
        WateringPeriodDialog.Builder()
            .setInitialDays(viewModel.wateringPeriod.value!!)
            .setOnCompleteListener { days ->
                viewModel.wateringPeriod.value = days
            }.show(childFragmentManager)
    }

    fun onWateringAlarmClick() {
        val listener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            val time = LocalTime.of(h, m)
            viewModel.wateringAlarmTime.value = time
        }
        val currentTime = viewModel.wateringAlarmTime.value!!
        val hour = currentTime.hour
        val minute = currentTime.minute
        TimePickerDialog(requireContext(), listener, hour, minute, false)
            .show()
    }

    private fun openDatePicker(
        currentDate: LocalDate,
        listener: DatePickerDialog.OnDateSetListener
    ) {
        val year = currentDate.year
        val month = currentDate.month.ordinal
        val day = currentDate.dayOfMonth
        DatePickerDialog(requireContext(), listener, year, month, day)
            .show()
    }

    fun onCloseClick() {
        dismiss()
    }

}