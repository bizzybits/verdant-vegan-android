package com.example.sqlite_firebase_kotlin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqlite_firebase_kotlin.DBContract.RecipeEntry.*

import java.util.ArrayList

class RecipeDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertRecipe(recipe: RecipeModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.RecipeEntry.COLUMN_RECIPE_NAME, recipe.recipeName)
        values.put(DBContract.RecipeEntry.COLUMN_CUISINE, recipe.cuisine)
        values.put(DBContract.RecipeEntry.COLUMN_INGREDIENT, recipe.ingredient)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.RecipeEntry.TABLE_NAME, null, values)

        return true
    }
    @Throws(SQLiteConstraintException::class)
    fun deleteRecipe(recipeName: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.RecipeEntry.COLUMN_RECIPE_NAME + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(recipeName)
        // Issue SQL statement.
        db.delete(DBContract.RecipeEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun editRecipe(recipeName: String, cuisine: String, ingredient: String): String {
        //val recipe = ArrayList<String>()
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.RecipeEntry.COLUMN_RECIPE_NAME + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(recipeName)

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.RecipeEntry.COLUMN_RECIPE_NAME, recipeName)
        values.put(DBContract.RecipeEntry.COLUMN_CUISINE, cuisine)
        values.put(DBContract.RecipeEntry.COLUMN_INGREDIENT, ingredient)

        // Issue SQL statement.A
        db.update(DBContract.RecipeEntry.TABLE_NAME, values, selection, selectionArgs)
        return selectionArgs[0]
    }
//App
    fun readRecipe(recipeName: String): ArrayList<RecipeModel> {
        val recipes = ArrayList<RecipeModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.RecipeEntry.TABLE_NAME + " WHERE " + DBContract.RecipeEntry.COLUMN_RECIPE_NAME + "='" + recipeName + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var cuisine: String
        var ingredient: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                cuisine = cursor.getString(cursor.getColumnIndex(DBContract.RecipeEntry.COLUMN_CUISINE))
                ingredient = cursor.getString(cursor.getColumnIndex(DBContract.RecipeEntry.COLUMN_INGREDIENT))

                recipes.add(RecipeModel(recipeName, cuisine, ingredient))
                cursor.moveToNext()
            }
        }
        return recipes
    }
    fun readAllRecipes(): ArrayList<RecipeModel> {
        val recipes = ArrayList<RecipeModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.RecipeEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var recipeName: String
        var cuisine: String
        var ingredient: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                recipeName = cursor.getString(cursor.getColumnIndex(DBContract.RecipeEntry.COLUMN_RECIPE_NAME))
                cuisine = cursor.getString(cursor.getColumnIndex(DBContract.RecipeEntry.COLUMN_CUISINE))
                ingredient = cursor.getString(cursor.getColumnIndex(DBContract.RecipeEntry.COLUMN_INGREDIENT))

                recipes.add(RecipeModel(recipeName, cuisine, ingredient))
                cursor.moveToNext()
            }
        }
        return recipes
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.RecipeEntry.TABLE_NAME + " (" +
                    DBContract.RecipeEntry.COLUMN_RECIPE_NAME + " TEXT PRIMARY KEY," +
                    DBContract.RecipeEntry.COLUMN_CUISINE + " TEXT," +
                    DBContract.RecipeEntry.COLUMN_INGREDIENT + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.RecipeEntry.TABLE_NAME
    }
}