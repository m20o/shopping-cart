package net.badprogrammer.platform.testsupport

import com.typesafe.config.ConfigFactory


object ActorSpecConfiguration {

  private val loglevel = "INFO"

  private val config =
    s"""
       |akka {
       |
       |loglevel = "$loglevel"
       |
       |log-dead-letters = 0
       |log-dead-letters-during-shutdown = off
       |
       |actor {
       |  debug {
       |    # enable function of LoggingReceive, which is to log any received message at
       |    # DEBUG level
       |    receive = on
       |  }
       |}
       |
       |persistence {
       |
       |  journal.plugin = "inmemory-journal"
       |
       |  snapshot-store.plugin = "inmemory-snapshot-store"
       |
       | view.auto-update-interval = 1 ms
       |}
       |}
    """.stripMargin

  def apply() = ConfigFactory.parseString(config)
}








