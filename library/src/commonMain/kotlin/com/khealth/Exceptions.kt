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

object HealthStoreNotAvailableException : Exception(
    "$HealthStoreName is not available for the current device!"
)

object HealthStoreNotInitialisedException : Exception(
    "KHealth has not been initialised yet! Please make sure to call " +
            "kHealth.initialise() before trying to access any other methods."
)

data class NoWriteAccessException(private val forPermission: String? = null) : Exception(
    "Writing to $HealthStoreName failed! Please make sure you have write permissions for " +
            "${forPermission ?: "all permissions you're trying to ask"}."
)
