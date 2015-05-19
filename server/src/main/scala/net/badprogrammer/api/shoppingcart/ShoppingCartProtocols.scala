package net.badprogrammer.api.shoppingcart

import spray.json.DefaultJsonProtocol

trait ShoppingCartProtocols extends DefaultJsonProtocol {

  implicit val CartItemFormat = jsonFormat3(CartItem)

  implicit val ShoppingCartFormat =jsonFormat1(ShoppingCart)

  implicit val CallerFormat = jsonFormat2(Caller)

}

object ShoppingCartProtocols extends ShoppingCartProtocols
