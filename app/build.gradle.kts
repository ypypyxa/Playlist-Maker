plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 34
        versionCode = 13
        versionName = "0.2.1"

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

    // Работа с базой данных (Database Handling):
    // Room Compiler использует KAPT (Kotlin Annotation Processing Tool)
    // для генерации кода на основе аннотаций, таких как @Entity, @Dao и @Database.
    kapt(libs.androidx.room.compiler)
    // Room Runtime — основная библиотека,
    // необходимая для выполнения операций с базой данных во время работы приложения.
    implementation(libs.androidx.room.runtime)
    // Room KTX — набор Kotlin-расширений для Room, упрощающих использование API и делая код более идиоматичным для Kotlin.
    implementation(libs.androidx.room.ktx)

    // Навигация (Navigation)
    // Kotlin-расширения для библиотеки навигации, упрощающие работу с фрагментами.
    implementation(libs.androidx.navigation.fragment.ktx)
    // Kotlin-расширения для навигации в пользовательском интерфейсе.
    implementation(libs.androidx.navigation.ui.ktx)

    // Фрагменты (Fragments)
    //Kotlin-расширения для работы с фрагментами, облегчающие использование API.
    implementation(libs.androidx.fragment.ktx)

    // Внедрение зависимостей (Dependency Injection)
    // Koin — легковесный фреймворк для внедрения зависимостей в Kotlin/Android приложениях.
    implementation(libs.koin.android)

    // Базовые библиотеки AndroidX
    // Kotlin-расширения для базовых классов Android, упрощающие разработку.
    implementation(libs.androidx.core.ktx)
    // Обеспечивает обратную совместимость компонентов интерфейса на старых версиях Android.
    implementation(libs.androidx.appcompat)

    // Компоненты пользовательского интерфейса (UI Components)
    // Библиотека компонентов Material Design от Google.
    implementation(libs.material)
    // Лайаут для создания гибких и сложных интерфейсов.
    implementation(libs.androidx.constraintlayout)

    // Загрузка и обработка изображений (Image Loading and Processing)
    // Библиотека для загрузки и кэширования изображений.
    implementation(libs.glide)
    // Компилятор для Glide, необходимый для генерации кода.
    annotationProcessor(libs.compiler)

    // Работа с сетью и данными (Networking and Data Handling)
    // Клиент для HTTP-запросов, упрощающий работу с REST API.
    implementation(libs.retrofit2.retrofit)
    // Конвертер для Retrofit, использующий Gson для сериализации/десериализации JSON.
    implementation(libs.converter.gson)
    // Библиотека для работы с JSON, преобразование объектов Java в JSON и обратно.
    implementation(libs.gson)

    // Тестирование (Testing)
    // JUnit 4 для написания модульных тестов.
    testImplementation(libs.junit)
    // Расширения JUnit для Android, поддержка инструментальных тестов.
    androidTestImplementation(libs.androidx.junit)
    // Espresso для автоматизированного UI-тестирования.
    androidTestImplementation(libs.androidx.espresso.core)
}