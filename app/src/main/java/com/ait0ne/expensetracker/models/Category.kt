package com.ait0ne.expensetracker.models

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "categories"
)
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var title: String,
    var cloud_id: String?
) : Serializable {
    companion object {
        fun fromContentValues(values: ContentValues?): Category? {
            var id: Int? = null
            var title: String? = null
            var cloud_id: String? = null
            values?.let {
                if (it.containsKey("id")) {
                    id = it.getAsInteger("id")
                }
                if (it.containsKey("title")) {
                    title = it.getAsString("title")
                }
                if (it.containsKey("cloud_id")) {
                    cloud_id = it.getAsString("cloud_id")
                }
            }

            if (title != null) {
                return Category(id, title!!.lowercase(), cloud_id)
            }

            return null
        }


        fun toContentValues(category: Category): ContentValues {
            val values = ContentValues()


            category.id?.let {
                values.put("id", it)

            }

            values.put("title", category.title.lowercase())
            category.cloud_id?.let {
                values.put("cloud_id", it)

            }

            return values
        }

    }
}