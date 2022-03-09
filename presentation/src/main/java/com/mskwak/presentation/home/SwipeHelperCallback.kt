package com.mskwak.presentation.home

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

    //10배의 속도로 swipe해야 escape함. 즉 빠른 swipe로 인한 escape막기
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    //width의 2배 거리로 이동해야 swipe됨. 즉 escape 거리 막기
    //사용자가 손을 놓았을 때 호출됨
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        //현재 view가 고정되어있지않고 사용자가 -clamp 이상 swipe시 isClamped = true 아니면 isClamped = false
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    //isClamp를 view의 tag로 관리
    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    //view의 x위치 계산
    private fun getViewHorizontalPosition(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        //view의 가로 길이의 절반까지만 swipe되도록
        val min = -view.width.toFloat() / 2
        //RIGHT방향 swipe 막기
        val max = 0f

        val x = if (isClamped) {
            //view가 고정되었을 때 swipe되는 영역 제한
            //고정되어있는 뷰를 이동하면 dX에 clamp값을 더해줘야 고정값에서부터 이동
            //isCurrentlyActive는 사용자가 이동하고 있을 때. 손을 떼면 false이므로 고정위치로 이동
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    //다른뷰가 swipe되면 고정해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentItemPosition == previousItemPosition) return
        previousItemPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            ObjectAnimator.ofFloat(getView(viewHolder), "translationX", 0f).apply {
                duration = 300
                start()
            }
            setTag(viewHolder, false)
            previousItemPosition == null
        }
    }
}