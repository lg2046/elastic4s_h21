package com.sksamuel.elastic4s

trait ElasticImplicits {
  implicit class RichString(str: String) {
    def /(`type`: String): IndexAndType = IndexAndType(str, `type`)
  }
}

object ElasticImplicits extends ElasticImplicits
