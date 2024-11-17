/*
 * Copyright (c) 2024 Shubham Singh
 *
 * This library is licensed under the Apache 2.0 License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khealth

/**
 * Clubs a permission with its status. Useful for the case where the app requests statuses for a
 * list of permissions that include a few unsupported ones on the current platform and the library
 * removes it from the input request and output status lists for compatibility reasons. In that
 * case, this object will come handy in detecting exactly which permissions were interacted with
 * (i.e. granted or denied) by the user.
 *
 * **Code Example -** Assume that the following code is run on an `Apple` platform:
 * ```kotlin
 * val requests = arrayOf(
 *     // Apple does not support this permission ❌
 *     KHPermission(
 *         dataType = KHDataType.Speed,
 *         read = true,
 *         write = false,
 *     ),
 *     // Apple does support this permission ✅
 *     KHPermission(
 *         dataType = KHDataType.HeartRate,
 *         read = true,
 *         write = false,
 *     )
 * )
 * val responses = kHealth.requestPermissions(*requests)
 *
 * // This will print: "Request count: 2"
 * println("Request count: ${requests.size}")
 *
 * // This will print: "Response count: 1"
 * println("Response count: ${responses.size}")
 *
 * // Now you can use this object to see which
 * // permissions were actually interacted with
 * responses.map { permWithStatus ->
 *     println(
 *         "For permission: ${permWithStatus.permission}, " +
 *                 "read status: ${permWithStatus.readStatus} " +
 *                 "and write status: ${permWithStatus.writeStatus}"
 *     )
 * }
 * ```
 */
data class KHPermissionWithStatus(
    val permission: KHPermission,
    val readStatus: KHPermissionStatus,
    val writeStatus: KHPermissionStatus,
)
