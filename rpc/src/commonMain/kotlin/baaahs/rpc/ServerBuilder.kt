package baaahs.rpc

public class ServerBuilder {
    private val services = mutableMapOf<String, RpcHandler<*>>()

    public fun <T : Any> registerServiceHandler(path: String, service: RpcHandler<T>) {
        if (services.containsKey(path)) {
            throw IllegalArgumentException("Service already registered for path: $path")
        }
        services[path] = service
    }

    public fun start() {

    }
}