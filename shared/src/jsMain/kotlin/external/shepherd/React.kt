package external.shepherd

import react.createContext
import react.useContext

// 1) Define the context data
external interface ShepherdContextType {
    var Shepherd: dynamic  // or refine with external object references
}

// 2) Create the actual context
val ShepherdJourneyContext = createContext<ShepherdContextType?>(null)

// 3) useShepherd hook (like useShepherd in TypeScript)
fun useShepherd(): dynamic {
    val context = useContext(ShepherdJourneyContext)
    if (context == null) {
        error("useShepherd must be used within a ShepherdJourneyProvider")
    }
    return context.Shepherd
}

// 4) ShepherdJourneyProvider component
//val ShepherdJourneyProvider = FC<PropsWithChildren> { props ->
//    // Provide the Shepherd library instance
//    ShepherdJourneyContext.Provider {
//        value = object : ShepherdContextType {
//            val Shepherd: dynamic = external.shepherd.Shepherd
//        }
//        +props.children
//    }
//}
