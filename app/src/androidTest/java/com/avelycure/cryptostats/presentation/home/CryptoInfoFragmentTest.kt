package com.avelycure.cryptostats.presentation.home

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.avelycure.cryptostats.R
import com.avelycure.cryptostats.presentation.MainActivity
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.isRoot

import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
class CryptoInfoFragmentTest : TestCase() {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java, null)

    @Test
    fun shouldBeVisible() {
        onView(isRoot()).perform(waitFor(3000))

        onView(withId(R.id.ci_tv_coin_value)).check(matches(isDisplayed()))
        onView(withId(R.id.ci_tv_percent_change_in_last_24h)).check(matches(isDisplayed()))
        onView(withId(R.id.ci_tv_lowest_in_last_24h)).check(matches(isDisplayed()))
        onView(withId(R.id.ci_tv_highest_in_last_24h)).check(matches(isDisplayed()))

        onView(withId(R.id.tv24low)).check(matches(isDisplayed()))
        onView(withId(R.id.tv24percent)).check(matches(isDisplayed()))
        onView(withId(R.id.tv24high)).check(matches(isDisplayed()))
    }
}

fun waitFor(millis: Long): ViewAction? {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "Wait for $millis milliseconds."
        }

        override fun perform(uiController: UiController, view: View?) {
            uiController.loopMainThreadForAtLeast(millis)
        }
    }
}

object HasRedColor {
    operator fun invoke(): ColorTextMatcher {
        return ColorTextMatcher.hasColorText(Color.RED)
    }
}

object HasGreenColor {
    operator fun invoke(): ColorTextMatcher {
        return ColorTextMatcher.hasColorText(Color.GREEN)
    }
}


class ColorTextMatcher(
    private val color: Int
) : TypeSafeMatcher<View>() {
    companion object Factory {
        fun hasColorText(color: Int): ColorTextMatcher {
            return ColorTextMatcher(color)
        }
    }

    override fun describeTo(description: Description) {
        description.appendText("has red color")
    }

    override fun matchesSafely(item: View): Boolean {
        if (item is TextView)
            return item.currentTextColor == color
        return false
    }

}