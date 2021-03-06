// Copyright 2018-2020 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.mopub.framework.base;

import androidx.annotation.NonNull;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;

import com.mopub.framework.pages.AdListPage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.fail;

public class BasePage {
    private static final int DEFAULT_TIMEOUT_SECS = 10;
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final int SAMPLE_TIME_MS = 200;
    private static final int SAMPLES_PER_SEC = 5;

    protected final String ADD_AD_UNIT_LABEL = "New ad unit";

    public void quickClickElement(@NonNull final ViewInteraction element) {
        element.perform(click());
    }

    public void clickElementWithText(@NonNull final String text, @NonNull final boolean isStrict) {
        final String failMessage = "This element with text '" + text + "' is not present";

        final ViewInteraction element = isStrict ?
                onView(withText(text)) :
                onView(withText(containsString(text)));
        clickElement(element, failMessage);
    }

    public void clickElementWithText(@NonNull final String text) {
        clickElementWithText(text, true);
    }

    public void clickElementWithId(final int id) {
        final ViewInteraction element = onView(withId(id));
        final String failMessage = "This element with id '" + id + "' is not present";

        clickElement(element, failMessage);
    }

    public void clickElement(@NonNull final ViewInteraction element) {
        clickElement(element, null);
    }

    public void clickElement(@NonNull final ViewInteraction element, @NonNull final String failMessage) {
        final String message = (failMessage != null) ?
                failMessage :
                "This element is not present";

        if (waitForElement(element)) {
            element.perform(click());
            return;
        }

        fail(message);
    }

    public void clickElementWithResource(@NonNull final String resName) {
        final ViewInteraction element = onView(withResourceName(resName));
        final String failMessage = "This element with resource name '" + resName + "' is not present";

        clickElement(element, failMessage);
    }

    public boolean waitForElement(@NonNull final ViewInteraction element) {
        return waitForElement(element, DEFAULT_TIMEOUT_SECS);
    }

    public boolean waitForElement(@NonNull final ViewInteraction element, final int timeoutInSeconds) {
        int i = 0;
        while (i++ < timeoutInSeconds * SAMPLES_PER_SEC) {
            try {
                element.check(matches(isEnabled()));
                return true;
            } catch (Throwable e) {
                try {
                    Thread.sleep(SAMPLE_TIME_MS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public static void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    public AdListPage goToHome() {
        ViewInteraction element = onView(withText(ADD_AD_UNIT_LABEL));

        if (!waitForElement(element, 1)) {
            pressBack();
            goToHome();
        }

        return new AdListPage();
    }
}
