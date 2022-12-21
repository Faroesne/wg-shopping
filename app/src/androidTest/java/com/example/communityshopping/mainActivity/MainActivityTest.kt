package com.example.communityshopping.mainActivity

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.communityshopping.R
import com.example.communityshopping.mainActivity.archive.models.Archive
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.AllOf.allOf
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test1_isViewCorrect() {
        onView(withId(R.id.nav_host_fragment_activity_main)).check(matches(isDisplayed()))
    }

    @Test
    fun test2_addItem() {
        val testName = "Eier"
        onView(withId(R.id.btn_addItem)).perform(click())
        onView(withId(R.id.nameEdit)).perform(typeText(testName))
        Espresso.closeSoftKeyboard()
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())

    }

    @Test
    fun test3_removeItem() {
        onView(allOf(withId(R.id.checkbox), isDescendantOfA(withId(R.id.containerList)))).perform(
            click()
        )
        onView(withId(R.id.btn_delete)).perform(click())
    }

    @Test
    fun test4_navigateToSecondActivity() {
        val testName = "Eier"
        onView(withId(R.id.btn_addItem)).perform(click())
        onView(withId(R.id.nameEdit)).perform(typeText(testName))
        Espresso.closeSoftKeyboard()
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        onView(allOf(withId(R.id.checkbox), isDescendantOfA(withId(R.id.containerList)))).perform(
            click()
        )
        onView(withId(R.id.btn_submit)).perform(click())
        onView(withId(R.id.scroll)).check(matches(isDisplayed()))
    }

    @Test
    fun test5_navigateToArchive() {
        val price = "8,97€"
        onView(withId(R.id.navigation_archive)).perform(click())
        onData(allOf(instanceOf(Archive::class.java))).atPosition(0).perform(click())
        onView(withId(R.id.archive_fullPrice)).check(matches(withText(price)))
    }
}