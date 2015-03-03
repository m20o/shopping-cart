package net.badprogrammer.platform.shoppingcart.aggregate

import net.badprogrammer.platform.shoppingcart.domain.{Money, Article, Item}

class ShoppingCart(private var quantities: Map[Article, Int] = Map.empty, private var quotes: Map[Article, Money] = Map.empty) {

  def contains(article: Article): Boolean = quantities.contains(article)

  def clear: Unit = quantities = Map.empty

  def remove(article: Article, quantity: Int): Unit = {
    val residualQuantity = quantities.get(article).map(_ - quantity).filter(_ > 0).getOrElse(0)
    if (residualQuantity == 0) {
      quantities = quantities - article
    } else {
      quantities = quantities + (article -> residualQuantity)
    }
  }

  def items: List[Item] = quantities.map { el => Item(el._1, el._2)}.toList

  def isEmpty = quantities.isEmpty

  def nonEmpty = quantities.nonEmpty

  def add(article: Article, quantity: Int): Unit = {
    val newQuantity = quantities.get(article).map(_ + quantity).getOrElse(quantity)
    quantities = quantities + (article -> newQuantity)
  }

  def add(p:(Article, Int)):Unit = add(p._1, p._2)

  def apply(article:Article): Option[Int] = quantities.get(article)

  def updateQuotation(quotation: (Article, Money)): Unit = quotes = quotes + quotation

  def quotations: Map[Item, Money] = for ((article, price) <- quotes; quantity <- quantities.get(article)) yield new Item(article, quantity) -> price
}
