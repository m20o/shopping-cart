package controllers.api

import net.badprogrammer.platform.shoppingcart.domain.{Article, Item}
import net.badprogrammer.platform.shoppingcart.query.CartContent
import play.api.libs.json._

trait ApiJsonProtocol {

  implicit val articleFormat: Format[Article] = Json.format[Article]

  implicit val itemFormat: Format[Item] = Json.format[Item]

  implicit  val cartContentFormat: Format[CartContent] = Json.format[CartContent]

}
