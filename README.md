<h1 align="center">🏥 KHealth 🏥</h1>

<p align="center">
    <img src="assets/logo.svg" width="200"  alt=""/>
</p>

<p align="center">
    <img src="http://img.shields.io/badge/-android-6EDB8D.svg?style=flat" />
    <img src="http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat" />
    <img src="http://img.shields.io/badge/-wearos-8ECDA0.svg?style=flat" />
</p>

**KHealth** (_short for Kotlin Health_) is a simple Kotlin Multiplatform wrapper over Android's [Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect) and Apple's [HealthKit](https://developer.apple.com/documentation/healthkit) APIs. It provides a simple and effective way to consume these native APIs in a Kotlin/Compose Multiplatform environment.

## Demo

> [!NOTE]  
> You can find the following app in the `sample*` directories (e.g. `sampleAndroidApp` and `sampleAppleApps`)

https://github.com/user-attachments/assets/7c971734-ed7d-42f3-b38a-897997f59142

## ⚙️ Usage

1. Instantiate

    ```kotlin
    // On Apple (iOS, watchOS)
    val kHealth = KHealth()
    
    // On Android (inside a ComponentActivity)
    class MainActivity : ComponentActivity() {
      private val kHealth = KHealth(this)
    
      // Rest of your code
    }
    ```

2. Initialise (only required on Android)

    ```kotlin
    // Inside a `ComponentActivity`
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      // Initialise (only on Android, on Apple, this function is no-op)
      kHealth.initialise()
    }
    ```
   
3. Check Permission Status

   ```kotlin
    val permissionStatuses: Set<KHPermissionWithStatus> = kHealth.checkPermissions(
        KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = true,
            write = true,
        ),
        KHPermission(
            dataType = KHDataType.HeartRate,
            read = true,
            write = false,
        ),
        // Add as many requests as you want
    )
   ```
   
4. Or Request Permissions

   ```kotlin
    val permissionStatuses: Set<KHPermissionWithStatus> = kHealth.requestPermissions(
        KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = true,
            write = true,
        ),
        KHPermission(
            dataType = KHDataType.HeartRate,
            read = true,
            write = false,
        ),
        // Add as many requests as you want
    )
   ```
   
5. Check if permission was granted

    ```kotlin
    val caloriesWriteStatus = permissionStatuses.first {
        it.permission.dataType == KHDataType.ActiveCaloriesBurned
    }.writeStatus

    val wasWritePermissionGranted = caloriesWriteStatus == KHPermissionStatus.Granted
    ```
   
6. Write record(s)

   ```kotlin
   if (wasWritePermissionGranted) {
        val insertResponse: KHWriteResponse = kHealth.writeActiveCaloriesBurned(
          KHRecord(
            unitValue = KHUnit.Energy.Kilocalorie(3.4f),
            startDateTime = Clock.System.now().minus(10.minutes),
            endDateTime = Clock.System.now(),
          ),
          KHRecord(
            unitValue = KHUnit.Energy.Joule(1.5f),
            startDateTime = Clock.System.now().minus(30.minutes),
            endDateTime = Clock.System.now().minus(10.minutes),
          ),
          // Add as many records as you want
        )

        when (insertResponse) {
           is KHWriteResponse.Failed -> {
             println("Records insertion failed ❌ Reason: ${insertResponse.exception}")
           }

           KHWriteResponse.SomeFailed -> println("Some records were not inserted ⚠️")

           KHWriteResponse.Success -> println("Records inserted ✅")
        }
   }
   ```

## 🚀 Getting Started

![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.shubhamsinghshubham777/khealth?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat&label=Latest%20Version)

Add the following to your shared module's `build.gradle.kts`:
```kotlin
implementation("io.github.shubhamsinghshubham777:khealth:0.0.2-SNAPSHOT")
```

or add it to your version catalog:

```toml
[versions]
khealth = "0.0.2-SNAPSHOT"

[libraries]
khealth = { module = "io.github.shubhamsinghshubham777:khealth", version.ref = "khealth" }

[plugins]
```

and use it in your `build.gradle.kts`:

```kotlin
kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.khealth)
    }
  }
}
```

### For SNAPSHOT versions
Add the following to your project level `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        ...
        // Add this
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

## 💻 API

> [!WARNING]  
> The library is currently a work-in-progress. Hence, the APIs mentioned below are not final and might change at any time. Although, I'll try my best to keep them stable.

KHealth supports the following methods:

| Method / Property Name                                                           | Return Type                   | Description                                                                                                                                                                                                                                                                                                                                       |
|----------------------------------------------------------------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| fun `initialise`()                                                               | `Unit`                        | Allows the Android app to listen to the user's selection on the health permissions system dialog. This method is no-op on Apple devices.                                                                                                                                                                                                          |
| val `isHealthStoreAvailable`                                                     | `Boolean`                     | Returns whether the current device has HealthConnect (on Android) / HealthKit (on Apple) installed and ready to go.                                                                                                                                                                                                                               |
| suspend fun `checkPermissions`(vararg permissions: KHPermission)                 | `Set<KHPermissionWithStatus>` | Checks and returns whether the user has already granted permissions to the provided data types. The status can either be Granted, Denied, or NotDetermined. On Apple, due to privacy concerns, HealthKit will never return the status of the READ permission, hence on Apple, this method will always return `NotDetermined` for the READ status. |
| suspend fun `requestPermissions`(vararg permissions: KHPermission)               | Set`<KHPermissionWithStatus>` | Requests the asked permissions from the health store and returns their status (Granted, Denied, or NotDetermined).                                                                                                                                                                                                                                |
| suspend fun `writeActiveCaloriesBurned`(vararg records: KHRecord<KHUnit.Energy>) | `KHWriteResponse`             | Allows the user to write one or more records for the active calories/energy burned value(s) into the health store and returns whether the insertion operation was successful, partially successful, or not successful at all.                                                                                                                     |

## 📄 License

This library is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.
