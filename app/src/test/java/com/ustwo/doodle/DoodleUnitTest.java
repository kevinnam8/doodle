package com.ustwo.doodle;


import android.content.Context;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Unit test for Doodle
 * @author Kevin Nam
 */
@RunWith(MockitoJUnitRunner.class)
public class DoodleUnitTest {
    @Mock Context mMockContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mMockContext = new MockContext();
    }

    /**
     * Tests {@link DoodleCanvas}'s get/set paint color methods
     * @throws Exception - to be reported in the result doc
     */
    @Test
    public void paintColor_isCorrect() throws Exception {
        final int TEST_COLOR = 0xFF00FF;

        DoodleCanvas doodleCanvas = new DoodleCanvas(mMockContext);

        doodleCanvas.setPaintColor(TEST_COLOR);
        assertThat(doodleCanvas.getPaintColor(), is(TEST_COLOR));
    }
}
