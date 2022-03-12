package com.mskwak.presentation.diary.diary_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mskwak.presentation.R
import com.mskwak.presentation.binding.localDateToText
import com.mskwak.presentation.custom_component.ZoomOutPageTransformer
import com.mskwak.presentation.databinding.DialogDiaryDetailBinding
import com.mskwak.presentation.dialog.DeleteConfirmDialog
import com.mskwak.presentation.diary.edit_diary.EditDiaryDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiaryDetailDialog(private val diaryId: Int) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDiaryDetailBinding
    private val pictureAdapter by lazy { PictureViewPagerAdapter() }

    @Inject
    lateinit var viewModelFactory: DiaryDetailViewModel.DiaryDetailViewModelFactory
    private val viewModel by viewModels<DiaryDetailViewModel> {
        DiaryDetailViewModel.provideFactory(viewModelFactory, diaryId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDiaryDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DiaryDetailDialog.viewModel
            dialog = this@DiaryDetailDialog
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initObserver()
    }

    private fun initViewPager() {
        binding.viewPager.apply {
            adapter = pictureAdapter
            setPageTransformer(ZoomOutPageTransformer())
            binding.pageIndicator.setViewPager2(this)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        viewModel.diary.observe(viewLifecycleOwner) { diary ->
            binding.date.localDateToText(diary.createdDate)
            binding.memoText.text = diary.memo.ifBlank { getString(R.string.diary_content_empty) }
            pictureAdapter.itemList = diary.pictureList
            pictureAdapter.notifyDataSetChanged()
        }
    }

    fun onCloseClick() {
        dismiss()
    }

    fun onMenuClick() {
        PopupMenu(
            requireContext(),
            binding.menuButton,
            Gravity.START,
            0,
            R.style.popupMenuStyle
        ).run {
            menuInflater.inflate(R.menu.modify_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        showEditDiary()
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

    private fun showDeleteConfirm() {
        DeleteConfirmDialog().apply {
            deleteClickListener = {
                viewModel.deleteDiary()
                this@DiaryDetailDialog.dismiss()
            }
        }.show(childFragmentManager, null)
    }

    private fun showEditDiary() {
        EditDiaryDialog(viewModel.diary.value!!.plantId, viewModel.diary.value!!.id)
            .show(childFragmentManager, null)
    }
}