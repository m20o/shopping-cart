package net.badprogrammer.shoppingcart.api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import net.badprogrammer.shoppingcart.command.{CartContent, GetContent}
import net.badprogrammer.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.shoppingcart.service.Cart
import Cart.Execute

import scala.concurrent.Future

class ShoppingCartRepository(cartsActor: ActorRef) {

  def load(id: ShoppingCartId)(implicit timeout: Timeout): Future[CartContent] = (cartsActor ? Execute(id, GetContent)).mapTo[CartContent]

}
