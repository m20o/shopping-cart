package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorContext, ActorRef}
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.handler._
import net.badprogrammer.platform.shoppingcart.view.ShoppingCartPriceView

import scala.collection.mutable

private[service] class ActiveCarts(repo: ActorRef, idGenerator: ShoppingCartIdGenerator)(context: ActorContext) {

  private val byUser: mutable.Map[User, ShoppingCartId] = mutable.Map.empty
  private val active: mutable.Map[ShoppingCartId, ActorRef] = mutable.Map.empty

  def get(user: User): Option[ActorRef] = for (id <- byUser.get(user); ref <- active.get(id)) yield ref

  def create(user: User): ShoppingCartId = {
    val id = byUser.getOrElseUpdate(user, idGenerator(user))
    active.update(id, createHandlerActor(id))
    id
  }

  def retrieve(id: ShoppingCartId): Option[ActorRef] = active.get(id)


  private def createHandlerActor(id: ShoppingCartId): ActorRef = {
    context.actorOf(CommandAndQueryDispatcher.props(aggregateFactory(id), viewFactory(id)))
  }

  private def aggregateFactory(id: ShoppingCartId): CommandAndQueryDispatcher.Aggregate = {
    CommandAndQueryDispatcher.Aggregate(props = ShoppingCartAggregate.props(id, repo), name = "cart")
  }

  private def viewFactory(id: ShoppingCartId): CommandAndQueryDispatcher.View = {
    CommandAndQueryDispatcher.View(ShoppingCartPriceView.props(id), s"price-${id.value}")
  }

}
