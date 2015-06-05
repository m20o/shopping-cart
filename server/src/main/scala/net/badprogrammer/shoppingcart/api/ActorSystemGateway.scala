package net.badprogrammer.shoppingcart.api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import net.badprogrammer.shoppingcart.command.{CartContent, GetContent}
import net.badprogrammer.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.shoppingcart.service.Cart
import net.badprogrammer.shoppingcart.service.Cart.{Created, Exists}

import scala.concurrent.{ExecutionContext, Future}

class ActorSystemGateway(cartsActor: ActorRef, implicit val context: ExecutionContext) {

  def load(id: ShoppingCartId)(implicit timeout: Timeout): Future[CartContent] = (cartsActor ? Cart.Execute(id, GetContent)).mapTo[CartContent]

  def create(caller: Caller)(implicit timeout: Timeout): Future[Option[Created]] = cartsActor ? Cart.Create(User(caller.id, caller.site)) collect {
    case msg: Created => Some(msg)
    case Exists => None
  }
}

case class Caller(id: String, site: String)
