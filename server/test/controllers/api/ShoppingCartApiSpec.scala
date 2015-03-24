package controllers.api

import net.badprogrammer.platform.shoppingcart.aggregate.ShoppingCart
import net.badprogrammer.platform.shoppingcart.command.{CartContent, GetContent}
import net.badprogrammer.platform.shoppingcart.domain.{Article, Money, ShoppingCartId, User}
import net.badprogrammer.platform.shoppingcart.service.Cart.{DoesNotExists, Execute}
import net.badprogrammer.shoppingcart.api.ShoppingCartSystem
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import support.FakeCartSystem

import scala.concurrent.Future
import scala.language.postfixOps

class ShoppingCartApiSpec extends PlaySpec with ApiJsonProtocol {

  val api = new ShoppingCartApi {

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

  "POST /carts" must {

    val johnDoe = User("jdoe", "SITE1")

    "create a new cart if the given user hasn't one" in {

      val result = api.create().apply(FakeRequest(POST, "/carts").withJsonBody(Json.toJson(johnDoe)))

      val future: Future[Result] = result.run

      status(future) mustBe CREATED
     // contentAsString(future) mustBe "aaaaa"
    }
  }
}

object Articles {

  val pasta = Article("pasta with tomato sauce")
  val wine = Article("red wine glass")
  val coffee = Article("coffee")
}


object KnownCarts {

  import controllers.api.Articles._

  val asJson = Json.parse( """{"items":[{"article":{"id":"pasta with tomato sauce"},"quantity":1},{"article":{"id":"red wine glass"},"quantity":2}, {"article":{"id":"coffee"},"quantity":1}]}""")

  val items = (pasta -> 1) :: (wine -> 2) :: (coffee -> 1) :: Nil
  val prices: Map[Article, Money] = (pasta -> "8.50") :: (wine -> "7.20") :: (coffee -> "1.00") :: Nil map (l => l._1 -> Money(l._2)) toMap


  val full = {
    val c = new ShoppingCart()
    items.foreach { i =>
      c.add(i)
      c.updateQuotation(i._1 -> prices(i._1))
    }
    c
  }

  val carts = Map.empty + (ShoppingCartId("full") -> full) + (ShoppingCartId("empty") -> new ShoppingCart())

  def apply(id: ShoppingCartId): Option[CartContent] = carts.get(id).map { x => CartContent(x.items)}

}
