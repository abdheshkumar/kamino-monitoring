package com.abtechsoft.stats

/**
  * Created by abdhesh on 09/05/17.
  */
object DockerStats {

  case class NetworkStats(`rx_bytes`: Long,
                          `rx_packets`: Long,
                          `rx_dropped`: Long,
                          `rx_errors`: Long,
                          `tx_bytes`: Long,
                          `tx_packets`: Long,
                          `tx_dropped`: Long,
                          `tx_errors`: Long)

  case class MemoryStats(`max_usage`: Long,
                         `usage`: Long,
                         `failcnt`: Long,
                         `limit`: Long)

  case class CpuStats(`cpu_usage`: CpuUsage,
                      `system_cpu_usage`: Long)

  case class CpuUsage(`total_usage`: Long,
                      `percpu_usage`: Seq[Long],
                      `usage_in_kernelmode`: Long,
                      `usage_in_usermode`: Long)

}