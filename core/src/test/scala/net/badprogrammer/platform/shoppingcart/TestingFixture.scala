package net.badprogrammer.platform.shoppingcart

import akka.actor._
import net.badprogrammer.platform.shoppingcart.domain._

object TestingFixture {

  val Flight = Article("flight")
  val HotDog = Article("hot-dog")
  val Cocaine = Article("cocaine")

  val pricing = {
    val builder = Map.newBuilder[Article, Money]
    builder += (Flight -> Money("50.50"))
    builder += (HotDog -> Money("10.10"))
    builder.result()
  }

  object FakeArticleRepository {

    private

    class ArticleHander extends Actor {
      def receive: Receive = {
        case q@Quote(_, article, _) if article == Cocaine => sender ! Quote.Unsuccessful(q, "We cannot sell drugs")
        case q@Quote(_, article, _) if article != Cocaine => sender ! Quote.Successful(q, pricing(article))
        case x => println(s"WTF is $x")
      }
    }

    def apply(implicit system: ActorSystem) = system.actorOf(Props[ArticleHander])
  }

}
