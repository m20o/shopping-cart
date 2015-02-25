package net.badprogrammer.platform.shoppingcart.handler

import akka.actor._
import akka.event.Logging
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

      received.who shouldBe "cart"
      received.what shouldBe aCommand
    }

    "send all queries to the views" in {

      handler ! aQuery

      val received = expectMsgAllConformingOf(classOf[Received], classOf[Received])

      received.map(_.what) should (contain only aQuery)
      received.map(_.who) should (contain allOf("price", "user"))
    }

  }

  override protected def beforeEach() = {

    handler = system.actorOf(CommandAndQueryDispatcher.props(aggregate("cart"), view("price"), view("user")))
  }

}

object CommandAndQueryDispatcherSpec {


  def aggregate(name: String) = CommandAndQueryDispatcher.Aggregate(Props(classOf[Probe], name), name)

  def view(name: String) = CommandAndQueryDispatcher.View(Props(classOf[Probe], name), name)

  case class Received(who: String, what: Any)

  class Probe(who: String) extends Actor {

    val log = Logging(this)

    override def receive = {

      case e => {
        log.debug(s"$who has received $e")
        sender() ! Received(who, e)
      }
    }
  }

  object Fake extends Command {
    override def toString: String = "I'm the command!"
  }

}


