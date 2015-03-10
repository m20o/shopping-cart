package controllers.api

import setup.Global

object ShoppingCartController extends ShoppingCartApi  {

  lazy val cartSystem = Global.cartSystem

}
