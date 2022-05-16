package com.ait0ne.expensetracker.providers

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.room.OnConflictStrategy
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Expense
import java.util.*

private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    /*
     * The calls to addURI() go here, for all of the content URI patterns that the provider
     * should recognize. For this snippet, only the calls for table 3 are shown.
     */

    /*
     * Sets the integer value for multiple rows in table 3 to 1. Notice that no wildcard is used
     * in the path
     */
    addURI("com.ait0ne.expensetracker.provider", "expenses", 1)
    addURI("com.ait0ne.expensetracker.provider", "expenses/#", 2)
    addURI("com.ait0ne.expensetracker.provider", "categories", 3)
    addURI("com.ait0ne.expensetracker.provider", "categories/#", 4)

}



class AppContentProvider(): ContentProvider() {
    private lateinit var db: ExpenseDB

    companion object {
        val URI = Uri.parse("content://com.ait0ne.expensetracker.provider/expenses")

        val CATEGORIES_URI = Uri.parse("content://com.ait0ne.expensetracker.provider/categories")

    }



    override fun onCreate(): Boolean {
        db = ExpenseDB.invoke(context!!)


        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {

        return when (sUriMatcher.match(p0)) {
            1 -> {

                val URI = db.openHelper.readableDatabase.query("select expenses.*, categories.title as category_name from expenses join categories on expenses.category_id = categories.id where expenses.dirty = 1")


                return URI
            }
            2 -> {
                var id = p2


                val URI = db.openHelper.readableDatabase.query("select expenses.*, categories.title  as category_name from expenses join categories on expenses.category_id = categories.id where expenses.cloud_id = ?", arrayOf(id))



                return URI
            }
            3 -> {
                val URI = db.openHelper.readableDatabase.query("select * from categories")



                return URI
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun getType(p0: Uri): String? {
       return when(sUriMatcher.match(p0)){
           1 -> {



               return "vnd.android.cursor.dir/vnd.com.ait0ne.expensestracker.expenses"
           }
           2 -> {
               return "vnd.android.cursor.item/vnd.com.ait0ne.expensestracker.expenses"

           }
           3 -> {



               return "vnd.android.cursor.dir/vnd.com.ait0ne.expensestracker.categories"
           }
           4 -> {
               return "vnd.android.cursor.item/vnd.com.ait0ne.expensestracker.categories"
           }

           else -> {
               return null
           }
       }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        Log.i("Provider", "category3")
        return when (sUriMatcher.match(p0)) {
            1 -> {
                val expense = Expense.fromContentValues(p1)

                expense?.let {
                    Log.i("Provider", "expense" + p1.toString())
                    val inserted = db.openHelper.writableDatabase.insert("expenses", OnConflictStrategy.REPLACE, p1)

                    return ContentUris.withAppendedId(p0, inserted)
                }


                Log.i("Provider", "category2")

                throw java.lang.IllegalArgumentException()

            }

            3 -> {
                val category = Category.fromContentValues(p1)

                category?.let {

                    val inserted = db.openHelper.writableDatabase.insert("categories", OnConflictStrategy.REPLACE, p1)

                    return ContentUris.withAppendedId(p0, inserted)
                }
                Log.i("Provider", p1.toString())
                Log.i("Provider", category.toString())
                throw java.lang.IllegalArgumentException()
            }
            else -> {
                Log.i("Provider", p0.toString())
                throw IllegalArgumentException()
            }
        }
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return when (sUriMatcher.match(p0)) {
            2 -> {



                throw IllegalArgumentException()

            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return when (sUriMatcher.match(p0)) {
            1 -> {
                val expense = Expense.fromContentValues(p1)

                expense?.let {
                    //                    db.getExpenseDao().upsertSync(it)
                    db.openHelper.writableDatabase.update("expenses", OnConflictStrategy.REPLACE, p1, "id = ?",
                        arrayOf(expense.id)
                    )

                    return 1

                }

                throw java.lang.IllegalArgumentException()

            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }


}