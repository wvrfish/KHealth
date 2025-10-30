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

import kotlin.time.Instant

/**
 * Represents a single measurement of power. For example, using a power meter when exercising on a
 * stationary bike.
 *
 * @param unit The measurement scale of this record
 * @param time The point in time when the measurement was taken
 * @param value Power generated, in Power unit (Valid range: 0-100000 Watts)
 */
data class KHPowerSample(val unit: KHUnit.Power, val value: Double, val time: Instant)
