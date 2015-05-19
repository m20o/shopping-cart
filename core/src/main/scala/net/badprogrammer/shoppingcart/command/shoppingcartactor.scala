package net.badprogrammer.shoppingcart.command

import net.badprogrammer.shoppingcart.domain.{Money, Item, Article}


case class UnknownCommand(command: Any)

case class AddArticle(article: Article, quantity: Int = 1) extends Command

case class ArticleAdded(article: Article, quantity: Int) extends ContentEvent

case class ArticleNotAvailable(article: Article, quantity: Int, reason: String) extends ContentEvent

case class ArticleQuoted(article: Article, price: Money) extends CartEvent

case object Clear extends Command

case object CartCleared extends ContentEvent

case class RemoveArticle(article: Article, quantity: Int = 1) extends Command

case class ArticleRemoved(article: Article, quantity: Int) extends ContentEvent

case object GetContent extends Query

case class CartContent(items: List[Item]) {

  val notEmpty = items.nonEmpty

  val isEmpty = items.isEmpty

}
