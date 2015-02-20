package net.badprogrammer.platform.shoppingcart.aggregate

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.persistence.PersistentActor
import net.badprogrammer.platform.shoppingcart.command._
import net.badprogrammer.platform.shoppingcart.domain._
import net.badprogrammer.platform.shoppingcart.query._

class ShoppingCartAggregate(val id: ShoppingCartId, val articleRepository: ActorRef) extends PersistentActor with ActorLogging {

  val persistenceId: String = id.value

  private val content = new ShoppingCartContent()

  private val bus = context.system.eventStream

  override def receiveRecover: Receive = {
    case e: ContentEvent => updateContent(e)
  }

  def receiveCommand = LoggingReceive {
    handleAddArticleCommand orElse
      handleRemoveArticleCommand orElse
      handleClearCartCommand orElse
      handleGetCartContentCommand orElse
      rejectUnknownCommand
  }

  private def handleAddArticleCommand: Receive = {
    case AddArticle(article, quantity) => articleRepository ! Quote(sender(), article, quantity)

    case q@Quote.Unsuccessful(quote, reason) => notifyThatArticleIsNotAvailable(q)
    case q@Quote.Successful(quote, price) => handleAddAvailableArticle(q)
  }

  private def notifyThatArticleIsNotAvailable(failure: Quote.Unsuccessful): Unit = {
    failure.source ! ArticleNotAvailable(failure.quote.article, failure.quote.quantity, failure.reason)
  }

  private def handleAddAvailableArticle(response: Quote.Successful): Unit = {
    persistEventAndUpdateContent(ArticleAdded(response.quote.article, response.quote.quantity), response.source)
    persistAndUpdateQuotation(response.quote.article, response.price)
  }

  private def handleRemoveArticleCommand: Receive = {
    case RemoveArticle(article, quantity) if content.contains(article) => persistEventAndUpdateContent(ArticleRemoved(article, quantity))
    case RemoveArticle(_, _) => // do nothing
  }

  private def handleClearCartCommand: Receive = {
    case Clear => persistEventAndUpdateContent(CartCleared)
  }

  private def handleGetCartContentCommand: Receive = {
    case GetContent if content.isEmpty => sender() ! EmptyCart
    case GetContent if content.nonEmpty => sender() ! CartContent(content.items)
  }

  private def rejectUnknownCommand: Receive = {
    case x => sender() ! UnknownCommand(x)
  }

  private def updateContent(event: ContentEvent) = {
    event match {
      case e: ArticleAdded => addArticleToCart(e)
      case e: ArticleRemoved => removeArticleFromCart(e)
      case CartCleared => content.empty
    }
  }

  private def removeArticleFromCart(removed: ArticleRemoved) {
    content.remove(removed.article, removed.quantity)
  }

  private def addArticleToCart(added: ArticleAdded) {
    content.add(added.article, added.quantity)
  }

  private def persistEventAndUpdateContent(e: ContentEvent, replyTo: ActorRef = sender()) {
    persist(e) { event =>
      updateContent(event)
      replyTo ! event
      bus.publish(event)
    }
  }

  private def persistAndUpdateQuotation(article: Article, money: Money): Unit = {
    persist(ArticleQuoted(article, money)) { event =>
      bus.publish(event)
    }
  }

}

object ShoppingCartAggregate {

  def props(id: ShoppingCartId, articleRepository: ActorRef) = Props(classOf[ShoppingCartAggregate], id, articleRepository)

}