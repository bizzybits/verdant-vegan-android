package com.example.sqlite_firebase_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.example.sqlite_firebase_kotlin.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityProfileBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //FIrebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recipesDBHelper = RecipeDBHelper(this)

        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.title = "My Vegan Recipes"

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user not null, user is logged in, get user info
            val email = firebaseUser.email
            //set to text view
            binding.emailTv.text = email

        }
        else {
            //user is null, user is not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    lateinit var recipesDBHelper : RecipeDBHelper

    fun addRecipe(v: View){
        var recipeName = this.edittext_recipeName.text.toString()
        var cuisine = this.edittext_cuisine.text.toString()
        var ingredient = this.edittext_ingredient.text.toString()
        var result = recipesDBHelper.insertRecipe(RecipeModel(recipeName = recipeName,cuisine = cuisine,ingredient = ingredient))
        //clear all edittext s
        this.edittext_ingredient.setText("")
        this.edittext_cuisine.setText("")
        this.edittext_recipeName.setText("")
        this.textview_result.text = "Added recipe : "+result
        this.ll_entries.removeAllViews()
    }

    fun deleteRecipe(v: View){
        var recipeName = this.edittext_recipeName.text.toString()
        val result = recipesDBHelper.deleteRecipe(recipeName)
        this.textview_result.text = "Deleted recipe : "+result
        this.ll_entries.removeAllViews()
    }

    fun editRecipe(v: View){
        var recipeName = this.edittext_recipeName.text.toString()
        var cuisine = this.edittext_cuisine.text.toString()
        var ingredient = this.edittext_ingredient.text.toString()
        val result = recipesDBHelper.editRecipe(recipeName, cuisine, ingredient)
        this.textview_result.text = "Edited recipe : "+result
        this.ll_entries.removeAllViews()
    }
    fun showAllRecipes(v: View){
        var recipes = recipesDBHelper.readAllRecipes()
        this.ll_entries.removeAllViews()
        recipes.forEach {
            var tv_recipe = TextView(this)
            tv_recipe.textSize = 15F
            tv_recipe.text = it.recipeName.toString() + " : " + it.cuisine.toString() + " - " + it.ingredient.toString()
            this.ll_entries.addView(tv_recipe)
        }
        this.textview_result.text = "Fetched " + recipes.size + " recipes!"
    }
}