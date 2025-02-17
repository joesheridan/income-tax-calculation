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

package api

import com.github.tomakehurst.wiremock.http.HttpHeader
import helpers.WiremockSpec
import models.DesErrorBodyModel
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status._
import play.api.libs.json.{JsString, Json}

class DeclareCrystallisationITest extends AnyWordSpec with WiremockSpec with ScalaFutures with Matchers {

  trait Setup {
    implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))
    val nino: String = "AA123123A"
    val taxYear = "2022"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"
    val desUrl = s"/income-tax/calculation/nino/$nino/$taxYear/$calculationId/crystallise"
    val agentClientCookie: Map[String, String] = Map("MTDITID" -> "555555555")
    val mtditidHeader: (String, String) = ("mtditid", "555555555")
    val requestHeaders: Seq[HttpHeader] = Seq(new HttpHeader("mtditid", "555555555"))
    auditStubs()
  }

  "declareCrystallisation" when {

    "the user is an individual" should {

      "return a 204 NoContent if declareCrystallisation has been posted successfully" in new Setup {

        val response: String = JsString("").toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, NO_CONTENT, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 204
        }
      }

      "return an InternalServerError(500) when DES returns an InternalServerError" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 500
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a ServiceUnavailable(503) when DES returns ServiceUnavailable" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 503
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 404 NotFound" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 404
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 409 Conflict" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, CONFLICT, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 409
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 400 BadRequest" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, BAD_REQUEST, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 400
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 422 UnprocessableEntity" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        authorised()
        stubPostWithoutRequestBody(desUrl, UNPROCESSABLE_ENTITY, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 422
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }
    }

    "the user is an agent" should {

      "return a 204 NoContent if declareCrystallisation has been posted successfully" in new Setup {

        val response: String = JsString("").toString()

        agentAuthorised()

        stubPostWithoutRequestBody(desUrl, NO_CONTENT, response)
        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 204
        }
      }

      "return an InternalServerError(500) when DES returns an InternalServerError" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(desUrl, INTERNAL_SERVER_ERROR, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 500
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a ServiceUnavailable(503) when DES returns ServiceUnavailable" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(desUrl, SERVICE_UNAVAILABLE, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 503
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 409 Conflict" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(desUrl, CONFLICT, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 409
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 404 NotFound" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(desUrl, NOT_FOUND, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 404
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 400 BadRequest" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(desUrl, BAD_REQUEST, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 400
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }

      "return a 4XX response when DES returns a 422 UnprocessableEntity" in new Setup {
        val response: String = Json.toJson(DesErrorBodyModel("DES_ERROR", "DES_ERROR")).toString()

        agentAuthorised()
        stubPostWithoutRequestBody(desUrl, UNPROCESSABLE_ENTITY, response)

        whenReady(buildClient(s"/income-tax-calculation/income-tax/nino/$nino/taxYear/$taxYear/$calculationId/declare-crystallisation")
          .withHttpHeaders(mtditidHeader)
          .post("""{}""")) {
          result =>
            result.status mustBe 422
            result.body mustBe """{"code":"DES_ERROR","reason":"DES_ERROR"}"""
        }
      }
    }
  }
}
