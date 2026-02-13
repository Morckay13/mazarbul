// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // ✅ Declaramos KSP con versión, pero NO lo aplicamos aquí
    id("com.google.devtools.ksp") version "2.0.21-1.0.26" apply false
}
