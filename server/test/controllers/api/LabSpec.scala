package controllers.api

import net.badprogrammer.platform.shoppingcart.command.GetContent
import net.badprogrammer.platform.shoppingcart.domain.User
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

class LabSpec extends PlaySpec with ApiJsonProtocol {

  val api = new ShoppingCartApi {

    val cartSystem: ShoppingCartSystem = FakeCartSystem {
      case Execute(id, command) => command match {
        case GetContent => KnownCarts(id).getOrElse(DoesNotExists)
      }
      case _ => throw new RuntimeException("Death!")
    }
  }


  "POST /carts" must {

    val johnDoe = User("jdoe", "SITE1")

    "create a new cart if the given user hasn't one" ignore {

      val result = api.create().apply(FakeRequest(POST, "/carts").withJsonBody(Json.toJson(johnDoe)))

      val future: Future[Result] = result.run

      status(future) mustBe CREATED
     // contentAsString(future) mustBe "aaaaa"
    }

    "daje" in {

      val johnDoe = User("jdoe", "SITE1")

      val result = api.prova().apply(FakeRequest().withJsonBody(Json.toJson(johnDoe)))


      status(result.run) mustBe CREATED
      // contentAsString(future) mustBe "aaaaa"
    }

  }
}





