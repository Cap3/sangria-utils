package de.cap3.sangria

import de.cap3.sangria.AbstractSangriaController.{AbstractContextFactory, GraphQlParams}
import play.api.libs.Files
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc._
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.renderer.SchemaRenderer
import sangria.schema.Schema

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AbstractSangriaController {

  trait AbstractContextFactory[Ctx] {
    def create(eventualToken: Option[String] = None,
               files: Seq[MultipartFormData.FilePart[Files.TemporaryFile]] = Seq()): Ctx
  }

  case class GraphQlParams
  (
    query: String,
    variables: Option[JsObject],
    operation: Option[String]
  )

}

abstract class AbstractSangriaController[Ctx](cc: ControllerComponents,
                                              contextFactory: AbstractContextFactory[Ctx],
                                              graphQlConfig: SangriaConfig)
                                             (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  protected val schema: Schema[Ctx, Unit]
  protected val middlewares: List[Middleware[Ctx]] = Nil

  def renderSchemaAction: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    if (graphQlConfig.showSchema) {
      Ok(SchemaRenderer.renderSchema(schema))
    } else {
      MethodNotAllowed("")
    }
  }

  def executeQueryAction: Action[AnyContent] = Action.async { request ⇒
    val multipartFormDataBody = request.body.asMultipartFormData
    val files = multipartFormDataBody.map(_.files).getOrElse(Seq())
    val json = (multipartFormDataBody match {
      case Some(multipartBody) =>
        multipartBody
          .asFormUrlEncoded.get("operations")
          .flatMap(_.headOption.map(Json.parse))
      case None => request.body.asJson
    }).getOrElse(new JsObject(Map()))


    val authToken = request.headers.get(graphQlConfig.authTokenHeader)

    extractVariables(json) match {
      case GraphQlParams(query, variables, operation) =>
        executeQuery(authToken, files)(query, variables, operation)
    }
  }

  protected def executeQuery(token: Option[String],
                             files: Seq[MultipartFormData.FilePart[Files.TemporaryFile]] = Seq()): (String, Option[JsObject], Option[String]) => Future[Result] =
    executeQuery(schema, contextFactory.create(token, files), _, _, _)

  protected def executeQuery(schema: Schema[Ctx, Unit],
                             context: Ctx,
                             query: String,
                             variables: Option[JsObject],
                             operation: Option[String]): Future[Result] =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        Executor.execute(
          schema = schema,
          queryAst = queryAst,
          userContext = context,
          operationName = operation,
          exceptionHandler = ExceptionHandler.empty,
          variables = variables getOrElse Json.obj(),
          middleware = middlewares
        ).map { result =>
          Ok(result)
        }.recover {
          case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
          case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
        }

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) ⇒
        Future.successful(BadRequest(Json.obj(
          "syntaxError" → error.getMessage,
          "locations" → Json.arr(Json.obj(
            "line" → error.originalError.position.line,
            "column" → error.originalError.position.column)))))

      case Failure(error) ⇒
        throw error
    }

  protected def extractVariables(json: JsValue): GraphQlParams = {
    val query = (json \ "query").as[String]
    val operation = (json \ "operationName").asOpt[String]

    val variables = (json \ "variables").toOption.flatMap {
      case JsString(vars) ⇒ Some(parseVariables(vars))
      case obj: JsObject ⇒ Some(obj)
      case _ ⇒ None
    }

    GraphQlParams(query, variables, operation)
  }

  protected def parseVariables(variables: String): JsObject =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]
}
