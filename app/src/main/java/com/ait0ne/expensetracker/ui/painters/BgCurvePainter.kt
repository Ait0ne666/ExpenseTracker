package com.ait0ne.expensetracker.ui.painters

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.widget.LinearLayout
import com.ait0ne.expensetracker.R


class CurvedLinearLayout(val ctx: Context, val attributes: AttributeSet): LinearLayout(ctx, attributes) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        setWillNotDraw(false)
        super.onLayout(changed, l, t, r, b)
    }


    override fun onDraw(canvas: Canvas?) {

        canvas?.let {
            val paint = Paint(ANTI_ALIAS_FLAG).apply {
                val colors = intArrayOf(
                    ctx.getColor(
                        R.color.gradientStart),
                    ctx.getColor(
                        R.color.gradientCenter),
                    ctx.getColor(
                        R.color.gradientEnd))

                shader = LinearGradient(0F,0F, canvas.width.toFloat(), canvas.height.toFloat(),colors, floatArrayOf(0f, 0.5F,  0.9F), Shader.TileMode.CLAMP )
//                color = Color.RED
                style = Paint.Style.FILL_AND_STROKE
            }
            val path = Path().apply {
                val height = canvas.height
                val width = canvas.width
                moveTo(0F,0F)
                lineTo(0F, height*0.6.toFloat())
                quadTo(0F, height*0.8.toFloat(), 0.2F * width.toFloat(), height*0.8.toFloat())
                lineTo( width*0.8F, height*0.8.toFloat())
                quadTo(width.toFloat(), height*0.8F, width.toFloat(), height.toFloat())
                lineTo(width.toFloat(), 0F)
                close()
            }

            canvas.drawPath(path, paint)

        }

        super.onDraw(canvas)

    }
}