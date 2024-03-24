package com.example.mygithublist.ui.main

import org.junit.*

class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun before() {
        mainViewModel = MainViewModel()
    }

    @Test
    fun testGetSearchUser() {
        mainViewModel = MainViewModel()
        mainViewModel.getSearchUser()
    }

}