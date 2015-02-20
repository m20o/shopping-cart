package net.badprogrammer.platform.shoppingcart.domain

import akka.actor.ActorRef

trait OriginalSender {

  def source: ActorRef
}

case class Quote(source: ActorRef, article: Article, quantity: Int) extends OriginalSender

object Quote {

  sealed trait Response extends OriginalSender {

    def quote: Quote

    lazy val source = quote.source
  }

  case class Successful(quote: Quote, price: Money) extends Response

  case class Unsuccessful(quote: Quote, reason: String) extends Response

}
