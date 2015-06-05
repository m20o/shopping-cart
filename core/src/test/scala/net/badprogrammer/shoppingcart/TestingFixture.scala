package net.badprogrammer.shoppingcart

import akka.actor._
import net.badprogrammer.shoppingcart.domain.{Article, Money, Quote}

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

    class ArticleHander extends Actor with ActorLogging {
      def receive: Receive = {
        case q@Quote(_, article, _) if article == Cocaine => sender ! Quote.Unsuccessful(q, "We cannot sell drugs")
        case q@Quote(_, article, _) if article != Cocaine => sender ! Quote.Successful(q, pricing(article))
        case x => log.error("Uknonwn article: {}", x)
      }
    }

    def apply(implicit system: ActorSystem) = system.actorOf(Props[ArticleHander])
  }
}
