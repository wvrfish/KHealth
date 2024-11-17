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
 * Depicts the kind of response from the health store when a [KHRecord] is written onto it. The
 * transaction can either Success completely, Fail completely, or Fail partially.
 */
sealed class KHWriteResponse {
    /**
     * Depicts that a write transaction onto the health store failed completely (i.e. no [KHRecord]s
     * were written).
     */
    data class Failed(val throwable: Throwable) : KHWriteResponse()

    // TODO: Check if you can return which records failed
    /**
     * Depicts that a write transaction onto the health store failed partially (i.e. some
     * [KHRecord]s were written).
     */
    data object SomeFailed : KHWriteResponse()

    /**
     * Depicts that a write transaction onto the health store succeeded completely (i.e. all
     * [KHRecord]s were written).
     */
    data object Success : KHWriteResponse()
}
