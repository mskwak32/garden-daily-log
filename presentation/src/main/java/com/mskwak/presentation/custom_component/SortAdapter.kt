package com.mskwak.presentation.custom_component

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.mskwak.presentation.R

class SortAdapter(context: Context, @LayoutRes resource: Int, objects: Array<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    var selectedItemPosition = 0

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent).also { view ->
            if (position == selectedItemPosition) {
                (view as? TextView)?.setTextColor(context.getColor(R.color.green_600))
            }
        }
    }
}