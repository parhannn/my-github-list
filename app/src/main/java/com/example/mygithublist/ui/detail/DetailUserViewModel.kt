package com.example.mygithublist.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mygithublist.api.RetrofitClient
import com.example.mygithublist.data.local.FavoriteUser
import com.example.mygithublist.data.local.FavoriteUserDao
import com.example.mygithublist.data.local.UserRoomDatabase
import com.example.mygithublist.data.model.DetailUserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(application: Application) : AndroidViewModel(application) {
    val user = MutableLiveData<DetailUserResponse>()
    private var favoriteUserDao: FavoriteUserDao?
    private var userRoomDatabase: UserRoomDatabase?

    init {
        userRoomDatabase = UserRoomDatabase.getDatabase(application)
        favoriteUserDao = userRoomDatabase?.favoriteUserDao()
    }

    fun setUserDetail(username: String?) {
        RetrofitClient.apiInstance
            .getUserDetail(username)
            .enqueue(object : Callback<DetailUserResponse> {
                override fun onResponse(
                    call: Call<DetailUserResponse>,
                    response: Response<DetailUserResponse>
                ) {
                    if (response.isSuccessful) {
                        user.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                    t.message?.let {
                        Log.d("Failure", it)
                    }
                    onCleared()
                }

            })
    }

    fun getUserDetail(): LiveData<DetailUserResponse> {
        return user
    }

    fun addToFavorite(username: String, id: Int, avatarUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteUser = FavoriteUser(
                username,
                id,
                avatarUrl
            )
            favoriteUserDao?.addToFavorite(favoriteUser)
        }
    }

    suspend fun checkUser(id: Int) = favoriteUserDao?.checkUser(id)

    fun removeFromFavorite(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            favoriteUserDao?.removeFromFavorite(id)
        }
    }
}