# Add project-specific Gson rules to keep configurations
-keep class com.aiswarya.wordconnections.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.aiswarya.wordconnections.data.remote.dto.** { *; }
-keepattributes serialization.*Annotation.*

# Keep Hilt dependencies
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keepattributes.*Annotation.*
-keepattributes InnerClasses

# Keep Room entities and DAOs
-keep class com.aiswarya.wordconnections.data.local.entity.** { *; }
-keep class com.aiswarya.wordconnections.data.local.dao.** { *; }

# Keep Retrofit interfaces
-keep class com.aiswarya.wordconnections.data.remote.api.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-keepattributes *Annotation*

# Keep serialization classes
-keep class kotlinx.serialization.** { *; }
-keepattributes kotlinx.serialization.SerializedName