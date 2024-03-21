package baaahs.rpc

import baaahs.util.IndentingWriter
import com.google.devtools.ksp.symbol.*

class ClassProcessor(
    classDeclaration: KSClassDeclaration,
    private val out: IndentingWriter
) {
    private val packageName = classDeclaration.packageName.asString()
    private val baseName = classDeclaration.simpleName.getShortName()
    private val typeParameters = classDeclaration.typeParameters.map { RpcTypeParam(it) }
    private val typeParams = typeParameters
        .joinToString(", ") { it.name }
        .wrapIfNotEmpty { "<$it>" }
    private val fullName = "$baseName$typeParams"
    private val rpcMethods = run {
        val rpcMethodOverloads = mutableMapOf<String, Int>()

        classDeclaration
            .getAllFunctions()
            .filter { !ignoredMethods.contains(it.simpleName.asString()) && it.isAbstract }
            .map { fn ->
                val name = fn.simpleName.asString()
                val overloadNumber = rpcMethodOverloads[name] ?: 0
                rpcMethodOverloads[name] = overloadNumber + 1
                RpcMethod(fn, overloadNumber)
            }
    }.toList()

    fun process() {
        out.appendLine("package $packageName")
        out.appendLine()
        out.appendLine("import kotlinx.serialization.KSerializer")
        out.appendLine("import kotlinx.serialization.builtins.serializer")
        out.appendLine("import kotlinx.serialization.modules.SerializersModule")
        out.appendLine()

        out.appendLine("/** RPC implementation for [${baseName}]. */")
        out.appendLine("class ${baseName}Rpc$typeParams(")
        emitConstructorParams()
        out.appendLine(") : ${RpcImpl::class.qualifiedName!!}<$fullName>, ${RpcService::class.qualifiedName!!}<$fullName> {")
        out.indent {
            out.appendLine("override fun createSender(endpoint: $rpcEndpointName) =")
            out.indent { out.appendLine("Sender(endpoint as $rpcClientName)") }
            out.appendLine()
            out.appendLine("override fun createReceiver(handler: $fullName) {")
            out.indent { out.appendLine("Receiver(handler)") }
            out.appendLine("}")
            out.appendLine()

            rpcMethods.forEach { rpcMethod ->
                rpcMethod.emitCommandPortVar(fullName, out)
            }

            out.appendLine("override public val commands: List<$commandPortName<$fullName, *, *>> = listOf(")
            out.indent {
                rpcMethods.forEach { rpcMethod ->
                    out.appendLine("${rpcMethod.fnName}Command,")
                }
            }
            out.appendLine(")\n")

            rpcMethods.forEach { rpcMethod ->
                rpcMethod.emitSerializationClass(out)
            }

            out.appendLine("inner class Sender(endpoint: $rpcClientName) : $fullName {")
            out.indent {
                rpcMethods.forEach { rpcMethod ->
                    rpcMethod.emitSenderFunction(out)
                }
            }
            out.appendLine("}")

            out.appendLine()

            out.appendLine("inner class Receiver(handler: $fullName) {")
            out.indent {
                out.appendLine("init {")
                out.indent {
                    rpcMethods.forEach { rpcMethod ->
                        rpcMethod.emitListenCommand(out)
                    }
                }
                out.appendLine("}")
            }
            out.appendLine("}")
        }
        out.appendLine("}")

        out.appendLine()

        emitGetImpl()
    }

    private fun emitGetImpl() {
        out.appendLine("fun ${typeParams.wrapIfNotEmpty { "$it " }}$baseName.Companion.getImpl(")
        emitConstructorParams()
        out.appendLine(") = ${baseName}Rpc(${constructorArgNames()})")
    }

    private fun constructorArgNames() = buildList {
        add("channelPrefix")
        typeParameters.forEach { add("${it.lowerName}Serializer") }
        add("serialModule")
    }.joinToString(", ")

    private fun emitConstructorParams() {
        out.indent {
            out.appendLine("channelPrefix: String,")
            typeParameters.forEach {
                out.appendLine("${it.lowerName}Serializer: KSerializer<${it.name}>,")
            }
            out.appendLine("serialModule: SerializersModule = SerializersModule {}")
        }
    }

    class RpcTypeParam(param: KSTypeParameter) {
        val name = param.simpleName.asString()
        val lowerName = name.lowercase()
    }

    class RpcMethod(fn: KSFunctionDeclaration, overloadNumber: Int) {
        private val fnNameOverloadable = fn.simpleName.getShortName()
        val fnName = fnNameOverloadable + if (overloadNumber > 0) overloadNumber else ""
        private val className = fnName[0].uppercase() + fnName.substring(1)
        private val params = fn.parameters.map { RpcParam(it) }
        private val genericParams = params.filter { it.type.isParameterized }
        private val returnType = RpcType(fn.returnType!!.resolve())
        private val nonUnitReturn = returnType.fullName != "kotlin.Unit"
        private val commandClassName = "${className}Command"
        private val maybeTypeParams = params
            .filter { it.type.isParameterized }
            .joinToString(", ") { it.type.name }
            .wrapIfNotEmpty { "<$it>" }
        private val responseClassName = if (nonUnitReturn) "${className}Response" else "kotlin.Unit"

        fun emitCommandPortVar(fullName: String, out: IndentingWriter) {
            out.append("private val ${fnName}Command: $commandPortName<$fullName, $commandClassName$maybeTypeParams, $responseClassName> = ")
            emitCommandPort(out)
            out.appendLine("\n")
        }

        private fun emitCommandPort(out: IndentingWriter) {
            out.appendLine("$commandPortName(")
            out.indent {
                out.appendLine("\"\$channelPrefix/$fnName\",")
                val serializers = genericParams
                    .joinToString(", ") { "${it.type.name.lowercase()}Serializer" }
                out.appendLine("$commandClassName.serializer(${serializers}),")
                out.appendLine("$responseClassName.serializer(),")
                out.append("{ ")
                emitCommandInvocation("", out)
                out.appendLine(" },")
                out.appendLine("serialModule")
            }
            out.append(")")
        }

        fun emitSerializationClass(out: IndentingWriter) {
            out.appendLine("@kotlinx.serialization.Serializable")

            out.append("private class $commandClassName$maybeTypeParams(")
            if (params.isNotEmpty()) {
                out.indent {
                    out.append(params.joinToString(",") { rpcParam ->
                        "\nval ${rpcParam.name}: ${rpcParam.type.fullName}"
                    })
                }
                out.appendLine()
            }
            out.appendLine(")")
            out.appendLine()

            if (nonUnitReturn) {
                out.appendLine("@kotlinx.serialization.Serializable")
                out.appendLine("private class ${className}Response(")
                out.indent {
                    out.appendLine("val value: ${returnType.fullName}")
                }
                out.appendLine(")")
                out.appendLine()
            }
        }

        fun emitSenderFunction(out: IndentingWriter) {
            out.appendLine(
                "override suspend fun $fnNameOverloadable(${
                    params.joinToString(", ") { rpcParam ->
                        "${rpcParam.name}: ${rpcParam.type.fullName}"
                    }
                }) =")
            out.indent {
                out.appendLine(
                    "${fnName}Command.transmit($commandClassName(${params.joinToString(", ") { it.name }}))" +
                            if (nonUnitReturn) ".value" else ""
                )
            }
        }

        fun emitListenCommand(out: IndentingWriter) {
            out.appendLine("endpoint.listenOnCommandChannel(${fnName}Command) {")
            out.indent {
                emitCommandInvocation("handler.", out)
                out.appendLine()
            }
            out.appendLine("}")
        }

        private fun emitCommandInvocation(context: String = "", out: IndentingWriter) {
            val paramList = params.joinToString(", ") { "it.${it.name}" }
            val invoke = "$context$fnNameOverloadable($paramList)"
            if (nonUnitReturn) {
                out.append("${className}Response(")
                out.append(invoke)
                out.append(")")
            } else {
                out.append(invoke)
            }
        }

        inner class RpcParam(param: KSValueParameter) {
            val name = param.name!!.asString()
            val type = RpcType(param.type.resolve())
        }
    }

    class RpcType(type: KSType) {
        val isParameterized = type.declaration is KSTypeParameter
        val name = if (isParameterized) {
            type.declaration.simpleName.asString()
        } else {
            type.declaration.qualifiedName?.asString() ?: "!!!"
        }
        private val types: String = type.arguments
            .joinToString(", ") { RpcType(it.type!!.resolve()).fullName }
            .wrapIfNotEmpty { "<$it>" }
        private val nullable = if (type.isMarkedNullable) "?" else ""
        val fullName: String = "$name$types$nullable"
    }

    companion object {
        private val ignoredMethods = arrayOf("equals", "hashCode", "toString")
        private val commandPortName = CommandPort::class.qualifiedName!!
        private val rpcEndpointName = RpcEndpoint::class.qualifiedName!!
        private val rpcClientName = RpcClient::class.qualifiedName!!
    }
}

private fun String.wrapIfNotEmpty(wrapper: (String) -> String): String =
    if (isNotEmpty()) wrapper(this) else this
