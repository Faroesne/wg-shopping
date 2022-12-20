package com.example.communityshopping.mainActivity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.communityshopping.R

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class MainActivityTest
{
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_isViewCorrect() {
        onView(withId(R.id.nav_host_fragment_activity_main)).check(matches(isDisplayed()))
    }
}