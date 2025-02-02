package enumeratum

import cats.syntax.either._
import io.circe.Decoder.Result
import io.circe.{Encoder, Decoder, Json, HCursor, DecodingFailure, KeyEncoder, KeyDecoder}

/** Created by Lloyd on 4/14/16.
  *
  * Copyright 2016
  */
object Circe {

  /** Returns an Encoder for the given enum
    */
  def encoder[A <: EnumEntry](enum: Enum[A]): Encoder[A] = new Encoder[A] {
    final def apply(a: A): Json = stringEncoder.apply(a.entryName)
  }

  def encoderLowercase[A <: EnumEntry](enum: Enum[A]): Encoder[A] =
    new Encoder[A] {
      final def apply(a: A): Json =
        stringEncoder.apply(a.entryName.toLowerCase)
    }

  def encoderUppercase[A <: EnumEntry](enum: Enum[A]): Encoder[A] =
    new Encoder[A] {
      final def apply(a: A): Json =
        stringEncoder.apply(a.entryName.toUpperCase)
    }

  /** Returns a Decoder for the given enum
    */
  def decoder[A <: EnumEntry](enum: Enum[A]): Decoder[A] = new Decoder[A] {
    final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
      val maybeMember = enum.withNameOption(s)
      maybeMember match {
        case Some(member) => Right(member)
        case _ =>
          Left(DecodingFailure(s"'$s' is not a member of enum $enum", c.history))
      }
    }
  }

  def decoderLowercaseOnly[A <: EnumEntry](enum: Enum[A]): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        val maybeMember = enum.withNameLowercaseOnlyOption(s)
        maybeMember match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $enum", c.history))
        }
      }
    }

  def decoderUppercaseOnly[A <: EnumEntry](enum: Enum[A]): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        val maybeMember = enum.withNameUppercaseOnlyOption(s)
        maybeMember match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $enum", c.history))
        }
      }
    }

  def decodeCaseInsensitive[A <: EnumEntry](enum: Enum[A]): Decoder[A] =
    new Decoder[A] {
      final def apply(c: HCursor): Result[A] = stringDecoder.apply(c).flatMap { s =>
        val maybeMember = enum.withNameInsensitiveOption(s)
        maybeMember match {
          case Some(member) => Right(member)
          case _ =>
            Left(DecodingFailure(s"'$s' is not a member of enum $enum", c.history))
        }
      }
    }

  /** Returns a KeyEncoder for the given enum
    */
  def keyEncoder[A <: EnumEntry](enum: Enum[A]): KeyEncoder[A] = KeyEncoder.instance(_.entryName)

  /** Returns a KeyDecoder for the given enum
    */
  def keyDecoder[A <: EnumEntry](enum: Enum[A]): KeyDecoder[A] =
    KeyDecoder.instance(enum.withNameOption)

  private val stringEncoder = implicitly[Encoder[String]]
  private val stringDecoder = implicitly[Decoder[String]]

}
