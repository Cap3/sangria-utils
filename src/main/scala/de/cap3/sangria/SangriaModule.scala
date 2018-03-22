package de.cap3.sangria

import play.api.inject.Module
import play.api.{Configuration, Environment}

class SangriaModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind(classOf[SangriaConfig]).toProvider(classOf[SangriaConfigProvider])
  )
}
