package com.example.favdish.view.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.databinding.FragmentRandomDishBinding
import com.example.favdish.model.entities.RandomDish
import com.example.favdish.viewmodel.NotificationsViewModel
import com.example.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var binding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]

        mRandomDishViewModel.getRandomRecipeFromApi()
        randomDishViewModelViewModelObserver()
    }

    private fun randomDishViewModelViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner,
            {randomDishResponse -> randomDishResponse?.let {
                setRandomDishResponseInUI(randomDishResponse.recipes[0])

            }}
            )

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner,
            {dataError -> dataError?.let {
                Log.e("RandomDishError", "$dataError")
            }}
        )

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner,
            {loadRandomDish -> loadRandomDish?.let {
                Log.e("RandomDishLoading", "$loadRandomDish")
            }}
        )
    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding?.ivDishImage!!)

        binding?.tvTitle?.text = recipe.title

        var dishType: String = "Other"
        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            binding!!.tvType.text = dishType
        }
        binding?.tvCategory?.text = "Other"
        var ingredients = ""
        for(value in recipe.extendedIngredients) {
            if (ingredients.isEmpty()){
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }
        binding?.tvIngredients?.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding?.tvCookingDirection?.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            binding?.tvCookingDirection?.text = Html.fromHtml(recipe.instructions)
        }

        binding?.tvCookingTime?.text = resources.getString(R.string.lblEstimateCookingTime, recipe.readyInMinutes.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}