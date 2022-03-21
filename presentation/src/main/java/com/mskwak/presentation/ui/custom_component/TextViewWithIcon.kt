package com.mskwak.presentation.ui.custom_component

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mskwak.presentation.R

class TextViewWithIcon @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var iconView: ImageView
    private var textView: TextView
    private var requiredView: TextView
    private var isRequired: Boolean = false
    private val defaultTextColor: Int

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_textview_with_icon, this, false)
        addView(view)

        iconView = view.findViewById(R.id.icon)
        textView = view.findViewById(R.id.text)
        requiredView = view.findViewById(R.id.requiredIcon)
        defaultTextColor = textView.currentTextColor
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
            iconView.setImageResource(iconRes)
        }

        with(textView) {
            setText(text)
            if (textColor != -1) setTextColor(textColor)
        }

        requiredView.visibility = if (isRequired) VISIBLE else GONE
    }

    fun setFieldEmpty(isEmpty: Boolean) {
        if (!isRequired) {
            return
        }
        val textColor = if (isEmpty) resources.getColor(R.color.red_300, null) else defaultTextColor
        textView.setTextColor(textColor)
    }
}