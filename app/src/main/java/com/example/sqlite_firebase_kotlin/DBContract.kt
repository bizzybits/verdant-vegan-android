package com.example.sqlite_firebase_kotlin

import android.provider.BaseColumns

object DBContract {

    /* Inner class that defines the table contents */
    class RecipeEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "recipes"
            val COLUMN_RECIPE_NAME = "recipeName"
            val COLUMN_CUISINE = "cuisine"
            val COLUMN_INGREDIENT = "ingredient"
        }
    }
}