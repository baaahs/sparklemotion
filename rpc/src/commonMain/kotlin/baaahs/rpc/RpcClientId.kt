package baaahs.rpc

import kotlin.coroutines.CoroutineContext

public class RpcClientId(public val id: String) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    public companion object Key : CoroutineContext.Key<RpcClientId>
}