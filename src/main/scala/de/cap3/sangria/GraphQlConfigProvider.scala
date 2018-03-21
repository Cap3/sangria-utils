package de.cap3.sangria

import javax.inject.{Inject, Provider}

import play.api.Configuration

class GraphQlConfigProvider @Inject()(configuration: Configuration) extends Provider[GraphQlConfig] {
  override def get(): GraphQlConfig = GraphQlConfig(configuration.get[Configuration]("graphql"))
}
