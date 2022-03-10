package com.mskwak.presentation.diary.diary_detail

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mskwak.presentation.R
import com.mskwak.presentation.custom_component.ZoomOutPageTransformer
import com.mskwak.presentation.databinding.DialogDiaryDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryDetailDialog(private val recordId: Int) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDiaryDetailBinding
    private val viewModel by viewModels<DiaryDetailViewModel>()
    private val pictureAdapter by lazy { PictureViewPagerAdapter() }

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

        viewModel.loadDiary(recordId)
    }

    private fun initViewPager() {
        binding.viewPager.apply {
            adapter = pictureAdapter
            setPageTransformer(ZoomOutPageTransformer())
            binding.pageIndicator.setViewPager2(this)
        }
    }

    private fun initObserver() {
        viewModel.pictureList.observe(viewLifecycleOwner) { list ->
            pictureAdapter.itemList = list
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
                        //TODO 일지 수정
                    }
                    R.id.menu_delete -> {
                        //TODO 일지 삭제 다이얼로그
                    }
                }
                true
            }
            show()
        }
    }
}