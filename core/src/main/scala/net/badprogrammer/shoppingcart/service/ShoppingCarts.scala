package net.badprogrammer.shoppingcart.service

import akka.actor._
import akka.event.Logging.MDC
import akka.persistence.PersistentActor
import Cart._
import net.badprogrammer.shoppingcart.aggregate.ShoppingCartAggregate
import net.badprogrammer.shoppingcart.command.Message
import net.badprogrammer.shoppingcart.domain.{ShoppingCartId, User}

// il problema e' tutto qui
class ShoppingCarts(catalog: ActorRef, generator: ShoppingCartIdGenerator) extends PersistentActor with DiagnosticActorLogging {

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
    log.debug("Forward {} to {}", message, id)
    carts.get(id).map(_ forward message).getOrElse(sender() ! DoesNotExists)
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




