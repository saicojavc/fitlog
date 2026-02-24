# --- Reglas Base de Android y Kotlin ---
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**

# --- Jetpack Compose ---
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.ui.platform.** { *; }

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler {
    <init>(...);
}

# --- Dagger Hilt ---
-keep class com.saico.** { *; }
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# --- Room Database ---
-dontwarn androidx.room.paging.**

# --- DataStore / Moshi / Serialization ---
-keep class com.squareup.moshi.** { *; }
-keepnames class com.saico.core.model.** { *; }
-keep class com.saico.core.model.** { *; }
-keepclassmembers class com.saico.core.model.** {
    <fields>;
    <init>(...);
}

# --- Firebase & Google Auth (CRÍTICO PARA RELEASE) ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Reglas específicas para Credential Manager y Google ID
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }
-dontwarn androidx.credentials.**

# --- Reglas Específicas del Proyecto Fitlog ---
-keep class com.saico.core.model.** { *; }
-keep class com.saico.core.database.entity.** { *; }
-keep class com.saico.core.database.dao.** { *; }

# Optimización: eliminar logs en release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
