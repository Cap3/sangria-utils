package de.cap3.sangria

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import play.api.libs.json.{JsValue, Json}
import sangria.schema.{FloatType, IntType, ScalarAlias, StringType}
import sangria.validation.ValueCoercionViolation

import scala.language.implicitConversions

object ScalarAliases {

  case object InstantViolation extends ValueCoercionViolation("Invalid date")

  implicit val InstantType = ScalarAlias[Instant, String](StringType,
    toScalar = _.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    fromScalar = dateString => try Right(Instant.parse(dateString)) catch {
      case _: DateTimeParseException => Left(InstantViolation)
    })

  case object JsValueViolation extends ValueCoercionViolation("Invalid Json")

  implicit val JsValueType = ScalarAlias[JsValue, String](StringType,
    toScalar = js => Json.prettyPrint(js),
    fromScalar = value => try Right(Json.parse(value)) catch {
      case _: Throwable => Left(JsValueViolation)
    })

  implicit val UnitType = ScalarAlias[Unit, String](StringType,
    toScalar = _ => "",
    fromScalar = _ => Right(()))

  case object UuidViolation extends ValueCoercionViolation("Invalid UUID")

  implicit val UuidType = ScalarAlias[UUID, String](StringType,
    toScalar = _.toString,
    fromScalar = idString => try Right(UUID.fromString(idString)) catch {
      case _: IllegalArgumentException => Left(UuidViolation)
    })

  case object LocalDateTimeViolation extends ValueCoercionViolation("Invalid date")

  implicit val LocalDateTimeType = ScalarAlias[LocalDateTime, String](StringType,
    toScalar = _.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    fromScalar = dateString => try Right(LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)) catch {
      case _: DateTimeParseException => Left(LocalDateTimeViolation)
    })

  case object ShortViolation extends ValueCoercionViolation("Invalid short")

  implicit val ShortType = ScalarAlias[Short, Int](IntType,
    toScalar = _.toInt,
    fromScalar = intValue => try Right(intValue.toShort) catch {
      case _: IllegalArgumentException => Left(ShortViolation)
    })

  case object ByteViolation extends ValueCoercionViolation("Invalid byte")

  implicit val ByteType = ScalarAlias[Byte, Int](IntType,
    toScalar = _.toInt,
    fromScalar = intValue => try Right(intValue.toByte) catch {
      case _: IllegalArgumentException => Left(ByteViolation)
    })

  case object FloatViolation extends ValueCoercionViolation("Invalid float")

  implicit val SmallFloatType = ScalarAlias[Float, Double](FloatType,
    toScalar = _.toDouble,
    fromScalar = doubleValue => try Right(doubleValue.toFloat) catch {
      case _: IllegalArgumentException => Left(FloatViolation)
    })
}
