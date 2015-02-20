package net.badprogrammer.platform.shoppingcart.aggregate

import net.badprogrammer.platform.shoppingcart.domain.{Article, Item}

class ShoppingCartContent(private var content: Map[Article, Int] = Map.empty) {

  def contains(article: Article): Boolean = content.contains(article)

  def empty: Unit = content = Map.empty

  def remove(article: Article, quantity: Int): Unit = {
    val residualQuantity = content.get(article).map(_ - quantity).filter(_ > 0).getOrElse(0)
    if (residualQuantity == 0) {
      content = content - article
    } else {
      content = content + (article -> residualQuantity)
    }
  }

  def items: List[Item] = content.map { el => Item(el._1, el._2)}.toList

  def isEmpty = content.isEmpty

  def nonEmpty = content.nonEmpty

  def add(article: Article, quantity: Int): Unit = {
    val newQuantity = content.get(article).map(_ + quantity).getOrElse(quantity)
    content = content + (article -> newQuantity)
  }

  def apply(article:Article): Option[Int] = content.get(article)
}
