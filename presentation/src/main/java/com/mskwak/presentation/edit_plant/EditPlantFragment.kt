package com.mskwak.presentation.edit_plant

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mskwak.presentation.R
import com.mskwak.presentation.base.BaseFragment
import com.mskwak.presentation.databinding.FragmentEditPlantBinding
import com.mskwak.presentation.dialog.SelectPhotoDialog
import com.mskwak.presentation.dialog.WateringPeriodDialog
import com.mskwak.presentation.util.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class EditPlantFragment : BaseFragment<FragmentEditPlantBinding>() {
    override val layoutRes: Int = R.layout.fragment_edit_plant
    private val viewModel by viewModels<EditPlantViewModel>()
    private val args by navArgs<EditPlantFragmentArgs>()

    override fun initView() {
        binding?.viewModel = viewModel
    }

    override fun initialize() {
        args.plantId?.let {
            viewModel.loadPlant(it.toInt())
        }
        viewModel.photoClickEvent.observe(viewLifecycleOwner) {
            openPhotoSelect()
        }
        viewModel.plantDateClickEvent.observe(viewLifecycleOwner) {
            openPlantDateDialog()
        }
        viewModel.lastWateringClickEvent.observe(viewLifecycleOwner) {
            openLastWateringDialog()
        }
        viewModel.wateringPeriodClickEvent.observe(viewLifecycleOwner) {
            openWateringPeriodDialog()
        }
        viewModel.wateringAlarmOnOff.observe(viewLifecycleOwner) {
            binding?.wateringAlarmSwitch?.isChecked = it
        }
        viewModel.wateringAlarmClickEvent.observe(viewLifecycleOwner) {
            openWateringAlarmDialog()
        }
        viewModel.onSavedEvent.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarMessage, Snackbar.LENGTH_SHORT)
    }

    private fun openPhotoSelect() {
        SelectPhotoDialog().apply {
            imageBitmapListener = { bitmap ->
                binding?.picture?.setImageBitmap(bitmap)
                viewModel.setNewPicture(bitmap)
            }
        }.show(childFragmentManager, null)
    }

    private fun openPlantDateDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val date = LocalDate.of(y, m.plus(1), d)
            viewModel.plantDate.value = date
        }
        openDatePicker(viewModel.plantDate.value!!, listener)
    }

    private fun openLastWateringDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val date = LocalDate.of(y, m.plus(1), d)
            viewModel.lastWateringDate.value = date
        }
        openDatePicker(viewModel.lastWateringDate.value!!, listener)
    }

    private fun openWateringPeriodDialog() {
        WateringPeriodDialog.Builder()
            .setInitialDays(viewModel.wateringPeriod.value!!)
            .setOnCompleteListener { days ->
                viewModel.wateringPeriod.value = days
            }.show(childFragmentManager)
    }

    private fun openWateringAlarmDialog() {
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

}