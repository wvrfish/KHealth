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
 * Represents a single measurement of [KHRecord.SleepSession].
 *
 * @param stage Describes the distinct phases of sleep (like REM, Deep, Light, etc.)
 * @param startTime The start instant of the interval over which the value this measurement was
 * captured
 * @param endTime The end instant of the interval over which the value this measurement was captured
 */
data class KHSleepStageSample(val stage: KHSleepStage, val startTime: Instant, val endTime: Instant)
