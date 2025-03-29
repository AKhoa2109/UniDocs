plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.anhkhoa.projectwebsitebantailieu"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.anhkhoa.projectwebsitebantailieu"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit) // Retrofit core
    implementation(libs.converterGson) // Convert JSON sang Object Java
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.viewpager2)
    implementation(libs.circleimageview)
    implementation(libs.dotsindicator)
    implementation(libs.stomp)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.protolite.well.known.types)
    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.glideCompiler)
}