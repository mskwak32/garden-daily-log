package com.mskwak.presentation.plant_dialog.plant_detail

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.mskwak.domain.model.Plant
import com.mskwak.presentation.R
import com.mskwak.presentation.base.BaseFullScreenDialog
import com.mskwak.presentation.binding.localDateToText
import com.mskwak.presentation.binding.localTimeToText
import com.mskwak.presentation.binding.setUri
import com.mskwak.presentation.custom_component.ListItemDecoVertical
import com.mskwak.presentation.databinding.DialogPlantDetailBinding
import com.mskwak.presentation.dialog.DeleteConfirmDialog
import com.mskwak.presentation.diary_dialog.diary_detail.DiaryDetailDialog
import com.mskwak.presentation.diary_dialog.edit_diary.DiaryEditDialog
import com.mskwak.presentation.home.HomeFragmentDirections
import com.mskwak.presentation.plant_dialog.edit_plant.PlantEditDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class PlantDetailDialog(private val plantId: Int) :
    BaseFullScreenDialog<DialogPlantDetailBinding>() {

    override val layoutRes: Int = R.layout.dialog_plant_detail
    private val diaryAdapter by lazy { DiarySummaryAdapter(viewModel) }

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
            binding.waterIcon.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.white, null))
            binding.waterAnimation.visibility = View.VISIBLE
            binding.waterAnimation.playAnimation()
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
        diaryAdapter.onItemClickListener = {
            openDiaryDetail(it.id)
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

            //마지막 물준 날짜 세팅
            val today = LocalDate.now()
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

            if (plant.waterPeriod == 0) {        //물주기 간격 설정 없음
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
        DiaryEditDialog(plantId, null).show(childFragmentManager, null)
    }

    fun moreDiaryClick() {
        val action = HomeFragmentDirections.homeToDiary(plantId.toString())
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

    private fun openDiaryDetail(diaryId: Int) {
        DiaryDetailDialog(diaryId).show(childFragmentManager, null)
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
        PlantEditDialog(viewModel.plant.value?.id).show(childFragmentManager, null)
    }
}