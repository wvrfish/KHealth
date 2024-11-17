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
 * An ovulation test checks for a rise in a hormone called luteinizing hormone (LH) in your urine,
 * which happens just before ovulation.
 *
 * - **Positive**: The test shows a high level of LH, meaning ovulation is likely happening soon.
 * - **Negative**: No rise in LH, meaning you're not close to ovulation yet.
 * - **High**: The test detects a very high LH level, indicating you’re near or at ovulation.
 * - **Inconclusive**: The test results are unclear, often due to factors like incorrect usage or a
 * faulty test.
 */
enum class KHOvulationTestResult { High, Negative, Positive, Inconclusive }
