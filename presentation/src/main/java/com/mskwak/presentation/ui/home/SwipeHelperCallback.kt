package com.mskwak.presentation.ui.home

import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.R
import java.lang.Float.max
import java.lang.Float.min

class SwipeHelperCallback : ItemTouchHelper.Callback() {
    private var currentItemPosition: Int? = null
    private var previousItemPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as PlantListAdapter.ItemViewHolder).itemView.findViewById(R.id.container)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x = getViewHorizontalPosition(view, dX, isClamped, isCurrentlyActive)
            currentDx = x
            getDefaultUIUtil().onDraw(c, recyclerView, view, x, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousItemPosition = viewHolder.adapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentItemPosition = viewHolder.adapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    //10?????? ????????? swipe?????? escape???. ??? ?????? swipe??? ?????? escape??????
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    //width??? 2??? ????????? ???????????? swipe???. ??? escape ?????? ??????
    //???????????? ?????? ????????? ??? ?????????
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        //?????? view??? ???????????????????????? ???????????? -clamp ?????? swipe??? isClamped = true ????????? isClamped = false
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    //isClamp??? view??? tag??? ??????
    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    //view??? x?????? ??????
    private fun getViewHorizontalPosition(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        //view??? ?????? ????????? ??????????????? swipe?????????
        val min = -view.width.toFloat() / 2
        //RIGHT?????? swipe ??????
        val max = 0f

        val x = if (isClamped) {
            //view??? ??????????????? ??? swipe?????? ?????? ??????
            //?????????????????? ?????? ???????????? dX??? clamp?????? ???????????? ????????????????????? ??????
            //isCurrentlyActive??? ???????????? ???????????? ?????? ???. ?????? ?????? false????????? ??????????????? ??????
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    //???????????? swipe?????? ????????????
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentItemPosition == previousItemPosition) return
        previousItemPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            ObjectAnimator.ofFloat(getView(viewHolder), "translationX", 0f).apply {
                duration = 300
                start()
            }
            setTag(viewHolder, false)
            previousItemPosition = null
        }
    }

    fun removeCurrentClamp(recyclerView: RecyclerView) {
        currentItemPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            ObjectAnimator.ofFloat(getView(viewHolder), "translationX", 0f).apply {
                duration = 300
                start()
            }
            setTag(viewHolder, false)
            previousItemPosition = null
            currentItemPosition = null
        }
    }
}