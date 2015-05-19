package net.badprogrammer.shoppingcart.service

import net.badprogrammer.shoppingcart.command.{Command, Query, Message, CartEvent}
import net.badprogrammer.shoppingcart.domain.{User, ShoppingCartId}


object Cart {

  case class Check(id: ShoppingCartId) extends Query
  case class FindByUser(user: User) extends Query

  trait Response

  case object Exists extends Response

  case object DoesNotExists extends Response

  case class Create(user: User) extends Command

  case class Created(user: User, id: ShoppingCartId) extends CartEvent

  case class Execute(id: ShoppingCartId, msg: Message)

}
