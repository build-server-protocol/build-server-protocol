package org.jetbrains.bsp.util

import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonValueCodec, JsonWriter}

object CustomCodec {
  val forEitherStringInt: JsonValueCodec[Either[String, Int]] =
    new JsonValueCodec[Either[String, Int]] {
      val nullValue: Either[String, Int] = null

      def decodeValue(in: JsonReader, default: Either[String, Int]): Either[String, Int] = {
        val t = in.nextToken()
        in.rollbackToken()
        if (t == '"') Left(in.readString(null))
        else Right(in.readInt())
      }

      def encodeValue(x: Either[String, Int], out: JsonWriter): Unit = x match {
        case Right(i) => out.writeVal(i)
        case Left(s)  => out.writeVal(s)
      }
    }
}
