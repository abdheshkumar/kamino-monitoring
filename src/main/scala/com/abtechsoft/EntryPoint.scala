package com.abtechsoft

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import com.abtechsoft.metrics.{CpuMetrics, MemoryMetrics, NetworkMetrics}
import com.abtechsoft.stats.DockerStats.{CpuStats, MemoryStats, NetworkStats}
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by abdhesh on 09/05/17.
  */
object EntryPoint extends App with KamonSupport {

  import settings.Settings._

  type ContainerStats = JValue

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val formats = DefaultFormats

  val logger = Logging(system, "Docker-Monitor")

  val network = Flow[ContainerStats].map(stats ⇒ (stats \ "network").extract[NetworkStats])
  val memory = Flow[ContainerStats].map(stats ⇒ (stats \ "memory_stats").extract[MemoryStats])
  val cpu = Flow[ContainerStats].map(stats ⇒ (stats \ "cpu_stats").extract[CpuStats])

  val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnection(dockerHost, dockerPort)

  def flowWriter(writeNetwork: Sink[NetworkStats, Future[Done]],
                 writeMemory: Sink[MemoryStats, Future[Done]],
                 writeCpu: Sink[CpuStats, Future[Done]]): Sink[ContainerStats, NotUsed] = {
    Sink.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val stats = builder.add(Broadcast[ContainerStats](3))
        stats.out(0) ~> network ~> writeNetwork
        stats.out(1) ~> memory ~> writeMemory
        stats.out(2) ~> cpu ~> writeCpu
        SinkShape(stats.in)
      })
  }


  def chunkConsumer(containerAlias: String): Sink[HttpResponse, Future[Done]] = Sink.foreach[HttpResponse] { response ⇒
    if (response.status == StatusCodes.OK) {
      logger.info(s"Getting stats from container: $containerAlias")

      val writeNetwork = Sink.foreach(NetworkMetrics(containerAlias))
      val writeMemory = Sink.foreach(MemoryMetrics(containerAlias))
      val writeCpu = Sink.foreach(CpuMetrics(containerAlias))
      response.entity.dataBytes.map(chunk => parse(chunk.utf8String)).to(flowWriter(writeNetwork, writeMemory, writeCpu)).run()
    } else {
      logger.error(s"Cannot connect to the Docker container: $containerAlias, with response status ${response.status}")
    }
  }

  def makeRequest(containerId: String, containerAlias: String): Future[Unit] = Future {
    Source.single(HttpRequest(uri = s"/v1.27/containers/$containerId/stats"))
      .via(connectionFlow)
      .runWith(chunkConsumer(containerAlias))
  }

  containers.foreach {
    case (containerId, containerAlias) ⇒
      makeRequest(containerId, containerAlias)
  }

  System.in.read()
  system.terminate()
  system.awaitTermination()
}
