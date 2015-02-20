package net.badprogrammer.platform.shoppingcart.view

import akka.persistence.PersistentView
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartPricing
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.platform.shoppingcart.query.Pricing.Element
import net.badprogrammer.platform.shoppingcart.query.{GetSummary, Pricing}

class ShoppingCartPriceView(val id: ShoppingCartId)
  extends PersistentView {

  val persistenceId = id.value

  val viewId: String = s"$persistenceId-priceview"

  val pricing = new ShoppingCartPricing()

  override def receive: Receive = {
    handleCartEvent orElse
      handleQueries orElse {
      case x => print(x)
    }
  }

  private def handleCartEvent: Receive = {
    case ArticleQuoted(article, price) => pricing.updateQuotation(article -> price)
    case ArticleAdded(article, quantity) => pricing.addQuantity(article -> quantity)
    case ArticleRemoved(article, quantity) => pricing.removeQuantity(article -> quantity)
    case CartCleared => pricing.clear
  }

  private def handleQueries: Receive = {
    case GetSummary => {
      sender() ! Pricing(pricing.content.map(el => new Element(el._1, el._2)))
    }
  }
}
