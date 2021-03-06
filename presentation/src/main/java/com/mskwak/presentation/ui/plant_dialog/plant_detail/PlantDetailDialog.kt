package com.mskwak.presentation.ui.plant_dialog.plant_detail

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.mskwak.domain.model.Plant
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogPlantDetailBinding
import com.mskwak.presentation.ui.base.BaseFullScreenDialog
import com.mskwak.presentation.ui.binding.localDateToText
import com.mskwak.presentation.ui.binding.localTimeToText
import com.mskwak.presentation.ui.binding.setImageUri
import com.mskwak.presentation.ui.custom_component.ListItemDecoVertical
import com.mskwak.presentation.ui.dialog.DeleteConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class PlantDetailDialog : BaseFullScreenDialog<DialogPlantDetailBinding>() {
    private val args by navArgs<PlantDetailDialogArgs>()
    private val plantId by lazy { args.plantId }

    override val layoutRes: Int = R.layout.dialog_plant_detail
    private val diaryAdapter by lazy {
        DiarySummaryAdapter {
            openDiaryDetail(it.id)
        }
    }
    private var isWateringFlag = false

    @Inject
    lateinit var viewModelAssistedFactory: PlantDetailViewModel.PlantDetailViewModelAssistedFactory
    private val viewModel by viewModels<PlantDetailViewModel> {
        PlantDetailViewModel.provideFactory(viewModelAssistedFactory, plantId)
    }

    override fun initialize() {
        binding.dialog = this
        binding.viewModel = viewModel

        initToolbar()
        initObserver()
        initRecyclerView()
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
        viewModel.diaries.observe(viewLifecycleOwner) { diaries ->
            diaryAdapter.submitList(diaries)
        }
        viewModel.wateringCompleted.observe(viewLifecycleOwner) {
            binding.waterIcon.setBackgroundResource(R.drawable.ic_water_drop_white)
            binding.waterAnimation.visibility = View.VISIBLE
            binding.waterAnimation.playAnimation()
            isWateringFlag = false
        }
    }

    private fun initRecyclerView() {
        val dividerHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
                .toInt()
        binding.diaryList.apply {
            adapter = diaryAdapter
            addItemDecoration(ListItemDecoVertical(dividerHeight))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupPlant(plant: Plant) {
        binding.apply {
            picture.setImageUri(plant.pictureUri)
            plantName.text = plant.name
            collapsingToolbar.title = plant.name

            val plantDateString =
                plant.createdDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            plantDate.text = "${getString(R.string.plant_date)}: $plantDateString"

            val pair = this@PlantDetailDialog.viewModel.getDdays()
            val dateText = pair.first
            val isDateOver = pair.second
            val today = LocalDate.now()

            binding.wateringDdays.text = dateText

            //????????? ???????????? ?????? ??????????????? ???????????? ????????????
            if (!isWateringFlag) {
                when {
                    isDateOver -> {
                        binding.waterIcon.setBackgroundResource(R.drawable.ic_water_drop_red)
                        binding.waterAnimation.visibility = View.INVISIBLE
                    }
                    plant.lastWateringDate == today -> {
                        binding.waterIcon.setBackgroundResource(R.drawable.ic_water_drop_white)
                        binding.waterAnimation.progress = 1f
                        binding.waterAnimation.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.waterIcon.setBackgroundResource(R.drawable.ic_water_drop_blue)
                        binding.waterAnimation.visibility = View.INVISIBLE
                    }
                }
            }

            //????????? ?????? ?????? ??????
            when (plant.lastWateringDate) {
                today -> {
                    lastWateringDate.text = getText(R.string.today)
                }
                today.minusDays(1) -> {
                    lastWateringDate.text = getText(R.string.yesterday)
                }
                else -> {
                    lastWateringDate.localDateToText(plant.lastWateringDate)
                }
            }

            if (plant.waterPeriod == 0) {        //????????? ?????? ?????? ??????
                wateringAlarm.text = getText(R.string.none)
                wateringAlarmSwitch.visibility = View.GONE
            } else {
                wateringAlarm.localTimeToText(plant.wateringAlarm.time)
                wateringAlarmSwitch.visibility = View.VISIBLE
            }

            binding.wateringAlarmSwitch.isChecked = plant.wateringAlarm.onOff
            memo.text = plant.memo
            binding.emptyDiaryText.text =
                getString(R.string.diary_list_empty_with_plantName, plant.name)
        }
    }

    fun newDiaryClick() {
        val action = PlantDetailDialogDirections.plantDetailToDiaryEdit(plantId, null)
        findNavController().navigate(action)
    }

    fun moreDiaryClick() {
        val action = PlantDetailDialogDirections.plantDetailToDiaryFragment(plantId.toString())
        findNavController().navigate(action)
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
            menuInflater.inflate(R.menu.modify_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        openEditPlant()
                    }
                    R.id.menu_delete -> {
                        showDeleteConfirm()
                    }
                }
                true
            }
            show()
        }
    }

    fun onWateringClick() {
        isWateringFlag = true
        viewModel.watering()
    }

    private fun openDiaryDetail(diaryId: Int) {
        val action = PlantDetailDialogDirections.plantDetailToDiaryDetail(diaryId)
        findNavController().navigate(action)
    }

    private fun showDeleteConfirm() {
        DeleteConfirmDialog().apply {
            deleteClickListener = {
                viewModel.deletePlant()
                this@PlantDetailDialog.dismiss()
            }
        }.show(childFragmentManager, null)
    }

    private fun openEditPlant() {
        val plantId = viewModel.plant.value?.id
        val action = PlantDetailDialogDirections.plantDetailToPlantEdit(plantId.toString())
        findNavController().navigate(action)
    }
}