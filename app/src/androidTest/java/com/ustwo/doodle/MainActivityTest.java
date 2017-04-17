package com.ustwo.doodle;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Instrumentation test for the main screen, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule
            = new ActivityTestRule<MainActivity>(MainActivity.class);


    /**
     * Shows the paint colour picker dialog, then check its title
     * @throws Exception
     */
    @Test
    public void showPaintColorPicker() throws Exception {
        // click the paint color picker
        onView(withId(R.id.navigation_set_paint_color)).perform(click());
        onView(withId(R.id.tvTitle)).check(matches(withText(containsString("Choose a paint color"))));
    }

    /**
     * Shows the background colour picker dialog, then check its title
     * @throws Exception
     */
    @Test
    public void showBackgroundColorPicker() throws Exception {
        onView(withId(R.id.navigation_set_background_color)).perform(click());
        onView(withId(R.id.tvTitle)).check(matches(withText(containsString("Choose a background color"))));
    }

    /**
     * Mimics the action for swiping the screen
     * @param x - start X position in DoodleCanvas
     * @param y - start X position in DoodleCanvas
     * @return
     */
    public static ViewAction swipeScreen(final float x, final float y) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DoodleCanvas.class);
            }

            @Override
            public String getDescription() {
                return "swipeScreen screen by touch ";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                // Get view absolute position
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                float startX =  x + location[0];
                float startY =  y + location[1];
                float[] coordinates = new float[] { startX, startY };
                float[] precision = new float[] { 1f, 1f };

                // touch down, move and up
                MotionEvent down = MotionEvents.sendDown(uiController, coordinates, precision).down;
                final int loopCount = 50;
                int unitX = 500/loopCount;
                int unitY = 500/loopCount;
                for(int i = 0; i < loopCount; i++) {
                    coordinates[0] = startX + unitX * i;
                    coordinates[1] = startY + unitY * i;
                    MotionEvents.sendMovement(uiController, down, coordinates);
                    uiController.loopMainThreadForAtLeast(20);
                }
                MotionEvents.sendUp(uiController, down, coordinates);
            }
        };
    }

    /**
     * Save the image after drawing line
     * @throws Exception
     */
    @Test
    public void saveDoodle() throws Exception {
        onView(withId(R.id.viewDoodleCanvas)).perform(swipeScreen(100, 100));
        onView(withId(R.id.navigation_save_doodle)).perform(click());
    }

    @Test
    /**
     * Set the wallpaper after drawing line
     * @throws Exception
     */
    public void setAsWallpaper() throws Exception {
        onView(withId(R.id.viewDoodleCanvas)).perform(swipeScreen(500, 200));
        onView(withId(R.id.navigation_set_as_wallpaper)).perform(click());
    }

}