package net.badprogrammer.platform.shoppingcart.handler

import akka.actor._
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.Article
import net.badprogrammer.platform.shoppingcart.query.GetSummary
import net.badprogrammer.platform.shoppingcart.testsupport.ActorSpec
import org.scalatest.BeforeAndAfterEach


class CommandAndQueryDispatcherSpec extends ActorSpec with BeforeAndAfterEach {

  import net.badprogrammer.platform.shoppingcart.handler.CommandAndQueryDispatcherSpec._

  var handler: ActorRef = _

  "A dispatcher" must {

    val aCommand = AddArticle(Article("test"))

    val aQuery = GetSummary

    "send all commands to the aggregate" in {

      handler ! aCommand

      val received = expectMsgType[Received]

      received.who shouldBe "aggregate"
      received.what shouldBe aCommand
    }

    "send all queries to the view" in {

      handler ! aQuery

      val received = expectMsgType[Received]

      received.who shouldBe "view"
      received.what shouldBe aQuery
    }

  }

  override protected def beforeEach(): Unit = {
    handler = system.actorOf(Props(classOf[CommandAndQueryDispatcher], probe("aggregate"), probe("view")))
  }

}

object CommandAndQueryDispatcherSpec {

  def probe(name: String) : LocalFactory = context => context.actorOf(Props(classOf[Probe], name))

  case class Received(who: String, what: Any)

  class Probe(who: String) extends Actor {

    override def receive = {

      case e => sender() ! Received(who, e)
    }
  }

  object Fake extends Command {
    override def toString: String = "I'm the command!"
  }
}


