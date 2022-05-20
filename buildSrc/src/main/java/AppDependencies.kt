object Versions {
    const val CORE_KTX = "1.7.0"
    const val APP_COMPAT = "1.4.1"
    const val LIFECYCLE_KTX = "2.4.1"
    const val NAVIGATION_KTX = "2.4.1"
    const val MATERIAL = "1.5.0"
    const val CONSTRAINT_LAYOUT = "2.1.3"
    const val COROUTINES = "1.5.2"

    const val JUNIT = "4.13.2"
    const val ANDROIDX_TEST = "1.4.0"
    const val ANDROIDX_TEST_JUNIT = "1.1.3"
    const val ESPRESSO = "3.4.0"
    const val ROBOLECTRIC = "4.5.1"
    const val ARCH_TEST = "2.1.0"
    const val MOCKITO = "4.3.1"

    const val ROOM = "2.4.1"
    const val GLIDE = "4.13.0"
    const val HILT_CORE = "2.41"
    const val GSON = "2.9.0"
    const val VIEW_PAGER = "1.0.0"
    const val DOTS_INDICATOR = "4.2"
    const val LOTTIE = "5.0.3"
    const val RETROFIT2 = "2.9.0"
    const val OKHTTP = "4.3.1"
    const val TIMBER = "5.0.1"

    const val GOOGLE_SERVICE = "4.3.10"
    const val GOOGLE_SERVICE_ADS = "20.6.0"
    const val FIREBASE_BOM = "29.2.1"
    const val FIREBASE_CRASHLYTICS_PLUGIN = "2.8.1"
}

object Libraries {
    const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    const val LIFECYCLE_VIEWMODEL =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE_KTX}"
    const val LIFECYCLE_LIVEDATA =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE_KTX}"
    const val NAVIGATION_FRAGMENT =
        "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_KTX}"
    const val NAVIGATION_UI = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION_KTX}"
    const val GSON = "com.google.code.gson:gson:${Versions.GSON}"
    const val GOOGLE_SERVICE_ADS =
        "com.google.android.gms:play-services-ads:${Versions.GOOGLE_SERVICE_ADS}"
    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"
}

object Ui {
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val CONSTRAINT_LAYOUT =
        "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"
    const val VIEW_PAGER = "androidx.viewpager2:viewpager2:${Versions.VIEW_PAGER}"
    const val DOTS_INDICATOR = "com.tbuonomo:dotsindicator:${Versions.DOTS_INDICATOR}"
    const val LOTTIE = "com.airbnb.android:lottie:${Versions.LOTTIE}"
}

object Di {
    const val HILT_ANDROID = "com.google.dagger:hilt-android:${Versions.HILT_CORE}"
    const val HILT_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.HILT_CORE}"
}

object Coroutines {
    const val COROUTINES_CORE =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    const val COROUTINES_TEST =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"
}

object Room {
    const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
    const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"
}

object Firebase {
    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"
    const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"
}

object Retrofit {
    const val RETROFIT2 = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT2}"
    const val RETROFIT_CONVERTER_SCALARS =
        "com.squareup.retrofit2:converter-scalars:${Versions.RETROFIT2}"
    const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
    const val OKHTTP_LOGGING_INTERCEPTOR =
        "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP}"
}

