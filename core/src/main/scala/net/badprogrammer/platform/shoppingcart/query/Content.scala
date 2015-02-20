package net.badprogrammer.platform.shoppingcart.query

import net.badprogrammer.platform.shoppingcart.domain.{Article, Item}

sealed abstract class Content(items: Seq[Item])

case class CartContent(items: List[Item]) extends Content(items)

case object CartContent {

  def apply(items: (Article, Int)*) = new CartContent(items.map(Item.fromTuple).toList)

}

case object EmptyCart extends Content(Seq.empty)