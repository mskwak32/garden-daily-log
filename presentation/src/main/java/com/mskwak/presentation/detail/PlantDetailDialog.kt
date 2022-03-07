package com.mskwak.presentation.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.mskwak.domain.model.Plant
import com.mskwak.presentation.R
import com.mskwak.presentation.binding.localDateToText
import com.mskwak.presentation.binding.localTimeToText
import com.mskwak.presentation.binding.setUri
import com.mskwak.presentation.databinding.DialogPlantDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class PlantDetailDialog : DialogFragment() {
    private lateinit var binding: DialogPlantDetailBinding
    private val args by navArgs<PlantDetailDialogArgs>()

    @Inject
    lateinit var viewModelAssistedFactory: PlantDetailViewModel.PlantDetailViewModelAssistedFactory
    private val viewModel by viewModels<PlantDetailViewModel> {
        PlantDetailViewModel.provideFactory(viewModelAssistedFactory, args.plantId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.fullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPlantDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            dialog = this@PlantDetailDialog
            viewModel = this@PlantDetailDialog.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initObserver()
    }

    private fun initToolbar() {
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {       //collapsed
                binding.collapsingToolbar.isTitleEnabled = true
                binding.toolbar.setBackgroundResource(R.color.white)
                binding.backButton.setBackgroundResource(R.drawable.ic_arrow_back_black)
                binding.menuButton.setBackgroundResource(R.drawable.ic_more_horiz_black)
            } else {
                binding.collapsingToolbar.isTitleEnabled = false
                binding.toolbar.setBackgroundResource(R.color.transparent)
                binding.backButton.setBackgroundResource(R.drawable.ic_arrow_back_white)
                binding.menuButton.setBackgroundResource(R.drawable.ic_more_horiz_white)
            }
        })
    }

    private fun initObserver() {
        viewModel.plant.observe(viewLifecycleOwner) { plant ->
            setupPlant(plant)
        }
        viewModel.isEmptyRecords.observe(viewLifecycleOwner) { isEmpty ->
            binding.emptyDiaryView.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.moreDiaryButton.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
        viewModel.records.observe(viewLifecycleOwner) { records ->
            //TODO
        }
        viewModel.wateringCompleted.observe(viewLifecycleOwner) {
            //TODO 물주기완료 애니메이션
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupPlant(plant: Plant) {
        binding.apply {
            picture.setUri(plant.pictureUri)
            plantName.text = plant.name
            collapsingToolbar.title = plant.name
            val dateString =
                plant.createdDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            plantDate.text = "${getString(R.string.plant_date)}: $dateString"
            wateringDdays.text = viewModel?.getDdays() ?: ""
            lastWateringDate.localDateToText(plant.lastWateringDate)
            wateringAlarm.localTimeToText(plant.wateringAlarm.time)
            binding.wateringAlarmSwitch.isChecked = plant.wateringAlarm.onOff
            memo.text = plant.memo
            binding.emptyDiaryText.text = getString(R.string.diary_list_empty, plant.name)
        }
    }

    fun newDiaryClick() {

    }

    fun moreDiaryClick() {

    }

    fun onBackClick() {
        dismiss()
    }

    fun onMenuClick() {
        PopupMenu(
            requireContext(),
            binding.menuButton,
            Gravity.END,
            0,
            R.style.popupMenuStyle
        ).run {
            menuInflater.inflate(R.menu.plant_detail_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_editPlant -> {

                    }
                    R.id.menu_deletePlant -> {

                    }
                }
                true
            }
            show()
        }
    }
}