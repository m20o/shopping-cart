package support.api

import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCart
import net.badprogrammer.platform.shoppingcart.command.CartContent
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, Money, Article}

import scala.language.postfixOps

object Articles {

  val pasta = Article("pasta with tomato sauce")
  val wine = Article("red wine glass")
  val coffee = Article("coffee")
}


object KnownCarts {

  import Articles._

  val items = (pasta -> 1) :: (wine -> 2) :: (coffee -> 1) :: Nil
  val prices: Map[Article, Money] = (pasta -> "8.50") :: (wine -> "7.20") :: (coffee -> "1.00") :: Nil map (l => l._1 -> Money(l._2)) toMap

  val full = {
    val c = new ShoppingCart()
    items.foreach { i =>
      c.add(i)
      c.updateQuotation(i._1 -> prices(i._1))
    }
    c
  }

  val carts = Map.empty + (ShoppingCartId("full") -> full) + (ShoppingCartId("empty") -> new ShoppingCart())

  def apply(id: ShoppingCartId): Option[CartContent] = carts.get(id).map { x => CartContent(x.items) }
}
