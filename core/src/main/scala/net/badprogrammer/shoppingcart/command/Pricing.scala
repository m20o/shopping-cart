package net.badprogrammer.shoppingcart.command

import net.badprogrammer.shoppingcart.domain.{Money, Item}

case class Pricing(elements: Traversable[Pricing.Element] = Nil) {

  val total: Money = elements.foldLeft(Money.Zero) { (partialPrice, item) => partialPrice + item.total }

}

object Pricing {

  case class Element(item: Item, price: Money) {

    val total: Money = price * item.quantity

  }
}
