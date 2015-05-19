package net.badprogrammer.platform.shoppingcart.service

import net.badprogrammer.platform.shoppingcart.command.{CartEvent, Command, Message, Query}
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}


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
