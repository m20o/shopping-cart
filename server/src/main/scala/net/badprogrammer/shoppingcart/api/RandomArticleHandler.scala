package net.badprogrammer.shoppingcart.api

import akka.actor._
import net.badprogrammer.shoppingcart.domain.{Money, Quote}

import scala.util.Random

class RandomArticleHandler extends Actor with ActorLogging{

  val random = new Random()

  def receive: Receive = {
    case q: Quote => {
      log.debug("Received {} from {}", q, sender())
      sender() ! Quote.Successful(q, Money(BigDecimal(random.nextInt(1000))))
    }
    case other => throw new RuntimeException("Invalid message: "+other)
  }
}

object RandomArticleHandler {

  def props() = Props[RandomArticleHandler]

  def reference(implicit factory: ActorRefFactory): ActorRef = factory.actorOf(props())
}
