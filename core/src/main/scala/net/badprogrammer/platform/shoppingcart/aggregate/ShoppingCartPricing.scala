package net.badprogrammer.platform.shoppingcart.aggregate

import net.badprogrammer.platform.shoppingcart.domain.{Article, Item, Money}

class ShoppingCartPricing(private var quotes: Map[Article, Money] = Map.empty,
                          private val quantities: ShoppingCartContent = new ShoppingCartContent()) {

  def clear: Unit = quantities.empty

  def removeQuantity(q: (Article, Int)): Unit = quantities.remove(article = q._1, quantity = q._2)

  def addQuantity(q: (Article, Int)): Unit = quantities.add(article = q._1, quantity = q._2)

  def updateQuotation(quotation: (Article, Money)): Unit = quotes = quotes + quotation

  def content: Map[Item, Money] = for ((article, price) <- quotes; quantity <- quantities(article)) yield new Item(article, quantity) -> price
}
