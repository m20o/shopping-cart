package net.badprogrammer.shoppingcart.api

import akka.actor.ActorRefFactory
import net.badprogrammer.platform.shoppingcart.command.GetContent
import net.badprogrammer.platform.shoppingcart.service.Cart.{DoesNotExists, Execute}
import spray.http.StatusCodes._
import support.api.KnownCarts
import support.{Base, FakeCartSystem}

class ShoppingCartApiSpec extends Base with ShoppingCartApi {

  override implicit def actorRefFactory: ActorRefFactory = system

  override def cartSystem: ShoppingCartSystem = FakeCartSystem {
    case Execute(id, command) => command match {
      case GetContent => KnownCarts(id).getOrElse(DoesNotExists)
    }
  }

  "GET /carts/(cartId)" must {

    "give NOT FOUND if cartId does not exists" in {

      Get("/carts/not-exists") ~> api ~> check {

        status === NotFound

      }
    }

    "give NO_CONTENT if cartId exists but is empty" in {

      Get("/carts/empty") ~> api ~> check {

        status === NoContent

      }
    }


    "return JSON representation of cart" in {

      Get("/carts/full") ~> api ~> check {

        status === OK

      }

      /*
      val id = "full"

      val result = api.retrieveCart("full").apply(FakeRequest(GET, s"/carts/$id"))

      val returned = contentAsJson(result)

      status(result) mustBe 200

      returned must be(KnownCarts.asJson)
      */
    }
  }

}
