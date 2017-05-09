package com.abtechsoft.metrics

/**
  * Created by abdhesh on 09/05/17.
  */

import kamon.Kamon
import com.abtechsoft.stats.DockerStats.MemoryStats
import kamon.metric.instrument.{InstrumentFactory, Memory}
import kamon.metric.{EntityRecorderFactory, GenericEntityRecorder}

class MemoryMetrics(instrumentFactory: InstrumentFactory) extends GenericEntityRecorder(instrumentFactory) {

  val usage = DiffRecordingHistogram(histogram("usage", Memory.Bytes))
  val limit = DiffRecordingHistogram(histogram("limit", Memory.Bytes))
  val maxUsage = DiffRecordingHistogram(histogram("max-usage", Memory.Bytes))

  def update(memoryStats: MemoryStats): Unit = {
    usage.record(memoryStats.`usage`)
    maxUsage.record(memoryStats.`max_usage`)
    limit.record(memoryStats.`limit`)
  }
}

object MemoryMetrics extends EntityRecorderFactory[MemoryMetrics] {
  override def category = "docker-memory"

  override def createRecorder(instrumentFactory: InstrumentFactory): MemoryMetrics = new MemoryMetrics(instrumentFactory)

  def apply(containerAlias: String): (MemoryStats) ⇒ Unit = Kamon.metrics.entity(MemoryMetrics, containerAlias).update
}