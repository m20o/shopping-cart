package net.badprogrammer.platform.shoppingcart.query

import net.badprogrammer.platform.shoppingcart.domain.Item

case class CartContent(items: List[Item]) {

  val notEmpty = items.nonEmpty

  val isEmpty = items.isEmpty

}