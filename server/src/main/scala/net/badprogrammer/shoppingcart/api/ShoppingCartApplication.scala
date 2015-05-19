package net.badprogrammer.shoppingcart.api

import spray.httpx.SprayJsonSupport
import spray.routing.{HttpService, Route}

trait ShoppingCartApplication extends HttpService with SprayJsonSupport {

  def routes: Route

}
