package net.badprogrammer.api.shoppingcart

import akka.actor.ActorSystem
import net.badprogrammer.platform.shoppingcart.service.ShoppingCartIdGenerator
import spray.routing.SimpleRoutingApp

object Main extends App with SimpleRoutingApp with ShoppingCartApi {

  implicit val system = ActorSystem("my-system")

  startServer(interface = "localhost", port = 8080) {
    routes
  }

  override implicit def idGenerator: ShoppingCartIdGenerator = ???
}
