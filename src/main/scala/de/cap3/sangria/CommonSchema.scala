package de.cap3.sangria

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{Instant, ZoneOffset}

import play.api.libs.json.{JsValue, Json}
import sangria.schema.{ScalarAlias, StringType}
import sangria.validation.ValueCoercionViolation

import scala.language.implicitConversions

object CommonSchema {

  case object InstantViolation extends ValueCoercionViolation("Invalid date")

  implicit val InstantType = ScalarAlias[Instant, String](StringType,
    toScalar = _.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    fromScalar = dateString => try Right(Instant.parse(dateString)) catch {
      case _: DateTimeParseException => Left(InstantViolation)
    }
  )

  case object JsValueViolation extends ValueCoercionViolation("Invalid Json")

  implicit val JsonType = ScalarAlias[JsValue, String](StringType,
    toScalar = js => Json.prettyPrint(js),
    fromScalar = value => try Right(Json.parse(value)) catch {
      case _: Throwable => Left(JsValueViolation)
    }
  )
}
