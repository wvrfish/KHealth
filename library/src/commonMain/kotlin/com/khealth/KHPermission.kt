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
 * Depicts an instance of the app's request to the operating system to provide it with the access to
 * read and/or write a particular kind of data from the health store.
 *
 * @param dataType Depicts the kind of values the app wants to read from the health store
 * @param read Whether the app wants to be able to read the [dataType] from health store
 * @param write Whether the app wants to be able to write the [dataType] from health store
 */
data class KHPermission(val dataType: KHDataType, val read: Boolean, val write: Boolean)
