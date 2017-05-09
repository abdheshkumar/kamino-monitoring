package com.abtechsoft

/**
  * Created by abdhesh on 09/05/17.
  */

import kamon.Kamon

trait KamonSupport {
  Kamon.start()
  sys.addShutdownHook(Kamon.shutdown())
}