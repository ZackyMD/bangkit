plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.submissionstoryappintermediate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.submissionstoryappintermediate"
        minSdk = 21
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.android.async.http)
    implementation (libs.gson)
    implementation (libs.retrofit2.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.room.runtime)
    implementation(libs.logging.interceptor)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.glide)
    implementation (libs.picasso)
    implementation (libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.room.external.antlr)
    implementation("junit:junit:4.12")
    implementation(libs.androidx.lifecycle.runtime.testing)
    annotationProcessor (libs.glide.compiler)
    kapt(libs.androidx.room.compiler)
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.mockito.inline)
    implementation("org.mockito:mockito-core:5.7.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    implementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.paging:paging-common:3.3.0")
    implementation("androidx.paging:paging-common:3.3.0")
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    testImplementation("androidx.paging:paging-common-ktx:3.1.1")
    implementation("androidx.paging:paging-common-ktx:3.1.1")
    testImplementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    testImplementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}