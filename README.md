[![Build Status](https://travis-ci.org/m20o/shopping-cart.svg?branch=master)](https://travis-ci.org/m20o/shopping-cart)

# Shopping Cart
       
This is a study on how to implement a minimal (but realistic) shopping cart system with `DDD` and  `eventsourcing`. Plus, within this project I'd like
to explore some issues eventually raising when introducing Scala into a fairly large enterprise with a solid Java culture. 

The following technologies has been used:

 * __Akka__ 2.3.9 as a core framework (using Scala 2.11.x);
 * __Spray__ 1.3.3 for the REST layer;
 * __ScalaTest__ 2.2.x for testing.
 
## What's included so far?

The project so far is divided into two distinct modules:

 * `core` contains the domain-specific parts of the application. Every specific actor should have a decent test suite, i.e. `ShoppingCartAggregate` has its own `ShoppingCartAggregateSpec`;
 * `server` contains the REST service. The current implementation uses [Spray](http://spray.io/) but, at this time, it is far from satisfactory.
 
## How to run it?

TODO (complete)

Right now you could analyse the system behaviour using the `specs` available on project.


## Getting started

If yout already know about Scala & SBT you can skip this section.

Since this is an [SBT](http://www.scala-sbt.org/) project, you'll probably need a recent version of Intellij IDEA (tested on 14.x) 
with Scala plugin in order to open it in a IDE. IDEA can also run Spec tests, so it's highly recommended.

Using a command line is easier to start:

 1. Run `./activator` on the project directory and wait until internet has been downloaded
 2. When the prompt appears, run `test` in order to excute all tests.
 
When tests are successful, you should see something similar to:

```
[info] Run completed in 2 seconds, 538 milliseconds.
[info] Total number of tests run: 21
[info] Suites: completed 4, aborted 0
[info] Tests: succeeded 21, failed 0, canceled 0, ignored 0, pending 0
```
If you want to play around while having your tests running at every changes, prepend test command with `~`, i.e. 

```
~ test
[info] MoneySpec:
[info] Money
[info] - should be created from a String
[info] - should be summable
[info] - should be subtractable
[info] - should be rounded as 2 decimals half-even
[info] - should be comparable
[info] PingPongActorSpec:
[info] A Ping actor
[INFO] [12/18/2014 21:27:22.335] [MySpec-akka.actor.default-dispatcher-3] [akka://MySpec/user/$a] In PingActor - received message: pong
[info] - must send back a ping on a pong
[info] A Pong actor
[INFO] [12/18/2014 21:27:22.340] [MySpec-akka.actor.default-dispatcher-4] [akka://MySpec/user/$b] In PongActor - received message: ping
[info] - must send back a pong on a ping
[info] ShoppingCartSpec:
[info] A shopping cart
[info] - must add a product
[info] - must refuse to add a product when it's not available
[info] - must add a product with a quantity
[info] - must sum quantities of the same product
[info] - must remove an existing product
[info] - must do nothing if asked to remove a product that is not present
[info] - must subtract quantities of the same product
[info] - must remove product from cart is its quantity is 0
[info] - must be cleared
[info] - must reject unknown commands
[info] A persistence cart
[info]   must recover the previous state 
[info]   - when is non empty
[INFO] [12/18/2014 21:27:23.580] [BrgActorySystem-akka.actor.default-dispatcher-5] [akka://BrgActorySystem/user/$a] Message [akka.persistence.JournalProtocol$WriteMessagesSuccessful$] from Actor[akka://BrgActorySystem/system/journal#866893466] to Actor[akka://BrgActorySystem/user/$a#-965529725] was not delivered. [1] dead letters encountered. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
[INFO] [12/18/2014 21:27:23.580] [BrgActorySystem-akka.actor.default-dispatcher-5] [akka://BrgActorySystem/user/$a] Message [akka.persistence.JournalProtocol$WriteMessageSuccess] from Actor[akka://BrgActorySystem/system/testActor2#-630366286] to Actor[akka://BrgActorySystem/user/$a#-965529725] was not delivered. [2] dead letters encountered. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
[INFO] [12/18/2014 21:27:23.580] [BrgActorySystem-akka.actor.default-dispatcher-5] [akka://BrgActorySystem/user/$l] Message [akka.persistence.JournalProtocol$ReplayMessagesSuccess$] from Actor[akka://BrgActorySystem/system/journal#866893466] to Actor[akka://BrgActorySystem/user/$l#128166001] was not delivered. [3] dead letters encountered. This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 'akka.log-dead-letters-during-shutdown'.
[info]   - when is recovered once it's started again
[info] ShoppingCartHandlerSpec:
[info] A shopping cart handler actor
[info] - must create a cart actor on demand
[info] - must forward commands to a specific actor
[info] Run completed in 2 seconds, 376 milliseconds.
[info] Total number of tests run: 21
[info] Suites: completed 4, aborted 0
[info] Tests: succeeded 21, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 3 s, completed Dec 18, 2014 9:27:23 PM
1. Waiting for source changes... (press enter to interrupt)
```

