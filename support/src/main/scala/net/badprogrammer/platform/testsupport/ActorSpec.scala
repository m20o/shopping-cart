package net.badprogrammer.platform.testsupport

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.Inspectors

abstract class ActorSpec(sys: ActorSystem = ActorSystems.InMemoryPersistence)
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










