package net.badprogrammer.shoppingcart.platform

import akka.actor.{Actor, Props}
import com.typesafe.config.ConfigFactory
import net.badprogrammer.platform.shoppingcart.domain._

class TestingFixture extends Actor {

  val Flight = Article("flight")
  val HotDog = Article("hot-dog")
  val Cocaine = Article("cocaine")

  val pricing = {
    val builder = Map.newBuilder[Article, Money]
    builder += (Flight -> Money("50.50"))
    builder += (HotDog -> Money("10.10"))
    builder.result()
  }

  override def receive: Receive = {
    case q@Quote(_, article, _) => sender() ! pricing.get(article).map(Quote.Successful(q, _)).getOrElse(Quote.Unsuccessful(q, s"$article not available"))
  }
}

object TestingFixture {

  def props() = Props[TestingFixture]
}

object ActorSpecConfiguration {

  private val loglevel = "INFO"

  private val config =
    s"""
       |akka {
       |
       |loglevel = "$loglevel"
                               |
                               |log-dead-letters = 0
                               |log-dead-letters-during-shutdown = off
                               |
                               |actor {
                               |  debug {
                               |    # enable function of LoggingReceive, which is to log any received message at
                               |    # DEBUG level
                               |    receive = on
                               |  }
                               |}
                               |
                               |persistence {
                               |
                               |  journal.plugin = "inmemory-journal"
                               |
                               |  snapshot-store.plugin = "inmemory-snapshot-store"
                               |
                               | view.auto-update-interval = 1 ms
                               |}
                               |}
    """.stripMargin

  def apply() = ConfigFactory.parseString(config)

}
