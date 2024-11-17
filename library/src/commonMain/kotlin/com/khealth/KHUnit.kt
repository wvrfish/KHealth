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

sealed class KHUnit {
    sealed class BloodGlucose : KHUnit() {
        data object MillimolesPerLiter : BloodGlucose()
        data object MilligramsPerDeciliter : BloodGlucose()
    }

    sealed class Energy : KHUnit() {
        data object Calorie : Energy()
        data object KiloCalorie : Energy()
        data object Joule : Energy()
        data object KiloJoule : Energy()
    }

    sealed class Length : KHUnit() {
        data object Meter : Length()
        data object Mile : Length()
        data object Inch : Length()
    }

    sealed class Mass : KHUnit() {
        data object Gram : Mass()
        data object Ounce : Mass()
        data object Pound : Mass()
    }

    sealed class Power : KHUnit() {
        data object KilocaloriePerDay : Power()
        data object Watt : Power()
    }

    sealed class Pressure : KHUnit() {
        data object MillimeterOfMercury : Pressure()
    }

    sealed class Temperature : KHUnit() {
        data object Celsius : Temperature()
        data object Fahrenheit : Temperature()
    }

    sealed class Velocity : KHUnit() {
        data object MilesPerHour : Velocity()
        data object MetersPerSecond : Velocity()
        data object KilometersPerHour : Velocity()
    }

    sealed class Volume : KHUnit() {
        data object Liter : Volume()
        data object FluidOunceUS : Volume()
    }
}
