package com.khealth

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSError
import platform.HealthKit.HKAuthorizationStatus
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KHealthTests {
    private lateinit var store: HKHealthStore
    private lateinit var sut: KHealth

    @BeforeTest
    fun setUp() {
        store = object : HKHealthStore() {}
        sut = KHealth(store = store, isHealthStoreAvailable = true)
    }

    @Test
    fun showsCorrectHealthStoreAvailability() {
        assertTrue(sut.isHealthStoreAvailable)
        sut = KHealth(store = store, isHealthStoreAvailable = false)
        assertFalse(sut.isHealthStoreAvailable)
    }

    @OptIn(UnsafeNumber::class)
    @Test
    fun checkPermissionsWorksAsExpectedInAllScenarios() = runTest {
        val allGrantedPermission = KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = true,
            write = true,
        )
        val readOnlyPermission = KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = true,
            write = false,
        )
        val writeOnlyPermission = KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = false,
            write = true,
        )
        val noPermission = KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = false,
            write = false,
        )
        val grantStore = object : HKHealthStore() {
            override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                return HKAuthorizationStatusSharingAuthorized
            }
        }
        val denyStore = object : HKHealthStore() {
            override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                return HKAuthorizationStatusSharingDenied
            }
        }
        val notDeterminedStore = object : HKHealthStore() {
            override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                return HKAuthorizationStatusNotDetermined
            }
        }

        // Case 1: When system grants WRITE permission
        sut = KHealth(store = grantStore, isHealthStoreAvailable = true)
        // Then allGranted request results in NotDetermined-Granted response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = allGrantedPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.Granted
                )
            ),
            sut.checkPermissions(allGrantedPermission)
        )
        // And read-only request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = readOnlyPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(readOnlyPermission)
        )
        // And write-only request results in NotDetermined-Granted response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = writeOnlyPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.Granted
                )
            ),
            sut.checkPermissions(writeOnlyPermission)
        )
        // And no-permission request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = noPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(noPermission)
        )

        // Case 2: When system denies WRITE permission
        sut = KHealth(store = denyStore, isHealthStoreAvailable = true)
        // Then allGranted request results in NotDetermined-Denied response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = allGrantedPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.Denied
                )
            ),
            sut.checkPermissions(allGrantedPermission)
        )
        // And read-only request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = readOnlyPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(readOnlyPermission)
        )
        // And write-only request results in NotDetermined-Denied response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = writeOnlyPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.Denied
                )
            ),
            sut.checkPermissions(writeOnlyPermission)
        )
        // And no-permission request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = noPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(noPermission)
        )

        // Case 3: When system does not determine WRITE permission
        sut = KHealth(store = notDeterminedStore, isHealthStoreAvailable = true)
        // Then allGranted request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = allGrantedPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(allGrantedPermission)
        )
        // And read-only request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = readOnlyPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(readOnlyPermission)
        )
        // And write-only request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = writeOnlyPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(writeOnlyPermission)
        )
        // And no-permission request results in NotDetermined-NotDetermined response
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = noPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.NotDetermined
                )
            ),
            sut.checkPermissions(noPermission)
        )
    }

    @OptIn(UnsafeNumber::class)
    @Test
    fun requestPermissionsWorksAsExpectedInAllScenarios() = runTest {
        val caloriesPermission = KHPermission(
            dataType = KHDataType.ActiveCaloriesBurned,
            read = true,
            write = true
        )
        val heartRatePermission = KHPermission(
            dataType = KHDataType.HeartRate,
            read = true,
            write = true
        )
        sut = KHealth(
            store = object : HKHealthStore() {
                override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                    return if (type == caloriesPermission.dataType.toHKObjectTypesOrNull()) {
                        HKAuthorizationStatusSharingAuthorized
                    } else {
                        HKAuthorizationStatusSharingDenied
                    }
                }

                override fun requestAuthorizationToShareTypes(
                    typesToShare: Set<*>?,
                    readTypes: Set<*>?,
                    completion: (Boolean, NSError?) -> Unit
                ) = completion(true, null)
            },
            isHealthStoreAvailable = true
        )
        assertEquals(
            setOf(
                KHPermissionWithStatus(
                    permission = caloriesPermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.Granted
                ),
                KHPermissionWithStatus(
                    permission = heartRatePermission,
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = KHPermissionStatus.Denied
                ),
            ),
            sut.requestPermissions(caloriesPermission, heartRatePermission)
        )
    }
}
