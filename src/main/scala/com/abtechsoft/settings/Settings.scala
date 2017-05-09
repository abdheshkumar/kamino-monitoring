package com.abtechsoft.settings

/**
  * Created by abdhesh on 09/05/17.
  */

import java.util.Map.Entry
import com.typesafe.config.{ConfigFactory, ConfigObject, ConfigValue}
import scala.collection.JavaConverters._

object Settings {
  private val config = ConfigFactory.load()

  val dockerHost = config.getString("docker.host")
  val dockerPort = config.getInt("docker.port")

  lazy val containers: Map[String, String] = {
    val list: Iterable[ConfigObject] = config.getObjectList("docker.containers").asScala
    (for {
      item: ConfigObject ← list
      entry: Entry[String, ConfigValue] ← item.entrySet().asScala
      containerId = entry.getKey
      containerAlias = entry.getValue.unwrapped().toString
    } yield (containerId, containerAlias)).toMap
  }
}