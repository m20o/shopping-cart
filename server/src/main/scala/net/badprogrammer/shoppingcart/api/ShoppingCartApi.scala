package net.badprogrammer.shoppingcart.api

import akka.util.Timeout
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.routing.{Route, HttpService}
import spray.http.MediaTypes._
import scala.concurrent.duration._
import scala.language.postfixOps

trait ShoppingCartApi extends HttpService with ApiJsonProtocol {

  def cartSystem: ShoppingCartSystem

  implicit val timeout = Timeout(1 second)

  /*
  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      } ~
        post {
          entity(as[BusinessContext]) { bc => ctx =>
            (shoppingCartSystem ? CreateCart(bc.id, bc.user)).map {
              case CartCreated(id) => ctx.withHttpResponseMapped(_.withHeaders(Location(id.value))) complete Created
              case Exists => complete(Conflict)
            }
          }
        }
    }

*/

  val api: Route = path("carts") {
    get {
      complete { "Hello" }
    }
  }
}
