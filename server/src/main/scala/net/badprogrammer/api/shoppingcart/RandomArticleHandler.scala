package net.badprogrammer.api.shoppingcart

import akka.actor.{ActorRef, ActorRefFactory, Props, Actor}
import net.badprogrammer.platform.shoppingcart.domain.{Money, Quote}

import scala.util.Random

class RandomArticleHandler extends Actor {

  val random = new Random()

  override def receive: Receive = {
    case q: Quote => Quote.Successful(q, Money(BigDecimal(random.nextInt(1000))))
  }
}

object RandomArticleHandler {

  def props() = Props[RandomArticleHandler]

  def reference(implicit factory: ActorRefFactory): ActorRef = factory.actorOf(props())
}
