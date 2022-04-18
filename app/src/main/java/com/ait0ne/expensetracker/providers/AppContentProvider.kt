package com.ait0ne.expensetracker.providers

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.OnConflictStrategy
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.models.Expense

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

}



class AppContentProvider(): ContentProvider() {
    private lateinit var db: ExpenseDB

    companion object {
        val URI = Uri.parse("content://com.ait0ne.expensetracker.provider/expenses")

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

                db.openHelper.readableDatabase.query(SupportSQLiteQueryBuilder.builder("expenses").selection(p2, p1).columns(p3).orderBy(p4).create())

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

           else -> {
               return null
           }
       }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return when (sUriMatcher.match(p0)) {
            1 -> {
                val expense = Expense.fromContentValues(p1)

                expense?.let {

//                    val inserted =  db.getExpenseDao().upsertSync(it)
                    val inserted = db.openHelper.writableDatabase.insert("expenses", OnConflictStrategy.REPLACE, p1)

                    return ContentUris.withAppendedId(p0, inserted)
                }

                throw java.lang.IllegalArgumentException()

            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return when (sUriMatcher.match(p0)) {
            2 -> {


                val id = p0.lastPathSegment?.toLong()
                id?.let {
                    return db.getExpenseDao().deleteExpenseByIdSync(id)

                }


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
                    db.openHelper.writableDatabase.update("expenses", OnConflictStrategy.REPLACE, p1, "where id = ?",
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