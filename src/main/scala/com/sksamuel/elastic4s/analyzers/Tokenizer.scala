package com.sksamuel.elastic4s.analyzers

import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

/** @author Stephen Samuel */
abstract class Tokenizer(val name: String) {
  @SuppressWarnings(Array("all"))
  def build(source: XContentBuilder): Unit = {}
  def json: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    builder.startObject()
    build(builder)
    builder.endObject()
    builder
  }
  def customized: Boolean = false
}

case object WhitespaceTokenizer extends Tokenizer("whitespace")
case object LetterTokenizer extends Tokenizer("letter")
case object LowercaseTokenizer extends Tokenizer("lowercase")
case object StandardTokenizer extends Tokenizer("standard")
case object PatternTokenizer extends Tokenizer("pattern")
case object KeywordTokenizer extends Tokenizer("keyword")
case object NGramTokenizer extends Tokenizer("nGram")
case object EdgeNGramTokenizer extends Tokenizer("edgeNGram")
case object UaxUrlEmailTokenizer extends Tokenizer("uax_url_email")

abstract class CustomizedTokenizer(override val name: String) extends Tokenizer(name) {
  override def customized: Boolean = true
}

case class UaxUrlEmailTokenizer(override val name: String,
                                maxTokenLength: Int = 255) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "uax_url_email")
    source.field("max_token_length", maxTokenLength)
  }

  def maxTokenLength(maxTokenLength: Int): UaxUrlEmailTokenizer = copy(maxTokenLength = maxTokenLength)
}

case class StandardTokenizer(override val name: String,
                             maxTokenLength: Int = 255) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
    source.field("max_token_length", maxTokenLength)
  }

  def maxTokenLength(maxTokenLength: Int): StandardTokenizer = copy(maxTokenLength = maxTokenLength)
}

case class PatternTokenizer(override val name: String,
                            pattern: String = "\\W+",
                            flags: String = "",
                            group: Int = -1) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern")
    if (flags.nonEmpty)
      source.field("flags", flags)
    source.field("pattern", pattern)
    if (group > 0) {
      source.field("group", group)
    }
  }

  def pattern(pattern: String): PatternTokenizer = copy(pattern = pattern)
  def flags(flags: String): PatternTokenizer = copy(flags = flags)
  def group(group: Int): PatternTokenizer = copy(group = group)
}

case class KeywordTokenizer(override val name: String,
                            bufferSize: Int = 256) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "keyword")
    source.field("bufferSize", bufferSize)
  }

  def bufferSize(bufferSize: Int): KeywordTokenizer = copy(bufferSize = bufferSize)
}

case class NGramTokenizer(override val name: String,
                          minGram: Int = 1,
                          maxGram: Int = 2,
                          tokenChars: Iterable[String] = Nil) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "nGram")
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
    if (tokenChars.nonEmpty)
      source.field("token_chars", tokenChars.toArray[String]: _*)
  }

  def minMaxGrams(min: Int, max: Int): NGramTokenizer = copy(minGram = min, maxGram = max)
  def tokenChars(tokenChars: Iterable[String]): NGramTokenizer = copy(tokenChars = tokenChars)
  def tokenChars(tokenChar: String, rest: String*): NGramTokenizer = copy(tokenChars = tokenChar +: rest)
}

case class EdgeNGramTokenizer(override val name: String,
                              minGram: Int = 1,
                              maxGram: Int = 2,
                              tokenChars: Iterable[String] = Nil) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "edgeNGram")
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
    if (tokenChars.nonEmpty)
      source.field("token_chars", tokenChars.toArray[String]: _*)
  }

  def minMaxGrams(min: Int, max: Int): EdgeNGramTokenizer = copy(minGram = min, maxGram = max)
  def tokenChars(tokenChars: Iterable[String]): EdgeNGramTokenizer = copy(tokenChars = tokenChars)
  def tokenChars(tokenChar: String, rest: String*): EdgeNGramTokenizer = copy(tokenChars = tokenChar +: rest)
}

case class PathHierarchyTokenizer(override val name: String,
                                  delimiter: Char = '/',
                                  replacement: Char = '/',
                                  bufferSize: Int = 1024,
                                  reverse: Boolean = false,
                                  skip: Int = 0) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "path_hierarchy")
    source.field("delimiter", delimiter.toString)
    source.field("replacement", replacement.toString)
    if (bufferSize > 1024) source.field("buffer_size", bufferSize)
    if (reverse) source.field("reverse", reverse)
    if (skip > 0) source.field("skip", skip)
  }
}
