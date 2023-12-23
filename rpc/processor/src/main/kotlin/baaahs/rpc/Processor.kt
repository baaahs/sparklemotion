package baaahs.rpc

import baaahs.util.IndentingWriter
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class Processor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(Service::class.qualifiedName!!, false).forEach { symbol ->
            when (symbol) {
                is KSClassDeclaration -> {
                    val dependencies = Dependencies(false, symbol.containingFile!!)
                    val packageName = symbol.packageName.asString()
                    val baseName = symbol.simpleName.getShortName()
                    environment.codeGenerator.createNewFile(
                        dependencies, packageName, "${baseName}Rpc"
                    ).writer().use { out ->
                        ClassProcessor(symbol, IndentingWriter(out)).process()
                    }
                }
            }
        }
        return emptyList()
    }

    class Provider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
            return Processor(environment)
        }
    }
}
