package controolers.api

import akka.actor.{Props, Actor, ActorSystem, ActorRef}
import controllers.api.ShoppingCartApi
import net.badprogrammer.platform.shoppingcart.ShoppingCartSystem
import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCartContent
import net.badprogrammer.platform.shoppingcart.command.Cart.DoesNotExists
import net.badprogrammer.platform.shoppingcart.command.Execute
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, Article}
import net.badprogrammer.platform.shoppingcart.query.{CartContent, GetContent}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{Json, JsValue}
import setup.ShoppingCartSystemProvider
import support.FakeCartSystem
import scala.concurrent.Future

import org.scalatest._
import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class ShoppingCartApiSpec extends PlaySpec {

  val api = new ShoppingCartApi with Controller with ShoppingCartSystemProvider {


    val cartSystem: ShoppingCartSystem = FakeCartSystem {
      case Execute(id, command) => command match {
        case GetContent => KnownCarts(id).getOrElse(DoesNotExists)
      }
    }
  }

  "GET /carts/(cartId)" must {

    "give NOT FOUND if cartId does not exists" in {

      val result = api.retrieveCart("unexistent").apply(FakeRequest(GET, "/carts"))

      status(result) mustBe 404

    }

    "give NO_CONTENT if cartId exists but is empty" in {

      val result = api.retrieveCart("empty").apply(FakeRequest(GET, "/carts"))

      status(result) mustBe 204
    }

    "return JSON representation of cart" in {

      val id = "full"

      val result = api.retrieveCart("full").apply(FakeRequest(GET, s"/carts/$id"))

      val returned = contentAsJson(result)

      status(result) mustBe 200

      returned must be(KnownCarts.asJson)
    }
  }
}


object KnownCarts {

  val asJson = Json.parse( """{"items":[{"article":{"id":"food"},"quantity":1},{"article":{"id":"wine"},"quantity":1}]}""")

  val full = {
    val c = new ShoppingCartContent()
    c.add(Article("food"), 1)
    c.add(Article("wine"), 1)
    c
  }

  val carts = Map.empty + (ShoppingCartId("full") -> full) + (ShoppingCartId("empty") -> new ShoppingCartContent())

  def apply(id: ShoppingCartId): Option[CartContent] = carts.get(id).map { x => CartContent(x.items)}

}
