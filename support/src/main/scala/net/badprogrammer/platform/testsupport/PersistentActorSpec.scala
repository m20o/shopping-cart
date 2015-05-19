package net.badprogrammer.platform.testsupport

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Terminated}

import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

abstract class PersistentActorSpec(sys: ActorSystem = ActorSystems.InMemoryPersistence) extends ActorSpec {

  def waitingFor[T: ClassTag](v: => Any) = {
    val tag: ClassTag[T] = implicitly[ClassTag[T]]
    v
    expectMsgClass(tag.runtimeClass.asInstanceOf[Class[T]])
  }

  def ignoring[T: ClassTag]: Unit = {
    val tag: ClassTag[T] = implicitly[ClassTag[T]]
    val clazz: Class[_] = tag.runtimeClass
    ignoreMsg { case t => t.getClass.isAssignableFrom(clazz)}
  }

  def waitFor(duration: FiniteDuration) = Thread.sleep(duration.toMillis)

  def sequentialId(name: String) = s"$name-${next()}"

  /**
   * Terminate the given actor and wait for proper cleanup from Akka.
   * @param ref
   * @return
   */
  def terminate(ref: ActorRef) = {
    watch(ref)
    waitingFor[Terminated] {
      ref ! PoisonPill
    }
    unwatch(ref)
  }

  private object next {
    private val counter: AtomicInteger = new AtomicInteger(0)
    def apply() = counter.incrementAndGet()
  }
}




