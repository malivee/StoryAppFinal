package com.belajar.storyapp.view.main

import android.Manifest
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.belajar.storyapp.helper.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import com.belajar.storyapp.R
import androidx.test.rule.GrantPermissionRule
import com.belajar.storyapp.view.home.HomepageActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        release()
    }

    @Test
    fun loginLogout_Success() {
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email)).perform(typeText("qwerty@msn.com"))

        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).perform(typeText("qwertyuiop"))

        onView(withId(R.id.btn_log_login)).check(matches(isEnabled()))
        onView(withId(R.id.btn_log_login)).perform(click())
        Thread.sleep(5000L)
        intended(hasComponent(HomepageActivity::class.java.name))

        onView(withId(R.id.btn_setting)).perform(click())

        onView(withId(R.id.btn_logout)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_logout)).perform(click())
    }

}