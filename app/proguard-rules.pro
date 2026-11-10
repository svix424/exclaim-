# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt
-keep,allowobfuscation,allowshrinking class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Prevent stripping domain model classes used via reflection
-keep class com.cycletracker.app.data.db.entity.** { *; }
-keep class com.cycletracker.app.domain.model.** { *; }
