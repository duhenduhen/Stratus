plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.duhen.stratus"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "org.duhen.stratus"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                          "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.activity:activity:1.13.0")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("com.crossbowffs.remotepreferences:remotepreferences:0.8")
    implementation("com.github.topjohnwu.libsu:core:6.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)

    compileOnly("de.robv.android.xposed:api:82")
}
