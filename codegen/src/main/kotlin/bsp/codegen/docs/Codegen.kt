package bsp.codegen.docs

import bsp.codegen.common.Loader
import bsp.codegen.common.ir.AbstractionLevel
import bsp.codegen.common.ir.IrConfig
import bsp.codegen.common.ir.SmithyToIr
import bsp.codegen.common.ir.TypeAliasing

object Codegen {
  // Used in the website codegen by mdoc
  @JvmStatic
  fun docs(namespace: String): String {
    val model = Loader.model
    val config =
        IrConfig(
            maps = TypeAliasing.Aliased,
            strings = TypeAliasing.Aliased,
            dataWithKind = AbstractionLevel.AsDef,
            openEnums = AbstractionLevel.AsDef,
            untaggedUnions = AbstractionLevel.AsType,
        )
    val docTree = SmithyToIr(model, config).docTree(namespace)
    return MarkdownRenderer.render(docTree)
  }
}
