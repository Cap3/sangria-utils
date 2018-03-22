# sangria-utils

[![Build Status](https://travis-ci.org/Cap3/sangria-utils.svg?branch=master)](https://travis-ci.org/Cap3/sangria-utils)

Utils and snippets for the sangria grapqhl library for scala.

## <a class="anchor" name="install"></a>Install

build.sbt

```scala
resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "de.cap3" %% "sangria-utils" % "0.1-SNAPSHOT"
)
```

conf/application.conf

```
play.modules.enabled += "de.cap3.sangria.SangriaModule"
```

## <a class="anchor" name="getting-started"></a> Getting Started

### Common Scalar Types

To use the common scalar types, you have to add the following import:

```scala
import de.cap3.sangria.CommonSchema._
```
