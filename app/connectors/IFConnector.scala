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

package connectors

import com.typesafe.config.ConfigFactory
import config.AppConfig
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}
import uk.gov.hmrc.http.HeaderCarrier.Config
import utils.HeaderCarrierSyntax.HeaderCarrierOps

import java.net.URL

trait IFConnector {

  val appConfig: AppConfig

  val headerCarrierConfig: Config = HeaderCarrier.Config.fromConfig(ConfigFactory.load())

  private[connectors] def iFHeaderCarrier(url: String, apiNumber: String)(implicit hc: HeaderCarrier): HeaderCarrier = {

    val internalHost = headerCarrierConfig.internalHostPatterns.exists(_.pattern.matcher(new URL(url).getHost).matches())

    val hcWithAuth = hc.copy(authorization = Some(Authorization(s"Bearer ${appConfig.iFAuthorisationToken(apiNumber)}")))

    if(internalHost) {
      hcWithAuth.withExtraHeaders("Environment" -> appConfig.environment)
    } else {
      hcWithAuth.withExtraHeaders("Environment" -> appConfig.environment).withExtraHeaders(hcWithAuth.toSeq: _*)
    }
  }
}
