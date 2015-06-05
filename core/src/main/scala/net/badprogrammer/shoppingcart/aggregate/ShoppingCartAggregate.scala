package net.badprogrammer.shoppingcart.aggregate

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.persistence.PersistentActor
import net.badprogrammer.shoppingcart.command._
import net.badprogrammer.shoppingcart.domain.{Article, Money, Quote, ShoppingCartId}

class ShoppingCartAggregate(val id: ShoppingCartId, val catalog: ActorRef) extends PersistentActor with ActorLogging {

  private val content = new ShoppingCart()

  def persistenceId: String = self.path.name

  override def receiveRecover: Receive = {
    case e: ContentEvent => updateContent(e)
  }

  private def updateContent(event: ContentEvent) = {
    event match {
      case e: ArticleAdded => addArticleToCart(e)
      case e: ArticleRemoved => removeArticleFromCart(e)
      case CartCleared => content.clear
    }
  }

  private def removeArticleFromCart(removed: ArticleRemoved) {
    content.remove(removed.article, removed.quantity)
  }

  private def addArticleToCart(added: ArticleAdded) {
    content.add(added.article, added.quantity)
  }

  def receiveCommand = LoggingReceive {
    handleAddArticleCommand orElse
      handleRemoveArticleCommand orElse
      handleClearCartCommand orElse
      handleGetCartContentCommand orElse
      rejectUnknownCommand
  }

  private def bus = context.system.eventStream

  def logging(handler: String)(block : => Receive): Receive = {
    case x => {
      log.debug("Handler {} received {}", handler, x)
      block(x)
    }
  }

  private def handleAddArticleCommand: Receive = {
    case AddArticle(article, quantity) => {
      log.debug("Bon, mando a {}", catalog.path)
      catalog ! Quote(sender(), article, quantity)
    }
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
    case GetContent => sender() ! CartContent(content.items)
  }

  private def rejectUnknownCommand: Receive = {
    case x => sender() ! UnknownCommand(x)
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
      content.updateQuotation(article -> money)
      bus.publish(event)
    }
  }
}

object ShoppingCartAggregate {
  def props(id: ShoppingCartId, catalog: ActorRef) = Props(classOf[ShoppingCartAggregate], id, catalog)
}
