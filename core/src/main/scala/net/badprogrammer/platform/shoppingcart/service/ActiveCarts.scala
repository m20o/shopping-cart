package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorContext, ActorRef}
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}

import scala.collection.mutable

private[service] class ActiveCarts(catalog: ActorRef)(context: ActorContext) {

  private val byUser: mutable.Map[User, ShoppingCartId] = mutable.Map.empty

  def existsFor(id: ShoppingCartId): Boolean = get(id).isDefined

  def get(id: ShoppingCartId): Option[ActorRef] = context.child(id.value)

  def retrieve(id: ShoppingCartId): Option[ActorRef] = context.child(id.value)

  def create(id: ShoppingCartId): Unit = context.actorOf(ShoppingCartAggregate.props(id, catalog), id.value)
}
