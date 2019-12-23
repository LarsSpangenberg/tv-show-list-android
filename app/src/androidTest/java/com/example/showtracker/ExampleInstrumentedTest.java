package com.example.showtracker;

import android.content.*;

import androidx.test.platform.app.*;
import androidx.test.runner.*;

import org.junit.*;
import org.junit.runner.*;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.showtracker", appContext.getPackageName());
    }
}
