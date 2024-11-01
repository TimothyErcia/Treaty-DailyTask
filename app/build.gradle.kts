plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.realmAndroid)
    alias(libs.plugins.kotlinKover)
}

android {
    namespace = "com.treaty.dailytask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.treaty.dailytask"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.koin)
    implementation(libs.koin.android)
    implementation(libs.library.base)
    implementation(libs.library.sync)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.mockito.mockito.core)
    testImplementation(libs.mockito.mockito.kotlin)

    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockito.inline)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kover {
    reports {
        // filters for all report types of all build variants
        filters {
            excludes {
                androidGeneratedClasses()
                classes(
                    // excludes debug classes
                    "*.NetworkUtility",
                    "*.TaskGroupRepository",
                    "*.ReminderRepository",
                    "*.AlarmUtility",
                    "*.AlarmReceivers",
                    "*.LocalNotificationUtility"
                )
            }
        }

        variant("debug") {
            // verification only for 'debug' build variant
            verify {
                rule {
                    minBound(80)
                }
            }
        }
    }
}