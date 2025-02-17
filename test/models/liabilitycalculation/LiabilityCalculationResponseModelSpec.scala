/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.liabilitycalculation

import play.api.http.Status
import play.api.libs.json._
import testConstants.GetCalculationDetailsConstants.{successExpectedJsonFull, successModelFull}
import testUtils.TestSuite

class LiabilityCalculationResponseModelSpec extends TestSuite {

  "LastTaxCalculationResponseMode model" when {
    "successful successModelMinimal" should {
      val successModelMinimal = LiabilityCalculationResponse(
        calculation = None,
        metadata = Metadata(
          calculationTimestamp = "2019-02-15T09:35:15.094Z",
          crystallised = true)
      )
      val expectedJson = s"""
                            |{
                            |  "metadata" : {
                            |    "calculationTimestamp" : "2019-02-15T09:35:15.094Z",
                            |    "crystallised" : true
                            |  }
                            |}
                            |""".stripMargin.trim


      "be translated to Json correctly" in {
        Json.toJson(successModelMinimal) mustBe Json.parse(expectedJson)
      }
      "should convert from json to model" in {
        val calcResponse = Json.fromJson[LiabilityCalculationResponse](Json.parse(expectedJson))
        Json.toJson(calcResponse.get) mustBe Json.parse(expectedJson)
      }
    }

    "successful successModelFull" should {

      "be translated to Json correctly" in {
        Json.toJson(successModelFull) mustBe Json.parse(successExpectedJsonFull)
      }

      "should convert from json to model" in {
        val calcResponse = Json.fromJson[LiabilityCalculationResponse](Json.parse(successExpectedJsonFull))
        Json.toJson(calcResponse.get) mustBe Json.parse(successExpectedJsonFull)
      }
    }

    "not successful" should {
      val errorStatus = 500
      val errorMessage = "Error Message"
      val errorModel = LiabilityCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")

      "have the correct Status (500)" in {
        errorModel.status mustBe Status.INTERNAL_SERVER_ERROR
      }
      "have the correct message" in {
        errorModel.message mustBe "Error Message"
      }
      "be translated into Json correctly" in {
        Json.prettyPrint(Json.toJson(errorModel)) mustBe
          (s"""
              |{
              |  "status" : $errorStatus,
              |  "message" : "$errorMessage"
              |}
           """.stripMargin.trim)
      }
    }
  }

}
