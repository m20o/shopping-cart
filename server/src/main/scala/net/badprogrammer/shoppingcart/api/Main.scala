package net.badprogrammer.shoppingcart.api

import akka.actor.ActorSystem
import net.badprogrammer.shoppingcart.service.ShoppingCartIdGenerator
import spray.routing.SimpleRoutingApp

object Main extends App with SimpleRoutingApp with ShoppingCartApi {

  implicit val system = ActorSystem("my-system")

  startServer(interface = "localhost", port = 8080) {
    routes
  }

  override implicit def idGenerator: ShoppingCartIdGenerator = ???
}
