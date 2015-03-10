package net.badprogrammer.platform.shoppingcart.command

import net.badprogrammer.platform.shoppingcart.domain.{Item, Money}

case class Pricing(elements: Traversable[Pricing.Element] = Nil) {

  val total: Money = elements.foldLeft(Money.Zero) { (partialPrice, item) => partialPrice + item.total }

}

object Pricing {

  case class Element(item: Item, price: Money) {

    val total: Money = price * item.quantity

  }
}
