package net.badprogrammer.platform.shoppingcart.domain


import scala.language.postfixOps
import scala.math.BigDecimal.RoundingMode

object Currency {

  import java.math.MathContext.DECIMAL32

  val Decimals = 2

  val Cents = BigDecimal(10000L, Decimals, DECIMAL32)

  def create(value: String) = (BigDecimal(value) * Cents).setScale(0, RoundingMode.HALF_EVEN) / Cents

}

case class Money(private val amount: BigDecimal) extends Ordered[Money] {

  import net.badprogrammer.platform.shoppingcart.domain.Currency._

  val cents: Long = amount * Cents toLong

  def this(amount: String) = this(Currency.create(amount))

  def +(that: Money) = new Money(this.amount + that.amount)

  def -(that: Money) = new Money(this.amount - that.amount)

  def *(factor: Long) = new Money(this.amount * factor)

  override def compare(that: Money): Int = this.amount.compare(that.amount)
}

object Money {

  val Zero = Money("0.00")

  def apply(amount: String) = new Money(amount)

  def fromCents(amount: Long) = new Money(BigDecimal(amount) / Currency.Cents)

}


