package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorContext, ActorRef, Props}
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.platform.shoppingcart.command.Cart.{DoesNotExists, Exists}
import net.badprogrammer.platform.shoppingcart.command.{Cart, Create}
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId}
import net.badprogrammer.platform.shoppingcart.handler._
import net.badprogrammer.platform.shoppingcart.view.ShoppingCartPriceView

import scala.collection.mutable

class ActiveCarts(repo: ActorRef, idGenerator: ShoppingCartIdFactory)(context: ActorContext) {

  private val byUser: mutable.Map[Create, ShoppingCartId] = mutable.Map.empty
  private val active: mutable.Map[ShoppingCartId, ActorRef] = mutable.Map.empty


  def create(c: Create): Either[Cart.Response, ShoppingCartId] = if (byUser.contains(c)) {
    Left(Exists)
  } else {
    val id = byUser.getOrElseUpdate(c, idGenerator(c))
    active.update(id, createHandlerActor(id))
    Right(id)
  }

  def retrieve(id: ShoppingCartId): Either[Cart.Response, ActorRef] = active.get(id).map(Right(_)).getOrElse(Left(DoesNotExists))


  private def createHandlerActor(id: ShoppingCartId): ActorRef = {
    context.actorOf(Props(classOf[CommandAndQueryDispatcher], aggregateFactory(id), viewFactory(id)))
  }

  private def aggregateFactory(id: ShoppingCartId): LocalFactory = {
    context => context.actorOf(Props(classOf[ShoppingCartAggregate], id, repo))
  }

  private def viewFactory(id: ShoppingCartId): LocalFactory = {
    context => context.actorOf(Props(classOf[ShoppingCartPriceView], id))
  }

}
