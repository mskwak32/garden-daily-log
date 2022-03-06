package com.mskwak.presentation.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.appbar.AppBarLayout
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogPlantDetailBinding
import kotlin.math.abs

class PlantDetailDialog : DialogFragment() {
    private lateinit var binding: DialogPlantDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.fullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPlantDetailBinding.inflate(inflater,container,false)
        initToolbar()
        return binding.root
    }

    private fun initToolbar() {
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            if(abs(verticalOffset) >= appBarLayout.totalScrollRange) {       //collapsed
                binding.collapsingToolbar.isTitleEnabled = true
                binding.toolbar.setBackgroundResource(R.color.white)
            } else {
                binding.collapsingToolbar.isTitleEnabled = false
                binding.toolbar.setBackgroundResource(R.color.transparent)
            }
        })
    }
}