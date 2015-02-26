package net.badprogrammer.platform.shoppingcart.command

import net.badprogrammer.platform.shoppingcart.domain.{User, ShoppingCartId}
import net.badprogrammer.platform.shoppingcart.query.Query


object Cart {

  case class Check(id: ShoppingCartId) extends Query

  trait Response
  case object Exists extends Response
  case object DoesNotExists extends Response

}


case class Create(user: User) extends Command

case class Created(id: ShoppingCartId) extends CartEvent

case class Execute(id: ShoppingCartId, msg: Any)