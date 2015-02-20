package net.badprogrammer.shoppingcart.platform.api

import akka.pattern._
import akka.util.Timeout
import net.badprogrammer.platform.shoppingcart.command.Cart.Exists
import net.badprogrammer.platform.shoppingcart.command.{Create, Created}
import spray.http.{HttpHeaders, StatusCodes}
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

            import scala.concurrent.ExecutionContext.Implicits.global

            (shoppingCartSystem ? Create(bc.id, bc.user)).map {
              case Created(id) => ctx.withHttpResponseMapped(_.withHeaders(HttpHeaders.Location(id.value))) complete StatusCodes.Created
              case Exists => complete(StatusCodes.Conflict)
            }
            //ctx.withHttpResponseMapped(_.withHeaders(HttpHeaders.Location("1234567"))) complete StatusCodes.Created
          }
        }
    }
}