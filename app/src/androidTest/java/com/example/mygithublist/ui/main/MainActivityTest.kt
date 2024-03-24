package com.example.mygithublist.ui.main

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.mygithublist.R
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    private val dummyQeury = "parhannn"

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun assertGetUser() {
        onView(withId(R.id.et_query)).perform(typeText(dummyQeury), closeSoftKeyboard())
        onView(withId(R.id.btn_search)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }
}