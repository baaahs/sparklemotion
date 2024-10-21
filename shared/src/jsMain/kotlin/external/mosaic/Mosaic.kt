package external.mosaic

import react.Component
import react.RBuilder
import react.RHandler
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
fun <T> RBuilder.mosaic(handler: RHandler<MosaicControlledProps<T>>) =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>, handler)

@Suppress("UNCHECKED_CAST")
fun <T> RBuilder.mosaicWindow(handler: RHandler<MosaicWindowProps<T>>) =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>, handler)

@Suppress("UNCHECKED_CAST")
fun <T> RBuilder.mosaicZeroState(handler: RHandler<MosaicZeroStateProps<T>>) =
    child(MosaicZeroState::class as KClass<out Component<MosaicZeroStateProps<T>, *>>, handler)
