package com.ait0ne.expensetracker.db

import android.database.Cursor
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.ExpenseFromApi
import com.ait0ne.expensetracker.utils.DateUtils
import java.util.*

class DbUtils {


    companion object {
        fun parseCursor(cursor: Cursor): MutableList<ExpenseFromApi> {

            val expenses = mutableListOf<ExpenseFromApi>()


            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                    val title = cursor.getStringOrNull(cursor.getColumnIndex("title"))
                    val date = cursor.getLongOrNull(cursor.getColumnIndex("date"))
                    val amount = cursor.getFloatOrNull(cursor.getColumnIndex("amount"))
                    val currency = cursor.getStringOrNull(cursor.getColumnIndex("currency"))
                    val cloud_id = cursor.getStringOrNull(cursor.getColumnIndex("cloud_id"))
                    val category_name =
                        cursor.getStringOrNull(cursor.getColumnIndex("category_name"))
                    val created_at = cursor.getLongOrNull(cursor.getColumnIndex("created_at"))
                    val updated_at = cursor.getLongOrNull(cursor.getColumnIndex("updated_at"))
                    val deleted_at = cursor.getLongOrNull(cursor.getColumnIndex("deleted_at"))

                    if (category_name != null && amount != null && date != null && currency != null && created_at != null && updated_at != null) {
                        expenses.add(
                            ExpenseFromApi(
                                id = id,
                                cloud_id = cloud_id,
                                title = title,
                                created_at = DateUtils.localToUTCString(Date(created_at)),
                                updated_at = DateUtils.localToUTCString(Date(updated_at)),
                                category_id = null,
                                category_name = category_name,
                                amount = amount,
                                deleted_at = if (deleted_at == null) {
                                    null
                                } else {
                                    DateUtils.localToUTCString(Date(deleted_at))
                                },
                                date = DateUtils.localToUTCString(Date(date)),
                                currency = Currency.fromText(currency),
                                success = null

                            )
                        )

                    }



                    cursor.moveToNext()
                }
            }
            cursor.close()


            return expenses

        }



        fun parseCategoriesCursor(cursor: Cursor): MutableList<Category> {

            val categories = mutableListOf<Category>()


            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getIntOrNull(cursor.getColumnIndex("id"))
                    val title = cursor.getStringOrNull(cursor.getColumnIndex("title"))
                    val cloud_id = cursor.getStringOrNull(cursor.getColumnIndex("cloud_id"))


                    if (title != null) {
                        categories.add(Category(id, title, cloud_id))
                    }



                    cursor.moveToNext()
                }
            }
            cursor.close()


            return categories

        }
    }
}