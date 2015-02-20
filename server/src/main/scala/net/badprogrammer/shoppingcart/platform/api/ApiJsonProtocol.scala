package net.badprogrammer.shoppingcart.platform.api

import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.MetaMarshallers
import spray.json._

trait ApiJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  implicit val businessContextFormat = jsonFormat2(BusinessContext)

}
