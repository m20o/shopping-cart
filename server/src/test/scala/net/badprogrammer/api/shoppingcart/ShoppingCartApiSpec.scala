package net.badprogrammer.api.shoppingcart

import akka.actor.ActorRefFactory
import net.badprogrammer.platform.shoppingcart.domain.{Item, ShoppingCartId}
import net.badprogrammer.platform.shoppingcart.service.ShoppingCartIdGenerator
import net.badprogrammer.platform.testsupport.ActorSystems
import spray.http.StatusCodes

class ShoppingCartApiSpec extends SpraySpec with ShoppingCartApi {

  val actorRefFactory: ActorRefFactory = ActorSystems.InMemoryPersistence

  implicit val idGenerator: ShoppingCartIdGenerator = (u) => ShoppingCartId(s"${u.id}~${u.context}")

  "The Api" should {

    val caller = Caller("jdoe", "nowhere.com")

    val expected = "jdoe~nowhere.com"

    "create a new shopping cart" in {
      Post("/carts", caller) ~> routes ~> check {
        assert(status === StatusCodes.Created)
        assert(responseAs[String] === expected)
      }
    }

    "reject with a 409 if a cart already exists for the given caller" in {
      Post("/carts", caller) ~> routes ~> check {
        assert(status === StatusCodes.Conflict)
      }
    }

    "add an article to a shopping cart" in {
      Put(s"/carts/$expected/articles", "food") ~> routes ~> check {
        assert(status === StatusCodes.OK)
        assert(responseAs[CartItem] === CartItem("food", "food", 1))
      }
    }
  }
}
