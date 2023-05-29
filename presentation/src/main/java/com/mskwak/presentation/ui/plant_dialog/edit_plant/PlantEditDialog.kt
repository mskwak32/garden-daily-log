package com.mskwak.presentation.ui.plant_dialog.edit_plant

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogPlantEditBinding
import com.mskwak.presentation.ui.base.BaseFullScreenDialog
import com.mskwak.presentation.ui.binding.localTimeToText
import com.mskwak.presentation.ui.dialog.SelectPhotoDialog
import com.mskwak.presentation.ui.dialog.WateringPeriodDialog
import com.mskwak.presentation.util.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class PlantEditDialog : BaseFullScreenDialog<DialogPlantEditBinding>() {
    override val layoutRes: Int = R.layout.dialog_plant_edit
    private val viewModel by viewModels<PlantEditViewModel>()
    private val args by navArgs<PlantEditDialogArgs>()

    @SuppressLint("ClickableViewAccessibility")
    override fun initialize() {
        binding.viewModel = viewModel
        binding.dialog = this

        args.plantId?.toInt()?.let {
            viewModel.loadPlant(it)
        }
        viewModel.wateringAlarmOnOff.observe(viewLifecycleOwner) {
            binding.wateringAlarmSwitch.isChecked = it
        }
        viewModel.onSavedEvent.observe(viewLifecycleOwner) {
            dismiss()
        }
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarMessage, Snackbar.LENGTH_SHORT)

        //빈공간 터치시 키보드 내리기
        binding.contentRoot.setOnTouchListener { _, _ ->
            hideSoftKeyboard()
            return@setOnTouchListener false
        }
    }

    fun onPhotoClick() {
        SelectPhotoDialog().apply {
            imageBitmapListener = { bitmap ->
                binding.picture.setImageBitmap(bitmap)
                viewModel.setNewPicture(bitmap)
            }
        }.show(childFragmentManager, null)
        hideSoftKeyboard()
    }

    fun onPlantDateClick() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val date = LocalDate.of(y, m.plus(1), d)
            viewModel.setPlantDate(date)
        }
        openDatePicker(viewModel.plantDate.value!!, listener)
        hideSoftKeyboard()
    }

    fun onLastWateringDateClick() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val date = LocalDate.of(y, m.plus(1), d)
            viewModel.setLastWateringDate(date)
        }
        openDatePicker(viewModel.lastWateringDate.value!!, listener)
    }

    fun onWateringPeriodClick() {
        WateringPeriodDialog.Builder()
            .setInitialDays(viewModel.wateringPeriod.value!!)
            .setOnCompleteListener { days ->
                viewModel.setWateringPeriod(days)
                if (days == 0) {
                    wateringAlarmEnable(false)
                } else {
                    wateringAlarmEnable(true)
                }
            }.show(childFragmentManager)
    }

    private fun wateringAlarmEnable(enable: Boolean) {
        binding.wateringAlarmSwitch.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        binding.wateringAlarm.isClickable = enable

        if (enable) {
            binding.wateringAlarm.localTimeToText(viewModel.wateringAlarmTime.value)
        } else {
            binding.wateringAlarm.text = getString(R.string.none)
        }
    }

    fun onWateringAlarmClick() {
        val listener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            val time = LocalTime.of(h, m)
            viewModel.setWateringAlarmTime(time)
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