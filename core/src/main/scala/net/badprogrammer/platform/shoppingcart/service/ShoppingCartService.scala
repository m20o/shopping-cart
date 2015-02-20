package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorRef, Props, Actor}
import net.badprogrammer.platform.shoppingcart.command.Cart.{DoesNotExists, Exists}
import net.badprogrammer.platform.shoppingcart.command._

class ShoppingCartService(repo: ActorRef, idFactory: ShoppingCartIdFactory) extends Actor {

  private val state: ActiveCarts = new ActiveCarts(repo, idFactory)(context)

  override def receive: Receive = {

    case c: Create => state.create(c) match {
      case Left(_) => sender() ! Exists
      case Right(id) => sender() ! Created(id)
    }

    case Cart.Check(id) => state.retrieve(id) match {
      case Left(_) => sender() ! DoesNotExists
      case Right(_) => sender() ! Exists
    }


    case Execute(id, msg) => state.retrieve(id) match {
      case Left(_) => sender() ! DoesNotExists
      case Right(ref) => ref forward msg
    }
  }

}

object ShoppingCartService {

  def props(repo: ActorRef, idFactory: ShoppingCartIdFactory) = Props(classOf[ShoppingCartService], repo, idFactory)
}




