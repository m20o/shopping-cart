package controllers.api

import play.api.mvc.Controller
import setup.{Global, ShoppingCartSystemProvider}

object ShoppingCart extends Controller with ShoppingCartApi with ShoppingCartSystemProvider {

  lazy val cartSystem = Global.cartSystem

}
