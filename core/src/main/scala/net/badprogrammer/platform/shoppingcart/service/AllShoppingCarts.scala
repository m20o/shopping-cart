package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorRef, Props}
import akka.persistence.PersistentActor
import net.badprogrammer.platform.shoppingcart.command.Message
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.service.Cart._

class AllShoppingCarts(catalog: ActorRef, generator: ShoppingCartIdGenerator) extends PersistentActor {

  val persistenceId: String = AllShoppingCarts.ID

  private def bus = context.system.eventStream

  private val state: ActiveCarts = new ActiveCarts(catalog)(context)

  def otherreceive: Receive = {

    case Cart.Check(id) => state.retrieve(id) match {
      case None => sender() ! DoesNotExists
      case Some(_) => sender() ! Exists
    }

    case Execute(id, msg) => state.retrieve(id) match {
      case None => sender() ! DoesNotExists
      case Some(ref) => ref forward msg
    }
  }

  def checkCartExistence(id: ShoppingCartId): Unit = {

    sender() ! state.get(id).map(x => Cart.Exists).getOrElse(DoesNotExists)
  }

  def forwardToCart(id: ShoppingCartId, message: Message): Unit = {
    state.get(id).foreach(_ forward message)
  }

  override def receiveCommand: Receive = {
    case Create(user) => tryCreateCartFor(user)
    case Check(id) => checkCartExistence(id)
    case Execute(id, msg) => forwardToCart(id, msg)
  }

  override def receiveRecover: Receive = {
    case Created(id) => state.create(id)
  }

  private def tryCreateCartFor(user: User): Unit = {
    val id = generator(user)
    if (state.existsFor(id)) sender() ! Exists
    else {
      persist(Created(id)) {
        ev =>
          state.create(ev.id)
          bus.publish(ev)
          sender() ! ev
      }
    }
  }
}

object AllShoppingCarts {

  val ID = "AllShoppingCarts"

  def props(repo: ActorRef)(implicit idFactory: ShoppingCartIdGenerator) = Props(classOf[AllShoppingCarts], repo, idFactory)
}




