package com.ait0ne.expensetracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.utils.DateUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.expense_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class ExpensesAdapter : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>(){

    inner class ExpenseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<ExpenseDTO>() {
        override fun areItemsTheSame(oldItem: ExpenseDTO, newItem: ExpenseDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseDTO, newItem: ExpenseDTO): Boolean {

            return oldItem.amount == newItem.amount && oldItem.category == newItem.category && oldItem.category_id == newItem.category_id && oldItem.date == newItem.date && newItem.currency == oldItem.currency && newItem.title == oldItem.title

        }
    }


    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.expense_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = differ.currentList[position]
        holder.itemView.apply {
            val pattern = "HH:mm"
            val format = SimpleDateFormat(pattern, Locale("ru", "RU"))

            tvAmount.text = expense.amount.toInt().toString() + Currency.symbol(expense.currency)
            tvDate.text = format.format(expense.date)

            tvExpenseCategory.text = expense.category.title

            var showDate = false


            if (position == 0) {
                showDate = true
                val params = (tvCurrentDay.layoutParams as ViewGroup.MarginLayoutParams)
                params.topMargin = 0
            } else {
                val previous = differ.currentList[position -1]

                if (!DateUtils.isSameDay(previous.date, expense.date)) {
                    showDate = true
                }
            }


            if (showDate) {
                val pattern = "d MMMM"
                tvCurrentDay.visibility = View.VISIBLE


                tvCurrentDay.text = SimpleDateFormat(pattern, Locale("ru", "RU")).format(expense.date)
            } else {
                tvCurrentDay.visibility = View.GONE
            }


            setOnClickListener {
                onItemClickListener?.let {
                    it(expense)
                }
            }


        }
    }


    private var onItemClickListener : ((ExpenseDTO) ->Unit)? = null


    fun setOnItemClickListener(listener: (ExpenseDTO) ->Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}