package net.badprogrammer.platform.shoppingcart.service

import akka.actor.{ActorContext, ActorRef, Props}
import akka.persistence.PersistentActor
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.platform.shoppingcart.command.Message
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.service.Cart._

// il problema e' tutto qui
class ShoppingCarts(catalog: ActorRef, generator: ShoppingCartIdGenerator) extends PersistentActor {

  val persistenceId: String = ShoppingCarts.ID

  private def bus = context.system.eventStream

  private val carts = new AggregatesHandler(catalog, generator)(context)

  override def receiveCommand: Receive = {
    case Create(user) => createCart(user)
    case Check(id) => checkCartExistence(id)
    case Execute(id, msg) => forwardToCart(id, msg)
  }

  override def receiveRecover: Receive = {
    case Created(_, id) => carts.create(id)
  }

  private def createCart(user: User): Unit = {
    val id = generator(user)
    if (carts.existsFor(id)) sender() ! Exists
    else {
      persist(Created(user, id)) {
        ev =>
          carts.create(ev.id)
          bus.publish(ev)
          sender() ! ev
      }
    }
  }

  private def checkCartExistence(id: ShoppingCartId): Unit = {
    sender() ! carts.get(id).map(x => Cart.Exists).getOrElse(DoesNotExists)
  }

  private def forwardToCart(id: ShoppingCartId, message: Message): Unit = {
    carts.get(id).foreach(_ forward message)
  }
}

object ShoppingCarts {

  val ID = "AllShoppingCarts"

  def props(repo: ActorRef)(implicit idFactory: ShoppingCartIdGenerator) = Props(classOf[ShoppingCarts], repo, idFactory)
}

sealed class AggregatesHandler(catalog: ActorRef, generator: ShoppingCartIdGenerator)(context: ActorContext) {

  def existsFor(id: ShoppingCartId): Boolean = get(id).isDefined

  def get(id: ShoppingCartId): Option[ActorRef] = context.child(id.value)

  def findById(id: ShoppingCartId): Option[ActorRef] = context.child(id.value)

  def create(id: ShoppingCartId): Unit = createChildShoppingCartActor(id)

  def createChildShoppingCartActor(id: ShoppingCartId): Unit = context.actorOf(ShoppingCartAggregate.props(id, catalog), id.value)
}




