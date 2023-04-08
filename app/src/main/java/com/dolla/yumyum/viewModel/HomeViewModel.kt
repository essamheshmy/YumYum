package com.dolla.yumyum.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolla.yumyum.db.MealDatabase
import com.dolla.yumyum.pojo.*
import com.dolla.yumyum.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @created 28/03/2023 - 5:07 PM
 * @project YumYum
 * @author adell
 */

class HomeViewModel(private val mealDatabase: MealDatabase) : ViewModel() {

    private val _randomMealLiveData = MutableLiveData<Meal>()
    val randomMealLiveData: LiveData<Meal>
        get() = _randomMealLiveData // This is a read-only property that returns the value of the private property _randomMealLiveData

    private val _popularMealsLiveData = MutableLiveData<List<PopularMeal>?>()
    val popularMealsLiveData: LiveData<List<PopularMeal>?>
        get() = _popularMealsLiveData // This is a read-only property that returns the value of the private property _popularMealsLiveData

    private val _categoriesLiveData = MutableLiveData<List<Category>?>()
    val categoriesLiveData: LiveData<List<Category>?>
        get() = _categoriesLiveData // This is a read-only property that returns the value of the private property _categoriesLiveData

    private val _favouriteMealsLiveData = mealDatabase.getMealDao()
        .getAllMeals() // This will get all the meals from the database and store it in the _favouriteMealsLiveData
    val favouriteMealsLiveData: LiveData<List<Meal>>
        get() = _favouriteMealsLiveData // This is a read-only property that returns the value of the private property _favouriteMealsLiveData

    private val _mealBottomSheetDialogLiveData = MutableLiveData<Meal>()
    val mealBottomSheetDialogLiveData: LiveData<Meal>
        get() = _mealBottomSheetDialogLiveData // This is a read-only property that returns the value of the private property _bottomSheetMealLiveData

    private val _searchedMealLiveData = MutableLiveData<List<Meal>?>()
    val searchedMealLiveData: LiveData<List<Meal>?>
        get() = _searchedMealLiveData // This is a read-only property that returns the value of the private property _searchedMealLiveData

    init { // If the activity is recreated, the init block will be called again but if the activity is not recreated, the init block will not be called again (handle fragment recreation)
        getRandomMeal() // Get a random meal when the HomeViewModel is created
    }

    private fun getRandomMeal() { // This function will make the API call to get a random meal
        RetrofitInstance.mealApi.getRandomMeal()
            .enqueue(object : Callback<MealList> { // Make the API call
                override fun onResponse(call: Call<MealList>, response: Response<MealList>) {

                    if (response.isSuccessful) { // If the response is successful, get the list of meals from the response body
                        val randomMealList =
                            response.body()?.meals // The response body is a MealList object
                        _randomMealLiveData.value =
                            randomMealList?.get(0)// Set the value of the randomMealLiveData to the first meal in the list (the random meal)
                    }
                }

                override fun onFailure(
                    call: Call<MealList>,
                    t: Throwable
                ) { // If the API call fails, log the error message
                    Log.d("HomeFragment_getRandomMeal()", t.message.toString())
                }
            })
    }

    fun getPopularMeals() { // This function will make the API call to get the popular meals
        RetrofitInstance.mealApi.getPopularMeals("Seafood") // We will use the category "Seafood" to get the popular meals because the MealDB API for popular meals is not free
            .enqueue(object : Callback<PopularMealList> { // Make the API call
                override fun onResponse(
                    call: Call<PopularMealList>,
                    response: Response<PopularMealList>
                ) {
                    if (response.isSuccessful) { // If the response is successful, get the list of meals from the response body
                        val popularMealList =
                            response.body()?.meals // The response body is a CategoryList object
                        _popularMealsLiveData.value =
                            popularMealList // Set the value of the popularMealsLiveData to the list of popular meals

                    }
                }

                override fun onFailure(
                    call: Call<PopularMealList>,
                    t: Throwable
                ) { // If the API call fails, log the error message
                    Log.d("HomeFragment_getPopularMeals()", t.message.toString())
                }
            })
    }

    fun getCategories() { // This function will make the API call to get all the categories
        RetrofitInstance.mealApi.getCategories()
            .enqueue(object : Callback<CategoryList> { // Make the API call
                override fun onResponse(
                    call: Call<CategoryList>,
                    response: Response<CategoryList>
                ) {
                    if (response.isSuccessful) { // If the response is successful, get the list of meals from the response body
                        val categoryList =
                            response.body()?.categories// The response body is a CategoryList object
                        _categoriesLiveData.value =
                            categoryList // Set the value of the categoriesLiveData to the list of categories
                    }
                }

                override fun onFailure(
                    call: Call<CategoryList>,
                    t: Throwable
                ) { // If the API call fails, log the error message
                    Log.d("HomeFragment_getCategories()", t.message.toString())
                }
            })
    }

    fun getMealById(id: String) { // This function will make the API call to get a meal by its ID
        RetrofitInstance.mealApi.getMealById(id)
            .enqueue(object : Callback<MealList> { // Make the API call
                override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                    if (response.isSuccessful) { // If the response is successful, get the list of meals from the response body
                        val mealList =
                            response.body()?.meals // The response body is a MealList object
                        _mealBottomSheetDialogLiveData.value =
                            mealList?.get(0) // Set the value of the bottomSheetMealLiveData to the first meal in the list (the meal with the specified ID)
                    }
                }

                override fun onFailure(
                    call: Call<MealList>,
                    t: Throwable
                ) { // If the API call fails, log the error message
                    Log.d("HomeFragment_getMealById()", t.message.toString())
                }
            })
    }

    fun searchMealByName(name: String) { // This function will make the API call to search a meal by its name
        RetrofitInstance.mealApi.searchMealByName(name)
            .enqueue(object : Callback<MealList> { // Make the API call
                override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                    if (response.isSuccessful) { // If the response is successful, get the list of meals from the response body
                        val mealList =
                            response.body()?.meals // The response body is a MealList object
                        _searchedMealLiveData.value =
                            mealList // Set the value of the searchedMealLiveData to the first meal in the list (the meal with the specified name)
                    }
                }

                override fun onFailure(
                    call: Call<MealList>,
                    t: Throwable
                ) { // If the API call fails, log the error message
                    Log.d("HomeFragment_searchMealByName()", t.message.toString())
                }
            })
    }

    fun deleteMealFromDb(meal: Meal) { // This function will delete the meal from the database
        viewModelScope.launch { // viewModelScope is used to launch a coroutine in the ViewModel
            mealDatabase.getMealDao()
                .deleteMeal(meal) // deleteMeal is used to delete the meal from the database
        }
    }

    fun insertMealIntoDb(meal: Meal) { // This function will insert the meal into the database
        viewModelScope.launch { // viewModelScope is used to launch a coroutine in the ViewModel
            mealDatabase.getMealDao()
                .upsertMeal(meal) // upsertMeal is used to update or insert the meal into the database
        }
    }
}