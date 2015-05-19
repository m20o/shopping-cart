package net.badprogrammer.api.shoppingcart

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import net.badprogrammer.platform.shoppingcart.command.{CartContent, GetContent}
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.platform.shoppingcart.service.Cart.Execute

import scala.concurrent.Future

class ShoppingCartRepository(cartsActor: ActorRef) {

  def load(id: ShoppingCartId)(implicit timeout: Timeout): Future[CartContent] = (cartsActor ? Execute(id, GetContent)).mapTo[CartContent]

}
