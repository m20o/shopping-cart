package net.badprogrammer.shoppingcart.api

import net.badprogrammer.platform.testsupport.CompleteWordSpec
import org.scalatest.WordSpec
import spray.testkit.ScalatestRouteTest

abstract class SpraySpec extends WordSpec with ScalatestRouteTest with CompleteWordSpec
