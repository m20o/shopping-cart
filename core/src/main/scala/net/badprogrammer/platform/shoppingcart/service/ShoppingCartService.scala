package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorRef, Props, Actor}
import net.badprogrammer.platform.shoppingcart.command.Cart.{DoesNotExists, Exists}
import net.badprogrammer.platform.shoppingcart.command._

class ShoppingCartService(repo: ActorRef, idGenerator: ShoppingCartIdGenerator) extends Actor {

  private val state: ActiveCarts = new ActiveCarts(repo, idGenerator)(context)

  override def receive: Receive = {
    case c: Create => state.get(c.user) match {
      case Some(_) => sender() ! Exists
      case None => sender() ! Created(state.create(c.user))
    }

    case Cart.Check(id) => state.retrieve(id) match {
      case None => sender() ! DoesNotExists
      case Some(_) => sender() ! Exists
    }

    case Execute(id, msg) => state.retrieve(id) match {
      case None => sender() ! DoesNotExists
      case Some(ref) => ref forward msg
    }
  }
}

object ShoppingCartService {
  def props(repo: ActorRef, idFactory: ShoppingCartIdGenerator) = Props(classOf[ShoppingCartService], repo, idFactory)
}




