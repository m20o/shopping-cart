package net.badprogrammer.shoppingcart.service

import akka.actor.{Actor, ActorRef, Props}
import Cart.FindByUser

class ShoppingCartsHandler(
                            val repository: ActorRef,
                            val storage: CartsByUserStorage,
                            implicit val generator: ShoppingCartIdGenerator) extends Actor {


  var carts: ActorRef = context.actorOf(ShoppingCarts.props(repository))
  var cartsByUser: ActorRef = context.actorOf(ShoppingCartsByUser.props(storage))

  def receive: Receive = {
    case q : FindByUser =>  cartsByUser forward q
    case other => carts forward other
  }
}

object ShoppingCartsHandler {

  def props(repository: ActorRef, storage: CartsByUserStorage)(implicit generator: ShoppingCartIdGenerator) =
    Props(classOf[ShoppingCartsHandler], repository, storage, generator)
}
