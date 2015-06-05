package net.badprogrammer.shoppingcart.api

import akka.actor.ActorRefFactory
import net.badprogrammer.platform.testsupport.ActorSystems
import net.badprogrammer.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.shoppingcart.service.{Cart, ShoppingCartIdGenerator}
import org.scalatest.{BeforeAndAfter, GivenWhenThen}
import spray.http.StatusCodes

class ShoppingCartApiSpec extends SpraySpec with ShoppingCartApi with BeforeAndAfter {

  val actorRefFactory: ActorRefFactory = ActorSystems.InMemoryPersistence

  implicit val idGenerator: ShoppingCartIdGenerator = (u) => ShoppingCartId(s"${u.id}~${u.context}")

  "The Api" should {

    val caller = Caller("jdoe", "nowhere.com")
    val cartId = "jdoe~nowhere.com"

    "create a new shopping cart" in {

      Post("/carts", caller) ~> routes ~> check {
        assert(status === StatusCodes.Created)
        assert(responseAs[String] === cartId)
      }
    }

    "reject with a 409 if a cart already exists for the given caller" in {
      Post("/carts", caller) ~> routes ~> check {
        assert(status === StatusCodes.Conflict)
      }
    }

    "add an article to a shopping cart" in {
      Put(s"/carts/$cartId/articles", "food") ~> routes ~> check {
        assert(status == StatusCodes.OK)
        assert(responseAs[CartItem] == CartItem("food", "food", 1))
      }
    }

    "add the same article multiple times" in {

      Put(s"/carts/$cartId/articles", "food") ~> routes ~> check {
        assert(status == StatusCodes.OK)
      }

      Put(s"/carts/$cartId/articles", "food") ~> routes ~> check {
        assert(status == StatusCodes.OK)
      }

      Get(s"/carts/$cartId/articles/food") ~> routes ~> check {
        responseAs[CartItem] should be(CartItem("food", "food", 3))
      }
    }
  }
}
