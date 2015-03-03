package net.badprogrammer.platform.shoppingcart.view

import akka.actor.Props
import akka.persistence.PersistentView
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCart
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.platform.shoppingcart.query.Pricing.Element
import net.badprogrammer.platform.shoppingcart.query.{GetSummary, Pricing}

class ShoppingCartPriceView(val id: ShoppingCartId)
  extends PersistentView {

  val persistenceId = id.value

  val viewId: String = s"$persistenceId-priceview"

  val cart = new ShoppingCart()

  override def receive: Receive = {
    handleCartEvent orElse
      handleQueries orElse {
      case x => print(x)
    }
  }

  private def handleCartEvent: Receive = {
    case ArticleQuoted(article, price) => cart.updateQuotation(article -> price)
    case ArticleAdded(article, quantity) => cart.add(article, quantity)
    case ArticleRemoved(article, quantity) => cart.remove(article, quantity)
    case CartCleared => cart.clear
  }

  private def handleQueries: Receive = {
    case GetSummary => {
      sender() ! Pricing(cart.quotations.map(el => new Element(el._1, el._2)))
    }
  }
}

object ShoppingCartPriceView {

  def props(id: ShoppingCartId) = Props(classOf[ShoppingCartPriceView], id)
}