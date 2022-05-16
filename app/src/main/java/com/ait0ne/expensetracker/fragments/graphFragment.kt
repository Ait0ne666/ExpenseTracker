package com.ait0ne.expensetracker.fragments

import android.app.ActionBar
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ait0ne.expensetracker.MainActivity
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.databinding.FragmentGraphBinding
import com.ait0ne.expensetracker.databinding.FragmentListBinding
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.dto.MonthCategory
import com.ait0ne.expensetracker.ui.bottomsheetpicker.BottomSheetPicker
import com.ait0ne.expensetracker.ui.bottomsheetpicker.SelectOption
import com.ait0ne.expensetracker.ui.viewmodels.ChartsViewModel
import com.ait0ne.expensetracker.utils.DateUtils
import com.ait0ne.expensetracker.utils.Resource
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.android.synthetic.main.fragment_graph.pbExpensesLoader
import kotlinx.android.synthetic.main.fragment_graph.tvCurrentMonth
import kotlinx.android.synthetic.main.fragment_list.*
import java.time.LocalDateTime
import java.util.*
import kotlin.math.PI

val colors = mutableListOf<Int>().apply {
    add(Color.parseColor("#d26af1"))
    add(Color.parseColor("#24ade2"))
    add(Color.parseColor("#6bd08b"))
    add(Color.parseColor("#fe956c"))
    add(Color.parseColor("#7fd6ac"))
    add(Color.parseColor("#9b52de"))
    add(Color.parseColor("#efa243"))
    add(Color.parseColor("#eb4c84"))
    add(Color.parseColor("#f492ab"))


}


class graphFragment : Fragment(R.layout.fragment_graph) {
    lateinit var viewmodel: ChartsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGraphBinding =
            DataBindingUtil.inflate(
                inflater,
                com.ait0ne.expensetracker.R.layout.fragment_graph,
                container,
                false
            )
        viewmodel = (activity as MainActivity).chartsViewModel
        binding.viewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.currentMonth.observe(viewLifecycleOwner) {
            tvCurrentMonth.text = "Статистика за " + DateUtils.months[it.month ?: 0]
        }



        viewmodel.chartInfo.observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    val entries = arrayListOf<PieEntry>()

                    it.data?.categories?.sortedWith(compareBy<MonthCategory> { c ->
                        c.amount
                    })?.reversed()?.forEach { category ->
                        entries.add(
                            PieEntry(
                                category.amount,
                                category.category_title + " " + category.amount.toInt() +  Currency.symbol(viewmodel.currency.value!!)
                            )
                        )
                    }

                    val set = PieDataSet(entries, null)
                    set.colors = colors
                    set.setDrawValues(false)


                    val chartData = PieData(set)
                    pcExpenses.apply {

                        data = chartData
                        description.isEnabled = false
                        setDrawEntryLabels(false)

                        centerText = it.data?.month_total?.toInt().toString() + Currency.symbol(viewmodel.currency.value!!)
                        setCenterTextColor(requireContext().getColor(R.color.grey))
                        setCenterTextSize(20f)
                        rotationAngle = 90f
                    }
                    pcExpenses.visibility = View.VISIBLE
                    hideProgressBar()

                    pcExpenses.invalidate()
                    

                    val legend = pcExpenses.legend
                    pcExpenses.legend.isEnabled = false
                    flLegend.removeAllViews()
                    legend.entries.forEach { entry ->

                        val tv = TextView(requireContext())
                        tv.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(10, 0, 10, 20)
                        }

                        val shape = GradientDrawable()

                        shape.cornerRadius = 60f
                        shape.colors = IntArray(2).apply {
                            set(0, entry.formColor)
                            set(1, entry.formColor)
                        }
                        tv.background = shape

                        tv.setPadding(50, 10, 50, 10)
                        tv.setTextColor(requireContext().getColor(R.color.white))
                        tv.text = entry.label
                        tv.typeface = Typeface.DEFAULT_BOLD

                        flLegend.addView(tv)

                    }
                }

                is Resource.Error -> {

                }

                is Resource.Loading -> {
                    pcExpenses.visibility = View.INVISIBLE
                    showProgressBar()
                }

            }
        }


        tvCurrentMonth.setOnClickListener {
            showDatePicker()
        }



        viewmodel.refetch()
    }


    fun showDatePicker() {
        val currentDate = LocalDateTime.now()
        val months = mutableListOf<SelectOption<Date>>()


        for (i in 0..11) {
            val month = currentDate.minusMonths(i.toLong())
            val date = Date()
            date.month = month.month.value - 1
            date.year = month.year - 1900

            months.add(SelectOption(date, DateUtils.months[date.month] + " " + (date.year + 1900)))
        }


        val picker = BottomSheetPicker<Date>(months) {
            viewmodel.changeDate(it)

        }

        picker.show(parentFragmentManager, "")
    }


    private fun hideProgressBar() {
        pbExpensesLoader.visibility = View.GONE
    }


    private fun showProgressBar() {
        pbExpensesLoader.visibility = View.VISIBLE
    }
}