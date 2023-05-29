package com.mskwak.presentation.ui.diary_dialog.diary_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogDiaryDetailBinding
import com.mskwak.presentation.ui.binding.localDateToText
import com.mskwak.presentation.ui.custom_component.ZoomOutPageTransformer
import com.mskwak.presentation.ui.dialog.DeleteConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryDetailDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDiaryDetailBinding
    private val args by navArgs<DiaryDetailDialogArgs>()
    private val pictureAdapter by lazy { PictureViewPagerAdapter() }
    private val viewModel by viewModels<DiaryDetailViewModel>()

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
        viewModel.loadDiary(args.diaryId)
    }

    private fun initViewPager() {
        binding.viewPager.apply {
            adapter = pictureAdapter
            setPageTransformer(ZoomOutPageTransformer())
            binding.pageIndicator.attachTo(this)
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
        val plantId = viewModel.diary.value!!.plantId
        val diaryId = viewModel.diary.value!!.id.toString()
        val action = DiaryDetailDialogDirections.diaryDetailToDiaryEdit(plantId, diaryId)
        findNavController().navigate(action)
    }
}