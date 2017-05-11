package com.abtechsoft.metrics

/**
  * Created by abdhesh on 09/05/17.
  */

import com.abtechsoft.stats.DockerStats.NetworkStats
import kamon.Kamon
import kamon.metric.instrument._
import kamon.metric.{EntityRecorderFactory, GenericEntityRecorder}
import kamon.system.sigar.DiffRecordingHistogram

class NetworkMetrics(instrumentFactory: InstrumentFactory) extends GenericEntityRecorder(instrumentFactory) {

  val receivedBytes = DiffRecordingHistogram(histogram("rx-bytes", Memory.Bytes))
  val transmittedBytes = DiffRecordingHistogram(histogram("tx-bytes", Memory.Bytes))
  val receiveErrors = DiffRecordingHistogram(histogram("rx-errors"))
  val receivePackets = DiffRecordingHistogram(histogram("tx-packets"))
  val transmitErrors = DiffRecordingHistogram(histogram("tx-errors"))
  val receiveDrops = DiffRecordingHistogram(histogram("rx-dropped"))
  val transmitDrops = DiffRecordingHistogram(histogram("tx-dropped"))
  val transmitPackets = DiffRecordingHistogram(histogram("tx-packets"))

  def update(networkStats: NetworkStats): Unit = {
    println(networkStats)
    receivedBytes.record(networkStats.`rx_bytes`)
    transmittedBytes.record(networkStats.`tx_bytes`)
    receiveErrors.record(networkStats.`rx_errors`)
    receivePackets.record(networkStats.`rx_packets`)
    transmitErrors.record(networkStats.`tx_errors`)
    receiveDrops.record(networkStats.`rx_dropped`)
    transmitDrops.record(networkStats.`tx_dropped`)
    transmitPackets.record(networkStats.`tx_packets`)
  }
}

object NetworkMetrics extends EntityRecorderFactory[NetworkMetrics] {
  override def category = "docker-network"

  override def createRecorder(instrumentFactory: InstrumentFactory): NetworkMetrics = new NetworkMetrics(instrumentFactory)

  def apply(containerAlias: String): (NetworkStats) => Unit = Kamon.metrics.entity(NetworkMetrics, containerAlias).update
}