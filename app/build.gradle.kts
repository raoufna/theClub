import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.unimib.wardrobe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.unimib.wardrobe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        resValue("string", "rapidapi_key", gradleLocalProperties(rootDir, providers).getProperty("rapidapi_key"))
        resValue("bool", "debug_mode", gradleLocalProperties(rootDir, providers).getProperty("debug_mode"))

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation (libs.commons.validator) //libreria per check email
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.gson)

    implementation(libs.room.runtime) //librerie per roomDatabase
    annotationProcessor(libs.room.compiler)

    implementation(libs.glide)
    implementation(libs.compiler) //librerie per Glide

    implementation(libs.retrofit) //librerie per implementare retrofit e convertiore gson
    implementation(libs.converter.gson)

    implementation(platform(libs.firebase.bom))//librerie per firebase
    implementation(libs.firebase.auth) //libreria autenticazione firebase
    implementation(libs.play.services.auth)
    implementation (libs.firebase.database)
}