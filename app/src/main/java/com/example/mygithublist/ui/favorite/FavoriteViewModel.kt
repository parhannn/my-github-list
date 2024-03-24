package com.example.mygithublist.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.mygithublist.data.local.FavoriteUser
import com.example.mygithublist.data.local.FavoriteUserDao
import com.example.mygithublist.data.local.UserRoomDatabase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private var favoriteUserDao: FavoriteUserDao?
    private var userRoomDatabase: UserRoomDatabase?

    init {
        userRoomDatabase = UserRoomDatabase.getDatabase(application)
        favoriteUserDao = userRoomDatabase?.favoriteUserDao()
    }

    fun getFavoriteUser(): LiveData<List<FavoriteUser>>? {
        return favoriteUserDao?.getFavoriteUser()
    }
}