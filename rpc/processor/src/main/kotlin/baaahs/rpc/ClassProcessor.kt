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
    private val rpcMethods = classDeclaration
        .getAllFunctions()
        .filter { !ignoredMethods.contains(it.simpleName.asString()) }
        .map { fn -> RpcMethod(fn) }

    fun process() {
        out.appendLine("package $packageName")
        out.appendLine()
        out.appendLine("import kotlinx.serialization.KSerializer")
        out.appendLine("import kotlinx.serialization.builtins.serializer")
        out.appendLine("import kotlinx.serialization.modules.SerializersModule")
        out.appendLine()

        out.appendLine("/** RPC implementation for [${baseName}]. */")
        out.appendLine("class ${baseName}Rpc$typeParams(")
        out.indent {
            out.appendLine("channelPrefix: String,")
            typeParameters.forEach {
                out.appendLine("${it.lowerName}Serializer: KSerializer<${it.name}>,")
            }
            out.appendLine("serialModule: SerializersModule = SerializersModule {}")
        }
        out.appendLine(") : ${RpcImpl::class.qualifiedName!!}<$fullName> {")
        out.indent {
            out.appendLine("override fun createSender(client: $rpcClientName) =")
            out.indent { out.appendLine("Sender(client)") }
            out.appendLine()
            out.appendLine("override fun createReceiver(server: $rpcServerName, handler: $fullName) {")
            out.indent { out.appendLine("Receiver(server, handler)") }
            out.appendLine("}")
            out.appendLine()

            rpcMethods.forEach { rpcMethod ->
                rpcMethod.emitCommandPort(out)
            }

            rpcMethods.forEach { rpcMethod ->
                rpcMethod.emitSerializationClass(out)
            }

            out.appendLine("inner class Sender(client: $rpcClientName) : $fullName {")
            out.indent {
                rpcMethods.forEach { rpcMethod ->
                    rpcMethod.emitCommandSender(out)
                }
                out.appendLine()
                rpcMethods.forEach { rpcMethod ->
                    rpcMethod.emitSenderFunction(out)
                }
            }
            out.appendLine("}")

            out.appendLine()

            out.appendLine("inner class Receiver(server: $rpcServerName, handler: $fullName) {")
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
    }

    class RpcTypeParam(param: KSTypeParameter) {
        val name = param.simpleName.asString()
        val lowerName = name.lowercase()
    }

    class RpcMethod(fn: KSFunctionDeclaration) {
        private val fnName = fn.simpleName.getShortName()
        private val className = fnName[0].uppercase() + fnName.substring(1)
        private val params = fn.parameters.map { RpcParam(it) }
        private val genericParams = params.filter { it.type.isParameterized }
        private val returnType = RpcType(fn.returnType!!.resolve())

        fun emitCommandPort(out: IndentingWriter) {
            out.appendLine("private val ${fnName}Command = $commandPortName(")
            out.indent {
                out.appendLine("\"\$channelPrefix/$fnName\",")
                val serializers = genericParams
                    .joinToString(", ") { "${it.type.name.lowercase()}Serializer" }
                out.appendLine("${className}Command.serializer(${serializers}),")
                if (returnType.isParameterized) {
                    out.appendLine("${returnType.name.lowercase()}Serializer,")
                } else {
                    out.appendLine("${returnType.name}.serializer(),")
                }
                out.appendLine("serialModule")
            }
            out.appendLine(")")
            out.appendLine()
        }

        fun emitSerializationClass(out: IndentingWriter) {
            out.appendLine("@kotlinx.serialization.Serializable")
            val maybeTypeParams = params
                .filter { it.type.isParameterized }
                .joinToString(", ") { it.type.name }
                .wrapIfNotEmpty { "<$it>" }

            out.append("private class ${className}Command${maybeTypeParams}(")
            if (params.isNotEmpty()) {
                out.indent {
                    out.append(params.joinToString(",") { rpcParam ->
                        "\nval ${rpcParam.name}: ${rpcParam.type.nullableName}"
                    })
                }
                out.appendLine()
            }
            out.appendLine(")")
            out.appendLine()
        }

        fun emitCommandSender(out: IndentingWriter) {
            out.appendLine("private val ${fnName}Sender = client.commandSender(${fnName}Command)")
        }

        fun emitSenderFunction(out: IndentingWriter) {
            out.appendLine(
                "override suspend fun $fnName(${
                    params.joinToString(", ") { rpcParam ->
                        "${rpcParam.name}: ${rpcParam.type.nullableName}"
                    }
                }) =")
            out.indent {
                out.appendLine("${fnName}Sender(${className}Command(${params.joinToString(", ") { it.name }}))")
            }
        }

        fun emitListenCommand(out: IndentingWriter) {
            val paramList = params.joinToString(", ") { "it.${it.name}" }
            out.appendLine("server.listenOnCommandChannel(${fnName}Command) { handler.$fnName($paramList) }")
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
        private val nullable = if (type.isMarkedNullable) "?" else ""
        val nullableName = "$name$nullable"
    }

    companion object {
        private val ignoredMethods = arrayOf("equals", "hashCode", "toString")
        private val commandPortName = CommandPort::class.qualifiedName!!
        private val rpcClientName = RpcClient::class.qualifiedName!!
        private val rpcServerName = RpcServer::class.qualifiedName!!
    }
}

private fun String.wrapIfNotEmpty(wrapper: (String) -> String): String =
    if (isNotEmpty()) wrapper(this) else this
