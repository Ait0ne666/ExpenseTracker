package com.ait0ne.expensetracker.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

object BindingUtils {

    @BindingAdapter("android:text")
    @JvmStatic
    public fun setText(view: TextInputEditText, value: Float?) {
        if (value == null) return;
        if (value.isNaN() || value == 0f) view.setText("");
        else view.setText(value.toString());
    }

    @InverseBindingAdapter(attribute = "android:text")
    @JvmStatic
    public fun getText(view: TextInputEditText): Float {
        val num = view.getText().toString();
        return num.toFloatOrNull() ?: return 0f
    }


    @BindingAdapter("android:text")
    @JvmStatic
    public fun setText(view: TextView, value: Float?) {
        if (value == null) return;
        if (value.isNaN() || value == 0f) view.setText("0");
        else view.setText(value.toInt().toString());
    }

    @InverseBindingAdapter(attribute = "android:text")
    @JvmStatic
    public fun getText(view: TextView): Float {
        val num = view.getText().toString();
        return num.toFloatOrNull() ?: return 0f
    }


    @InverseBindingAdapter(attribute = "android:text")
    @JvmStatic
    public fun getDate(view: TextInputEditText): Date {
        val pattern = "dd-MM-yyyy"
        val format = SimpleDateFormat(pattern, Locale.GERMANY)
        return format.parse(view.getText().toString())
    }


    @BindingAdapter("android:text")
    @JvmStatic
    public fun setDate(view: TextInputEditText, value: Date?) {
        val date = value ?: Date()
        val pattern = "dd-MM-yyyy"
        val format = SimpleDateFormat(pattern, Locale.GERMANY)


        view.setText(format.format(date))
    }
}