// Root build file. Plugins are declared here (applied `false`) and
// actually applied per-module in app/build.gradle.kts, which is the
// standard Gradle version-catalog setup for a single-module app.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
