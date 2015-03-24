package net.badprogrammer.shoppingcart.api

import net.badprogrammer.platform.shoppingcart.domain.User
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.MetaMarshallers
import spray.json._

trait ApiJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  implicit val userFormat = jsonFormat2(User)

  implicit val businessContextFormat = jsonFormat2(BusinessContext)

}
