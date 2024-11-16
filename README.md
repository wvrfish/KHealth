<h1 align="center">🏥 KHealth 🏥</h1>

<p align="center">
    <img src="assets/logo.svg" width="200"  alt=""/>
</p>

<p align="center">
    <img src="http://img.shields.io/badge/-android-6EDB8D.svg?style=flat" />
    <img src="http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat" />
    <img src="http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat" />
</p>

**KHealth** (_short for Kotlin Health_) is a simple Kotlin Multiplatform wrapper over Android's [Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect) and Apple's [HealthKit](https://developer.apple.com/documentation/healthkit) APIs. It provides a simple and effective way to consume these native APIs in a Kotlin/Compose Multiplatform environment.

## Demo

> [!NOTE]  
> You can find the following app in the `sample*` directories (e.g. `sampleAndroidApp` and `sampleAppleApps`)

https://github.com/user-attachments/assets/7c971734-ed7d-42f3-b38a-897997f59142

https://github.com/user-attachments/assets/2dcd7c92-5d15-47c0-9ecb-819aa1cd125b

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
   
6. Write records

   ```kotlin
   if (wasWritePermissionGranted) {
        val insertResponse: KHWriteResponse = kHealth.writeRecords(
            KHRecord.ActiveCaloriesBurned(
                unit = KHUnit.Energy.KiloCalorie,
                value = 3.4,
                startTime = Clock.System.now().minus(10.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.HeartRate(
                samples = listOf(
                    KHHeartRateSample(
                        beatsPerMinute = 126,
                        time = Clock.System.now().minus(10.minutes)
                    )
                ),
            ),
            // Add as many records as you want
        )

        when (insertResponse) {
           is KHWriteResponse.Failed -> {
             println("Records insertion failed ❌ Reason: ${insertResponse.throwable}")
           }

           KHWriteResponse.SomeFailed -> println("Some records were not inserted ⚠️")

           KHWriteResponse.Success -> println("Records inserted ✅")
        }
   }
   ```
7. Read records
   ```kotlin
    val heartRateRecords = kHealth.readRecords(
        KHReadRequest.HeartRate(
            startTime = Clock.System.now().minus(1.days),
            endTime = Clock.System.now()
        )
    )
    println("Heart Rate records: $heartRateRecords")
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

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This library is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.
