package com.mskwak.presentation.ui.dialog_plant.plant_detail

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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
    private val viewModel by viewModels<PlantDetailViewModel>()

    override fun initialize() {
        binding.dialog = this
        binding.viewModel = viewModel

        initToolbar()
        initObserver()
        initRecyclerView()
        viewModel.loadData(args.plantId)
    }

    private fun initToolbar() {
        binding.layoutAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {       //collapsed
                binding.collapsingToolbar.isTitleEnabled = true
                binding.toolbar.setBackgroundResource(R.color.white)
                binding.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_black)
                binding.ivMenu.setBackgroundResource(R.drawable.ic_more_horiz_black)
            } else {
                binding.collapsingToolbar.isTitleEnabled = false
                binding.toolbar.setBackgroundResource(R.color.transparent)
                binding.ivBack.setBackgroundResource(R.drawable.ic_arrow_back_white)
                binding.ivMenu.setBackgroundResource(R.drawable.ic_more_horiz_white)
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
            binding.ivWaterIcon.setBackgroundResource(R.drawable.ic_water_drop_white)
            binding.animWater.visibility = View.VISIBLE
            binding.animWater.playAnimation()
            isWateringFlag = false
        }
    }

    private fun initRecyclerView() {
        val dividerHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
                .toInt()
        binding.rvDiaryList.apply {
            adapter = diaryAdapter
            addItemDecoration(ListItemDecoVertical(dividerHeight))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupPlant(plant: Plant) {
        binding.apply {
            ivPicture.setImageUri(plant.pictureUri)
            tvPlantName.text = plant.name
            collapsingToolbar.title = plant.name

            val plantDateString =
                plant.createdDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            tvPlantDate.text = "${getString(R.string.plant_date)}: $plantDateString"

            val wateringDays = this@PlantDetailDialog.viewModel.getWateringDays()
            val days = wateringDays?.days
            val isDateOver = wateringDays?.isDateOver ?: false
            val today = LocalDate.now()

            binding.tvWateringDays.text = days?.let {
                if (isDateOver) {
                    getString(R.string.watering_d_day_plus_format, days)
                } else {
                    getString(R.string.watering_d_day_minus_format, days)
                }
            } ?: ""


            //물주기 버튼으로 인한 갱신시에는 동작하지 않도록함
            if (!isWateringFlag) {
                when {
                    isDateOver -> {
                        binding.ivWaterIcon.setBackgroundResource(R.drawable.ic_water_drop_red)
                        binding.animWater.visibility = View.INVISIBLE
                    }

                    plant.lastWateringDate == today -> {
                        binding.ivWaterIcon.setBackgroundResource(R.drawable.ic_water_drop_white)
                        binding.animWater.progress = 1f
                        binding.animWater.visibility = View.VISIBLE
                    }

                    else -> {
                        binding.ivWaterIcon.setBackgroundResource(R.drawable.ic_water_drop_blue)
                        binding.animWater.visibility = View.INVISIBLE
                    }
                }
            }

            //마지막 물준 날짜 세팅
            when(plant.lastWateringDate) {
                today -> {
                    tvLastWateringDate.text = getText(R.string.today)
                }

                today.minusDays(1) -> {
                    tvLastWateringDate.text = getText(R.string.yesterday)
                }

                else -> {
                    tvLastWateringDate.localDateToText(plant.lastWateringDate)
                }
            }

            if (plant.waterPeriod == 0) {        //물주기 간격 설정 없음
                tvWateringAlarm.text = getText(R.string.none)
                switchWateringAlarm.visibility = View.GONE
            } else {
                tvWateringAlarm.localTimeToText(plant.wateringAlarm.time)
                switchWateringAlarm.visibility = View.VISIBLE
            }

            binding.switchWateringAlarm.isChecked = plant.wateringAlarm.onOff
            tvMemo.text = plant.memo
            binding.tvEmptyDiary.text =
                getString(R.string.diary_list_empty_with_plantName, plant.name)
        }
    }

    fun newDiaryClick() {
        val action = PlantDetailDialogDirections.toDiaryEdit(plantId, null)
        findNavController().navigate(action)
    }

    fun moreDiaryClick() {
        val action = PlantDetailDialogDirections.toDiaryFragment(plantId.toString())
        findNavController().navigate(action)
    }

    fun onBackClick() {
        dismiss()
    }

    fun onMenuClick() {
        PopupMenu(
            requireContext(),
            binding.ivMenu,
            Gravity.END,
            0,
            R.style.popupMenuStyle
        ).run {
            menuInflater.inflate(R.menu.modify_menu, menu)
            setOnMenuItemClickListener {
                when(it.itemId) {
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
        val action = PlantDetailDialogDirections.toDiaryDetail(diaryId)
        findNavController().navigate(action)
    }

    private fun showDeleteConfirm() {
        DeleteConfirmDialog().apply {
            deleteClickListener = {
                lifecycleScope.launch {
                    viewModel.deletePlant()
                    this@PlantDetailDialog.dismiss()
                }
            }
        }.show(childFragmentManager, null)
    }

    private fun openEditPlant() {
        val plantId = viewModel.plant.value?.id
        val action = PlantDetailDialogDirections.toPlantEdit(plantId.toString())
        findNavController().navigate(action)
    }
}