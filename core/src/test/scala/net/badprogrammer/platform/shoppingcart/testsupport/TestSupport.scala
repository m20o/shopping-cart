package net.badprogrammer.platform.shoppingcart.testsupport

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.Inspectors

import scala.reflect.ClassTag

abstract class ActorSpec(sys: ActorSystem = ActorSystem("CartDomainSystem", ActorSpecConfiguration()))
  extends TestKit(sys)
  with ImplicitSender
  with CompleteWordSpec
  with Inspectors {

  override protected final def afterAll(): Unit = {
    TestKit.shutdownActorSystem(sys)
    cleanupAfterSpec()
  }

  protected def cleanupAfterSpec(): Unit = {}
}

object ActorSpecConfiguration {

  // akka.persistence.journal.leveldb-shared.store.native

  // akka.persistence.journal.leveldb.native = off
  // akka.persistence.view.auto-update-interval

  //akka.persistence.journal.plugin =

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

object GlobalCounter {

  private val counter: AtomicInteger = new AtomicInteger(0)

  def next() = counter.incrementAndGet()
}

abstract class PersistentActorSpec(sys: ActorSystem = ActorSystem("BrgActorySystem")) extends ActorSpec {

  def nextId(base: String): String = s"$base-${GlobalCounter.next()}"

  def generatedId = nextId("test-actor")

  def waitingFor[T: ClassTag](v: => Any) = {
    val tag: ClassTag[T] = implicitly[ClassTag[T]]
    v
    expectMsgClass(tag.runtimeClass.asInstanceOf[Class[T]])
  }
}




