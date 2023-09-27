package org.jetbrains.bsp.generators

import software.amazon.smithy.model.Model

object Loader {
    fun readResource(name: String): String {
        try {
            return Loader::class.java.classLoader.getResourceAsStream(name)!!.bufferedReader().use { it.readText() }
        } catch (e: Throwable) {
            throw RuntimeException(
                "Failed to read resource $name",
                e
            )
        }
    }

    private val extensionRegex = Regex("bsp/extensions/(.+)\\.smithy")

    val extensions: List<String> = readResource("META-INF/smithy/manifest").lines().mapNotNull {
        extensionRegex.matchEntire(it)?.groupValues?.get(1)
    }

    val namespaces: List<String> = run {
        listOf("bsp") + extensions.map { "bsp.$it" }
    }

    val model: Model = Model.assembler().discoverModels().assemble().unwrap()

    val protocolVersion: String = readResource("META-INF/smithy/bsp/version").trim()
}
