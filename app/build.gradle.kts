plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.myapplication45"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication45"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true

    }
}

dependencies {
    // Paypal
    implementation("com.paypal.sdk:paypal-android-sdk:2.16.0")
    // QR code
    implementation("com.google.zxing:core:3.2.1")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation("org.greenrobot:eventbus:3.0.0")
    implementation("com.google.android.exoplayer:exoplayer:2.7.2")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.afollestad.material-dialogs:core:0.9.6.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.wefika:flowlayout:0.4.1") {
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}