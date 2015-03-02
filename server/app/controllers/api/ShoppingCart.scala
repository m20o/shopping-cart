package controllers.api

import setup.Global

object ShoppingCart extends ShoppingCartApi  {

  lazy val cartSystem = Global.cartSystem

}
