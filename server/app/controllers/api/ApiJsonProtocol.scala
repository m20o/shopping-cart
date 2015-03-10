package controllers.api

import net.badprogrammer.platform.shoppingcart.command.CartContent
import net.badprogrammer.platform.shoppingcart.domain.{User, Article, Item}
import play.api.libs.json._

trait ApiJsonProtocol {

  implicit val articleFormat: Format[Article] = Json.format[Article]

  implicit val itemFormat: Format[Item] = Json.format[Item]

  implicit val cartContentFormat: Format[CartContent] = Json.format[CartContent]

  implicit val userFormat: Format[User] = Json.format[User]

}
