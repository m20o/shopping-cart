package net.badprogrammer.shoppingcart.domain

import akka.actor.ActorRef

trait OriginalSender {

  def source: ActorRef

}

case class Quote(source: ActorRef, article: Article, quantity: Int) extends OriginalSender

object Quote {

  sealed abstract class Response(quote: Quote) extends OriginalSender {

    lazy val source = quote.source
  }

  case class Successful(quote: Quote, price: Money) extends Response(quote)

  case class Unsuccessful(quote: Quote, reason: String) extends Response(quote)

}
