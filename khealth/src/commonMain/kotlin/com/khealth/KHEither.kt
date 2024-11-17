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
 * Represents a disjoint union entity, generally used to depict objects that are supported on both
 * Android and Apple platforms but use different return types internally.
 *
 * At any given point in time, only one of these values will be available, hence the member
 * properties [isLeft] and [isRight] will come in handy in such cases.
 *
 * @param left The unit represented by Android
 * @param right The unit represented by Apple
 */
data class KHEither<T, U>(val left: T, val right: U) {
    val isLeft: Boolean get() = left != null
    val isRight: Boolean get() = right != null
}
