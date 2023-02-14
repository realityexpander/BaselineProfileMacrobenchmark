package com.realityexpander.benchmark

import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */

// 1. Run this test to generate a baseline profile (baseline-prof.txt)
//    In Run configurations, add the following to the Gradle options:
//    Gradle Project: BaselineProfileMacrobenchmark
//    ./gradlew :benchmark:pixel2Api31BenchmarkAndroidTest --rerun-tasks -P android.testInstrumentationRunnerArguments.class=com.realityexpander.benchmark.BaselineProfileGenerator
//    Run that.
// 2. Copy benchmark/build/outputs/managed_device_android_test_additional_output/pixel2Api31/BaselineProfileGenerator_generateBaselineProfile-baseline-prof.txt
//    to /app/baseline-prof.txt
// Note: may have to `Clean Build` and run this twice. (DON'T ASK ME WHY)
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @OptIn(ExperimentalBaselineProfilesApi::class)
    @get:Rule
    val baselineRule = BaselineProfileRule()

    @OptIn(ExperimentalBaselineProfilesApi::class)
    @Test
    fun generateBaselineProfile() = baselineRule.collectBaselineProfile(
        packageName = "com.realityexpander.baselineprofilemacrobenchmark",
    ) {
        pressHome()
        startActivityAndWait()

        addElementsAndScrollDown()
    }
}

// Regular tests
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.realityexpander.baselineprofilemacrobenchmark",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun scrollAndNavigate() = benchmarkRule.measureRepeated(
        packageName = "com.realityexpander.baselineprofilemacrobenchmark",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()

        addElementsAndScrollDown()
    }
}


// Important to use real device for this test
// 3. Run test to check against benchmark
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark2 {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()


    ////////////////////////////
    // Worst case scenario
    @Test
    fun startUpCompilationModeNone() = startup(CompilationMode.None())

    @Test
    fun scrollAndNavigateCompilationModeNone() = scrollAndNavigate(CompilationMode.None())


    ////////////////////////////
    // Case where we are using a baseline profile
    @Test
    fun startUpCompilationModePartial() = startup(CompilationMode.Partial())

    @Test
    fun scrollAndNavigateCompilationModePartial() = scrollAndNavigate(CompilationMode.Partial())


    ////////////////////////////
    // Tests

    fun startup(mode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.realityexpander.baselineprofilemacrobenchmark",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = mode
    ) {
        pressHome()
        startActivityAndWait()
    }

    fun scrollAndNavigate(mode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.realityexpander.baselineprofilemacrobenchmark",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = mode
    ) {
        pressHome()
        startActivityAndWait()

        addElementsAndScrollDown()
    }
}

fun MacrobenchmarkScope.addElementsAndScrollDown() {
    val button = device.findObject(By.text("Click me"))
    val list = device.findObject(By.res("item_list"))

    repeat(30) {
        button.click()
    }

    device.waitForIdle()

    list.setGestureMargin(device.displayWidth / 5)
    list.fling(Direction.DOWN)

    device.waitForIdle()

    device.findObject(By.text("Element 29")).click()

    device.wait(Until.hasObject(By.text("Detail: Element 29")), 5000)
}