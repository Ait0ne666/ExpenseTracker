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
import com.ait0ne.expensetracker.models.ExpenseWithCategory
import com.ait0ne.expensetracker.utils.DateUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.expense_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class ExpensesAdapter : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<ExpenseWithCategory>() {
        override fun areItemsTheSame(oldItem: ExpenseWithCategory, newItem: ExpenseWithCategory): Boolean {
            return oldItem.expense.id == newItem.expense.id
        }

        override fun areContentsTheSame(oldItem: ExpenseWithCategory, newItem: ExpenseWithCategory): Boolean {

            return false
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

            tvAmount.text = expense.expense.amount.toInt().toString() + Currency.symbol(expense.expense.currency)
            tvDate.text = format.format(expense.expense.date)

            tvExpenseCategory.text = expense.category.title.capitalize()

            var showDate = false


            if (position == 0) {
                showDate = true
                val params = (tvCurrentDay.layoutParams as ViewGroup.MarginLayoutParams)
                params.topMargin = 0
            } else {
                val previous = differ.currentList[position - 1]

                if (!DateUtils.isSameDay(previous.expense.date, expense.expense.date)) {
                    showDate = true
                }
            }


            if (showDate) {
                val pattern = "d MMMM"
                tvCurrentDay.visibility = View.VISIBLE


                tvCurrentDay.text =
                    SimpleDateFormat(pattern, Locale("ru", "RU")).format(expense.expense.date)
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


    private var onItemClickListener: ((ExpenseWithCategory) -> Unit)? = null


    fun setOnItemClickListener(listener: (ExpenseWithCategory) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}