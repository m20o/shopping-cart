package net.badprogrammer.shoppingcart.domain

import scala.language.implicitConversions

case class Item(article: Article, quantity: Int)

object Item {

  implicit def fromTuple(p: (Article, Int)): Item = new Item(p._1, p._2)

}
