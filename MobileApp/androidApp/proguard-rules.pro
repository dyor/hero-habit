# Add project specific ProGuard rules here.

# Navigation 3 Routes & Serializers
-keep class **ScreenRoute { *; }
-keep class **ScreenRoute$Companion { *; }
-keepclassmembers class **ScreenRoute {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class **ScreenRoute$$serializer { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class com.dyor.habithero.** {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * implements kotlinx.serialization.internal.GeneratedSerializer {
    *;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep class com.dyor.habithero.data.source.remote.** { *; }
-keep class com.dyor.habithero.domain.model.** { *; }
-keep class com.dyor.habithero.data.source.local.entity.** { *; }

# Koin & ViewModels (Prevent R8 from stripping constructor reflection)
-keep class org.koin.** { *; }
-dontwarn org.koin.**
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class com.dyor.habithero.data.repository.** {
    <init>(...);
}

# Adapty SDK
-keep class com.adapty.** { *; }
-dontwarn com.adapty.**

# Coil & FileKit
-keep class coil3.** { *; }
-keep class io.github.vinceglb.filekit.** { *; }
