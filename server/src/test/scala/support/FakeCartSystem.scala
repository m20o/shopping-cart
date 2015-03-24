package support

import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import net.badprogrammer.shoppingcart.api.ShoppingCartSystem

import scala.concurrent.Future

class FakeCartSystem(pf: PartialFunction[Any, Any]) extends ShoppingCartSystem {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def reference: ActorRef = ???

  override def terminate: Unit = {}

  override def send(message: Any)(implicit timeout: Timeout): Future[Any] = Future {
    pf.apply(message)
  }
}

object FakeCartSystem {

  def apply(pf: PartialFunction[Any, Any] ): FakeCartSystem = new FakeCartSystem(pf)
}