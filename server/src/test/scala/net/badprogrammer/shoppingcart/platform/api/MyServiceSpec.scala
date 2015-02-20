package net.badprogrammer.shoppingcart.platform.api

import akka.actor.ActorRef
import net.badprogrammer.platform.shoppingcart.command.Create
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.platform.shoppingcart.service.{ShoppingCartIdFactory, ShoppingCartService}
import org.scalatest.{Matchers, WordSpec}
import spray.http.StatusCodes
import spray.json.DefaultJsonProtocol
import spray.testkit.{RouteTest, ScalatestRouteTest}


object BusinessProtocol extends DefaultJsonProtocol {
  implicit val businessFormat = jsonFormat2(BusinessContext)
}

class MyServiceSpec
  extends WordSpec
  with ScalatestRouteTest
  with Matchers
  with MyService
  with FakeShoppingCartServiceProvider {

  def actorRefFactory = system // connect the DSL to the test ActorSystem

  "A service" should {

    "work" in {

      Get("/") ~> myRoute ~> check {

        responseAs[String] should include("hello")

      }
    }

    "create a new cart" in {

      Post("/", BusinessContext("GreatSellSite", "appa")) ~> myRoute ~> check {

        status shouldBe StatusCodes.Created
        response.headers.find(_.name == "Location").map(_.value).get should include("1")
      }

    }

    "reject creation if a cart already exists" in {

      Post("/", BusinessContext("GreatSellSite", "appa")) ~> myRoute ~> check {

        status shouldBe StatusCodes.Created
        response.headers.find(_.name == "Location").map(_.value).get should include("1")
      }

    }

  }
}

object FakeIdGenerator extends ShoppingCartIdFactory {

  private var number = 0

  override def apply(v1: Create): ShoppingCartId = ShoppingCartId((number += 1).toString)
}

trait FakeShoppingCartServiceProvider extends ShoppingCartServiceProvider {

  this: RouteTest =>

  override def shoppingCartSystem: ActorRef = system.actorOf(ShoppingCartService.props(null, FakeIdGenerator))
}