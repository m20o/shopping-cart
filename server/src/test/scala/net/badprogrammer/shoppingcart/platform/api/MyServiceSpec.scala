package net.badprogrammer.shoppingcart.platform.api

import akka.actor.{ActorSystem, ActorRef}
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.service.{ShoppingCartIdGenerator, ShoppingCartService}
import net.badprogrammer.shoppingcart.platform.{ActorSpecConfiguration, TestingFixture}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import spray.http.HttpHeaders
import spray.http.StatusCodes._
import spray.json.DefaultJsonProtocol
import spray.testkit.{RouteTest, ScalatestRouteTest}

object BusinessProtocol extends DefaultJsonProtocol {
  implicit val businessFormat = jsonFormat2(BusinessContext)
}

abstract class Base extends WordSpec with ScalatestRouteTest with Matchers with BeforeAndAfter {
  override protected def createActorSystem(): ActorSystem = {
    ActorSystem("SpraySystem", ActorSpecConfiguration())
  }
}


class MyServiceSpec
  extends Base
  with MyService
  with ArticleProvider
  with FakeShoppingCartServiceProvider {

  def actorRefFactory = system // connect the DSL to the test ActorSystem

  "A service" should {

    "work" in {

      Get("/") ~> myRoute ~> check {

        responseAs[String] should include("hello")

      }
    }

    "create a new cart" in {

      Post("/", BusinessContext("MerchantCorp", "john@doe.com")) ~> myRoute ~> check {

        status === Created
        val location = header[HttpHeaders.Location]
        location should be('defined)
        location.map(_.uri) should be('defined)
      }

    }

    "reject creation if a cart already exists" ignore {

      Post("/", BusinessContext("GreatSellSite", "appa")) ~> myRoute ~> check {
        status === Created
      }

      Post("/", BusinessContext("GreatSellSite", "appa")) ~> myRoute ~> check {

        status === Conflict

      }

    }

  }
}

object FakeIdGenerator extends ShoppingCartIdGenerator {

  private var number = 0

  override def apply(user: User): ShoppingCartId = {
    number += 1
    ShoppingCartId(number.toString)
  }
}

trait FakeShoppingCartServiceProvider extends ShoppingCartServiceProvider {

  this: RouteTest with ArticleProvider =>

  override def shoppingCartSystem: ActorRef = {
    system.actorOf(ShoppingCartService.props(articleRepository, FakeIdGenerator))
  }
}

trait ArticleProvider {

  this: RouteTest with BeforeAndAfter =>

  def articleRepository: ActorRef = system.actorOf(TestingFixture.props())

  /*
  private var repositoryRef: Option[ActorRef] = None

  def articleRepository: ActorRef = {
    repositoryRef.getOrElse {
      val ref = system.actorOf(TestingFixture.props())
      repositoryRef = Some(ref)
      ref
    }
  }

  after {
    repositoryRef.foreach(_ ! PoisonPill)
    repositoryRef = None
  }
  */


}