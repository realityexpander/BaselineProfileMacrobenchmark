# BaselineProfileMacrobenchmark
Android App to show how to improve launch performance using benchmarks and baseline profiles

0. Create regular tests that run on regular device

1. Run this test to generate a baseline profile (baseline-prof.txt)
    In Run configurations, add the following to the Gradle options:
    Gradle Project: BaselineProfileMacrobenchmark
    ./gradlew :benchmark:pixel2Api31BenchmarkAndroidTest --rerun-tasks -P android.testInstrumentationRunnerArguments.class=com.realityexpander.benchmark.BaselineProfileGenerator
    Run that.
2. Copy benchmark/build/outputs/managed_device_android_test_additional_output/pixel2Api31/BaselineProfileGenerator_generateBaselineProfile-baseline-prof.txt
    to /app/baseline-prof.txt
   Note: may have to `Clean Build` and run this twice. (DON'T ASK ME WHY)
 
3. Run Benchmark Test - ExampleStartupBenchmark2
   Note: this must be run on a real device.
