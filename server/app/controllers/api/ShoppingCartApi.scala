package controllers.api

import akka.util.Timeout
import net.badprogrammer.platform.shoppingcart.command.Cart.DoesNotExists
import net.badprogrammer.platform.shoppingcart.command.Execute
import net.badprogrammer.platform.shoppingcart.domain.ShoppingCartId
import net.badprogrammer.platform.shoppingcart.query.{CartContent, GetContent}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import setup.ShoppingCartSystemProvider

import scala.concurrent.duration._
import scala.language.postfixOps

trait ShoppingCartApi extends ApiJsonProtocol{

  this: Controller with ShoppingCartSystemProvider =>

  import scala.concurrent.ExecutionContext.Implicits.global

  def retrieveCart(id: String) = Action.async { implicit req =>

    implicit val timeout = Timeout(1 second)

    cartSystem.send(Execute(ShoppingCartId(id),  GetContent)) map {
      case DoesNotExists => NotFound
      case c: CartContent if c.notEmpty => Ok(Json.toJson(c))
      case c: CartContent if c.isEmpty=> NoContent
    }
  }

}
