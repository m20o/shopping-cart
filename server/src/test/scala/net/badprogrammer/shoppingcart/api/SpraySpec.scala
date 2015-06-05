package net.badprogrammer.shoppingcart.api

import java.util.concurrent.atomic.AtomicInteger

import net.badprogrammer.platform.testsupport.CompleteWordSpec
import org.scalatest.WordSpec
import spray.testkit.ScalatestRouteTest

abstract class SpraySpec extends WordSpec with ScalatestRouteTest with CompleteWordSpec {

  private val counter = new AtomicInteger()

  def nextCartId = s"cart~${counter.incrementAndGet()}"

}
