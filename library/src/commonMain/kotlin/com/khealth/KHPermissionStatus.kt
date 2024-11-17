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
 * Represents the user's response to a [KHPermission] request. He can either grant or deny a
 * permission request. But since Apple's HealthKit does not provide the real read status of any
 * permission due to privacy concerns, another type of status was introduced named [NotDetermined].
 * In this case, the user will have to just try to read the data from HealthKit, if the data is
 * returned, it means that the app was granted the permission, otherwise not.
 */
enum class KHPermissionStatus { Granted, Denied, NotDetermined }
