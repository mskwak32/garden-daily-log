package com.mskwak.presentation.ui.fragment_diary

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mskwak.domain.model.Diary
import com.mskwak.domain.type.DiaryListSortOrder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.FragmentDiaryBinding
import com.mskwak.presentation.ui.base.BaseFragment
import com.mskwak.presentation.ui.custom_component.ListItemDecoHorizontal
import com.mskwak.presentation.ui.custom_component.ListItemDecoVertical
import com.mskwak.presentation.ui.custom_component.SortAdapter
import com.mskwak.presentation.ui.dialog.SelectMonthDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class DiaryFragment : BaseFragment<FragmentDiaryBinding>() {
    override val layoutRes: Int = R.layout.fragment_diary
    private val viewModel by viewModels<DiaryViewModel>()
    private val filterAdapter by lazy { FilterAdapter(viewModel) }
    private val diaryListAdapter by lazy {
        DiaryListAdapter(
            onItemClick = { diary ->
                openDiaryDetail(diary)
            },
            getPlantName = { plantId ->
                viewModel.plantNameMap.value?.get(plantId)
            }
        )
    }
    private val args by navArgs<DiaryFragmentArgs>()

    override fun initialize() {
        binding.fragment = this
        binding.viewModel = viewModel
        viewModel.loadPlantNames()
        initFilter()
        initDiaryList()
        initSortSpinner()
    }

    private fun initSortSpinner() {
        val spinnerAdapter = SortAdapter(
            requireContext(),
            R.layout.layout_spinner_item,
            resources.getStringArray(R.array.diary_sort_array)
        )
        binding.spinnerSort.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    spinnerAdapter.selectedItemPosition = p2
                    viewModel.setSortOrder(DiaryListSortOrder.values()[p2])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //do nothing
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initFilter() {
        val dividerWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                .toInt()
        binding.rvPlantFilter.apply {
            adapter = filterAdapter
            addItemDecoration(ListItemDecoHorizontal(dividerWidth))
        }

        if (args.plantId?.toIntOrNull() != null) {
            viewModel.selectedPlantId = args.plantId!!.toInt()
        }

        viewModel.plantNameMap.observe(viewLifecycleOwner) {
            filterAdapter.setItemMap(it)
            filterAdapter.notifyDataSetChanged()
        }
    }

    private fun initDiaryList() {
        val dividerHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                .toInt()

        binding.rvDiaryList.apply {
            adapter = diaryListAdapter
            addItemDecoration(ListItemDecoVertical(dividerHeight))
        }

        viewModel.diaries.observe(viewLifecycleOwner) {
            lifecycle.coroutineScope.launchWhenStarted {
                diaryListAdapter.submitList(it)
            }
        }
    }

    fun onMonthClick() {
        if (viewModel.month.value != null) {
            SelectMonthDialog.Builder()
                .setInitialValue(viewModel.month.value!!.year, viewModel.month.value!!.monthValue)
                .setOnCompleteListener { year, month ->
                    viewModel.setMonth(year, month)
                }
                .show(childFragmentManager)
        }
    }

    fun onThisMonthClick() {
        val currentDate = LocalDate.now()
        viewModel.setMonth(currentDate.year, currentDate.monthValue)
    }

    private fun openDiaryDetail(diary: Diary) {
        val action = DiaryFragmentDirections.diaryFragmentToDiaryDetail(diary.id)
        findNavController().navigate(action)
    }
}