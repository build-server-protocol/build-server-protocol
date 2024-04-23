package bsp.codegen.common.ir

import software.amazon.smithy.model.shapes.ShapeId

/**
 * This configuration allows to specify preferences for how the smithy model is represented
 * internally. This enhances generating libraries that fully utilize the particular language's
 * features.
 *
 * In the examples below, we assume that the smithy model contains the following shape:
 *
 *  ```
 *  structure StructName {
 *      value: TypeName
 *  }
 * ```
 */
class IrConfig(
    // ktfmt: off
    /**
     * - Smithy: ``` string TypeName ```
     * - Pure: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field(name="value", type=Type.String, required=false, hints=[]) ], hints = [] ) ```
     * - Aliased: ``` Def.Alias( shapeId = bsp#Identifier, aliasedType = Type.String, hints = []
     *   ) ``` ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.Ref(shapeId=bsp#TypeName), required = false, hints = []
     *   ) ], hints = [] ) ```
     */
    // ktfmt: on
    val strings: TypeAliasing,
    // ktfmt: off
    /**
     * - Smithy: ``` map TypeName { key: Int value: String } ```
     * - Pure: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.Map(key=Type.String, value=Type.String), required = false, hints = []
     *   ) ], hints = [] ) ```
     * - Aliased: ``` Def.Alias( shapeId = bsp#Identifier, aliasedType = Type.Map(key=Type.String,
     *   value=Type.String), hints = [] ) ``` ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.Ref(shapeId=bsp#TypeName), required = false, hints = []
     *   ) ], hints = [] ) ```
     */
    // ktfmt: on
    val maps: TypeAliasing,
    // ktfmt: off
    /**
     * - Smithy: ```
     *
     *     @data document TypeName ``` ``` @dataKind(kind: "some-type-name", extends: [TypeName])
     *       structure SomeTypeName {} ```
     * - Both: ``` Def.Structure( shapeId = bsp#SomeTypeName, fields = [], hints = [] ) ``` ```
     *   Def.DataKinds( shapeId = bsp#TypeName, kindsEnumId = bsp#TypeNameKind, kinds =
     *   [ PolymorphicDataKind( kind = "some-type-name", shape = Type.Ref(shapeId=bsp#SomeTypeName), hints = []
     *   ) ], hints = [] ) ```
     * - AsType: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field(name="dataKind", type=Type.String, required=false, hints=[]), Field(name="value",
     *   type=Type.Json, required=false, hints=[]) ], hints = [] ) ```
     * - AsDef: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.Ref(shapeId=bsp#TypeName), required = false, hints = []
     *   ) ], hints = [] ) ```
     */
    // ktfmt: on
    val dataWithKind: AbstractionLevel,
    // ktfmt: off
    /**
     * - Smithy: ``` @enumKind("open") intEnum TypeName { OPTION = 1 } ```
     * - Both: ``` Def.OpenEnum( shapeId = bsp#TypeName, enumType = EnumType.IntEnum, values =
     *   [ EnumValue(name="OPTION", value=1, hints=[]) ], hints = [] ) ```
     * - AsType: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field(name="value", type=Type.Int, required=false, hints=[]) ], hints = [] ) ```
     * - AsDef: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.Ref(shapeId=bsp#TypeName), required = false, hints = []
     *   ) ], hints = [] ) ```
     */
    // ktfmt: on
    val openEnums: AbstractionLevel,
    // ktfmt: off
    /**
     * - Smithy: ``` @untaggedUnion union TypeName { string: String integer: Integer } ```
     * - AsType: ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.UntaggedUnion(members=[Type.String, Type.Int]),
     *   required = false, hints = [] ) ], hints = [] ) ```
     * - AsDef: ``` Def.UntaggedUnion( shapeId = bsp#TypeName, members = [Type.String, Type.Int],
     *   hints = [] ) ``` ``` Def.Structure( shapeId = bsp#StructName, fields =
     *   [ Field( name = "value", type = Type.Ref(shapeId=bsp#TypeName), required = false, hints = []
     *   ) ], hints = [] ) ```
     */
    // ktfmt: on
    val untaggedUnions: AbstractionLevel,
)

// ktfmt: off
/**
 * It specifies how to represent a particular type, if it should be aliased or not.
 *
 * Motivation: 2 ways of using strings in kotlin
 * - Pure: ``` val field: String ```
 * - Aliased: ``` typealias AliasedStringName = String ``` ``` val field: AliasedStringName ```
 */
// ktfmt: on
data class TypeAliasing(val aliased: (ShapeId) -> Boolean) {
  companion object {
    val Pure: TypeAliasing = TypeAliasing { false }
    val Aliased: TypeAliasing = TypeAliasing { it.namespace.startsWith("bsp") }
  }
}

// ktfmt: off
/**
 * It specifies if we would like to treat a particular shape as a type or as a top level definition.
 *
 * Motivation: representing UntaggedUnion
 * - AsType: (scala) ``` val field: Either[String, Int] ```
 * - AsDef: (rust) ``` pub enum EitherTypeName { String(String), I32(i32), } ``` ``` val field:
 *   EitherTypeName ```
 */
// ktfmt: on
enum class AbstractionLevel {
  AsDef,
  AsType,
}
