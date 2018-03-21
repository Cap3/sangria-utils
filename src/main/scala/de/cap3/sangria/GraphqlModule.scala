package de.cap3.sangria

import play.api.inject.Module
import play.api.{Configuration, Environment}

class GraphqlModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind(classOf[GraphQlConfig]).toProvider(classOf[GraphQlConfigProvider])
  )
}
