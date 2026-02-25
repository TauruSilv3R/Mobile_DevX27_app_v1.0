// Root build.gradle.kts — plugin declarations only (no apply at top level)
plugins {
    alias(libs.plugins.android.application)    apply false
    alias(libs.plugins.kotlin.android)         apply false
    alias(libs.plugins.kotlin.compose)         apply false
    alias(libs.plugins.kotlin.serialization)   apply false
    alias(libs.plugins.ksp)                    apply false
    alias(libs.plugins.hilt)                   apply false
    alias(libs.plugins.google.services)        apply false
}

// ─────────────────────────────────────────────────────────────────────────────
// Custom Gradle Task: exportApk  (v1.4 — FullInput)
// ─────────────────────────────────────────────────────────────────────────────
val exportApk by tasks.registering {
    group       = "DevX27"
    description = "Assembles the debug APK and exports it to /build-output/DevX27_v1.4_FullInput.apk"
    dependsOn(":app:assembleDebug")
    doLast {
        val outputDir = rootProject.file("build-output")
        outputDir.mkdirs()
        val apkSource = rootProject.file("app/build/outputs/apk/debug/app-debug.apk")
        val apkDest   = File(outputDir, "DevX27_v1.4_FullInput.apk")
        if (apkSource.exists()) {
            apkSource.copyTo(apkDest, overwrite = true)
            println("✅ APK exported → ${apkDest.absolutePath}")
        } else {
            println("⚠️  Debug APK not found. Run assembleDebug first.")
        }
    }
}
