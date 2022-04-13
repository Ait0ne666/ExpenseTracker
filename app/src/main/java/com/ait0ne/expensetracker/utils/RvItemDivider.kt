package com.ait0ne.expensetracker.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RvItemDivider(private val vertical: Int, private val horizontal: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = horizontal
        outRect.right = horizontal



        outRect.bottom = vertical
    }
}