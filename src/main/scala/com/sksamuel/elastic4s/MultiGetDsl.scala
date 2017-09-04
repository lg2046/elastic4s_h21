package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributePreference, DefinitionAttributeRefresh}
import org.elasticsearch.action.get.{GetResponse, MultiGetAction, MultiGetItemResponse, MultiGetRequest, MultiGetRequestBuilder, MultiGetResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait MultiGetDsl extends GetDsl {

  implicit object MultiGetDefinitionExecutable
    extends Executable[MultiGetDefinition, MultiGetResponse, MultiGetResult] {
    override def apply(c: Client, t: MultiGetDefinition): Future[MultiGetResult] = {
      injectFutureAndMap(c.multiGet(t.build, _))(MultiGetResult.apply)
    }
  }
}

case class MultiGetResult(original: MultiGetResponse) {

  import scala.collection.JavaConverters._

  @deprecated("use .responses for a scala friendly Seq, or use .original to access the java result", "2.0")
  def getResponses() = original.getResponses

  def responses: Seq[MultiGetItemResult] = original.iterator.asScala.map(MultiGetItemResult.apply).toList
}

case class MultiGetItemResult(original: MultiGetItemResponse) {

  @deprecated("use failure for a scala friendly Option, or use .original to access the java result", "2.0")
  def getFailure = original.getFailure

  @deprecated("use response for a scala friendly Option, or use .original to access the java result", "2.0")
  def getResponse = original.getResponse

  def getId = original.getId
  def getIndex = original.getIndex

  def getType = original.getType
  def isFailed = original.isFailed

  def failure: Option[MultiGetResponse.Failure] = Option(original.getFailure)
  def id = original.getId
  def index = original.getIndex
  def response: Option[GetResponse] = Option(original.getResponse)
  def `type`: String = original.getType
  def failed: Boolean = original.isFailed
}

case class MultiGetDefinition(gets: Iterable[GetDefinition])
  extends DefinitionAttributePreference
    with DefinitionAttributeRefresh {

  val _builder = new MultiGetRequestBuilder(ProxyClients.client, MultiGetAction.INSTANCE)

  gets foreach { get =>
    val item = new MultiGetRequest.Item(get.indexTypes.index, get.indexTypes.types.headOption.orNull, get.id)
    item.routing(get.build.routing)
    item.fields(get.build.fields: _*)
    item.version(get.build.version)
    _builder.add(item)
  }

  def build: MultiGetRequest = _builder.request()

  def realtime(realtime: Boolean): this.type = {
    _builder.setRealtime(realtime)
    this
  }
}
