package net.badprogrammer.shoppingcart.platform.api

import akka.pattern._
import akka.util.Timeout
import net.badprogrammer.platform.shoppingcart.command.Cart.Exists
import net.badprogrammer.platform.shoppingcart.command.{Create => CreateCart, Created => CartCreated}
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.routing.HttpService


/*
// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

*/

import scala.concurrent.ExecutionContext.Implicits.global

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService with ApiJsonProtocol with ShoppingCartServiceProvider {

  import spray.http.MediaTypes._

  import scala.concurrent.duration._

  implicit val timeout = Timeout(1 second)

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
}