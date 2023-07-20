package com.mskwak.presentation.ui.custom_component

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.LayoutTextviewWithIconBinding

class TextViewWithIcon @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private lateinit var binding: LayoutTextviewWithIconBinding
    private var isRequired: Boolean = false
    private val defaultTextColor: Int

    init {
        val binding =
            LayoutTextviewWithIconBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)

        defaultTextColor = binding.tvText.currentTextColor
        initAttrs(attrs)
    }

    @SuppressLint("Recycle")
    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithIcon)
        val iconRes = typedArray.getResourceId(R.styleable.TextViewWithIcon_iconRes, -1)
        val text = typedArray.getText(R.styleable.TextViewWithIcon_text)
        val textColor = typedArray.getColor(R.styleable.TextViewWithIcon_textColor, -1)
        isRequired = typedArray.getBoolean(R.styleable.TextViewWithIcon_required, false)

        if (iconRes != -1) {
            binding.tvIcon.setImageResource(iconRes)
        }

        with(binding.tvText) {
            setText(text)
            if (textColor != -1) setTextColor(textColor)
        }

        binding.tvRequiredIcon.visibility = if (isRequired) VISIBLE else GONE
    }

    fun setFieldEmpty(isEmpty: Boolean) {
        if (!isRequired) {
            return
        }
        val textColor = if (isEmpty) resources.getColor(R.color.red_300, null) else defaultTextColor
        binding.tvText.setTextColor(textColor)
    }
}