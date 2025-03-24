plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.unimib.wardrobe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unimib.wardrobe"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation (libs.commons.validator) //libreria per check email
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.gson)

    implementation(libs.room.runtime) //librerie per roomDatabase
    annotationProcessor(libs.room.compiler)

    implementation(libs.glide)
    implementation(libs.compiler) //librerie per Glide

    implementation(libs.retrofit)
}