package org.jetbrains.bsp.generators.ir

/**
 * This configuration allows to specify preferences for how the smithy model is represented internally.
 * This enhances generating libraries that fully utilize the particular language's features.
 *
 * In the examples below, we assume that the smithy model contains the following shape:
 *  ```
 *  structure StructName {
 *      value: TypeName
 *  }
 * ```
 */
class IrConfig(
    /**
     * - Smithy:
     *      ```
     *      string TypeName
     *      ```
     * - Pure:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(name="value", type=Type.String, required=false, hints=[])
     *          ],
     *          hints = []
     *      )
     *      ```
     * - Aliased:
     *      ```
     *      Def.Alias(
     *          shapeId = bsp#Identifier,
     *          aliasedType = Type.String,
     *          hints = []
     *      )
     *      ```
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.Ref(shapeId=bsp#TypeName),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     */
    val strings: TypeAliasing,
    /**
     * - Smithy:
     *      ```
     *      map TypeName {
     *          key: Int
     *          value: String
     *      }
     *      ```
     * - Pure:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.Map(key=Type.String, value=Type.String),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     * - Aliased:
     *      ```
     *      Def.Alias(
     *          shapeId = bsp#Identifier,
     *          aliasedType = Type.Map(key=Type.String, value=Type.String),
     *          hints = []
     *      )
     *      ```
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.Ref(shapeId=bsp#TypeName),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     */
    val maps: TypeAliasing,
    /**
     * - Smithy:
     *      ```
     *      @data
     *      document TypeName
     *      ```
     *      ```
     *      @dataKind(kind: "some-type-name", extends: [TypeName])
     *      structure SomeTypeName {}
     *      ```
     * - Both:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#SomeTypeName,
     *          fields = [],
     *          hints = []
     *      )
     *      ```
     *      ```
     *      Def.DataKinds(
     *          shapeId = bsp#TypeName,
     *          kindsEnumId = bsp#TypeNameKind,
     *          kinds = [
     *              PolymorphicDataKind(
     *                  kind = "some-type-name",
     *                  shape = Type.Ref(shapeId=bsp#SomeTypeName),
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     * - AsType:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(name="dataKind", type=Type.String, required=false, hints=[]),
     *              Field(name="value", type=Type.Json, required=false, hints=[])
     *          ],
     *          hints = []
     *      )
     *      ```
     * - AsDef:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.Ref(shapeId=bsp#TypeName),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     */
    val dataWithKind: AbstractionLevel,
    /**
     * - Smithy:
     *      ```
     *      @enumKind("open")
     *      intEnum TypeName {
     *          OPTION = 1
     *      }
     *      ```
     * - Both:
     *      ```
     *      Def.OpenEnum(
     *          shapeId = bsp#TypeName,
     *          enumType = EnumType.IntEnum,
     *          values = [
     *              EnumValue(name="OPTION", value=1, hints=[])
     *          ],
     *          hints = []
     *      )
     *      ```
     * - AsType:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(name="value", type=Type.Int, required=false, hints=[])
     *          ],
     *          hints = []
     *      )
     *      ```
     * - AsDef:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.Ref(shapeId=bsp#TypeName),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     */
    val openEnums: AbstractionLevel,
    /**
     * - Smithy:
     *      ```
     *      @untaggedUnion
     *      union TypeName {
     *          string: String
     *          integer: Integer
     *      }
     *      ```
     * - AsType:
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.UntaggedUnion(members=[Type.String, Type.Int]),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     * - AsDef:
     *      ```
     *      Def.UntaggedUnion(
     *          shapeId = bsp#TypeName,
     *          members = [Type.String, Type.Int],
     *          hints = []
     *      )
     *      ```
     *      ```
     *      Def.Structure(
     *          shapeId = bsp#StructName,
     *          fields = [
     *              Field(
     *                  name = "value",
     *                  type = Type.Ref(shapeId=bsp#TypeName),
     *                  required = false,
     *                  hints = []
     *              )
     *          ],
     *          hints = []
     *      )
     *      ```
     */
    val untaggedUnions: AbstractionLevel,
)

/**
 * It specifies how to represent a particular type, if it should be aliased or not.
 *
 * Motivation: 2 ways of using strings in kotlin
 * - Pure:
 *      ```
 *      val field: String
 *      ```
 * - Aliased:
 *      ```
 *      typealias AliasedStringName = String
 *      ```
 *      ```
 *      val field: AliasedStringName
 *      ```
 */
enum class TypeAliasing {
    Pure,
    Aliased,
}

/**
 * It specifies if we would like to treat a particular shape as a type or as a top level definition.
 *
 * Motivation: representing UntaggedUnion
 * - AsType: (scala)
 *      ```
 *      val field: Either[String, Int]
 *      ```
 * - AsDef: (rust)
 *      ```
 *      pub enum EitherTypeName {
 *          String(String),
 *          I32(i32),
 *      }
 *       ```
 *      ```
 *      val field: EitherTypeName
 *      ```
 */
enum class AbstractionLevel {
    AsDef,
    AsType,
}
