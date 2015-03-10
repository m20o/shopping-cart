package controllers.api

import akka.util.Timeout
import net.badprogrammer.platform.shoppingcart.ShoppingCartSystem
import net.badprogrammer.platform.shoppingcart.service.Cart
import Cart.{Create, DoesNotExists, Execute}
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain.{ShoppingCartId, User}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

trait ShoppingCartApi extends Controller with ApiJsonProtocol {

  def cartSystem: ShoppingCartSystem

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(1 second)

  def retrieveCart(id: String) = Action.async { req =>


    cartSystem.send(Execute(ShoppingCartId(id), GetContent)) map {
      case DoesNotExists => NotFound
      case c: CartContent if c.notEmpty => Ok(Json.toJson(c))
      case c: CartContent if c.isEmpty => NoContent
    }
  }

  def create() = Action.async(BodyParsers.parse.json) {
    request =>
      val result = request.body.validate[User]
      Future.successful {
        result.fold(
          errors => {
            BadRequest(JsError.toFlatJson(errors))
          },

          user => {
            cartSystem.send(Create(user))
            InternalServerError("The end")
          }
        )
      }
  }
}
