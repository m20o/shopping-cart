package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorContext, ActorRef}
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}

import scala.collection.mutable

private[service] class ActiveCarts(repo: ActorRef, idGenerator: ShoppingCartIdGenerator)(context: ActorContext) {

  private val byUser: mutable.Map[User, ShoppingCartId] = mutable.Map.empty

  def get(user: User): Option[ActorRef] = for (id <- byUser.get(user); ref <- retrieve(id)) yield ref

  def create(user: User): ShoppingCartId = {
    val id = byUser.getOrElseUpdate(user, idGenerator(user))
    createHandlerActor(id)
    id
  }

  def retrieve(id: ShoppingCartId): Option[ActorRef] = context.child(id.value)

  private def createHandlerActor(id: ShoppingCartId): ActorRef = {
    context.actorOf(ShoppingCartAggregate.props(id, repo), id.value)
  }
}
