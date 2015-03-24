package support

import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import spray.testkit.ScalatestRouteTest

abstract class Base extends WordSpec with ScalatestRouteTest with Matchers with BeforeAndAfter {
  override protected def createActorSystem(): ActorSystem = {
    ActorSystem("SpraySystem", ActorSpecConfiguration())
  }
}
