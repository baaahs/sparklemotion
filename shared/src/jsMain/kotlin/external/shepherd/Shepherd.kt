@file:JsModule("shepherd.js")
@file:JsNonModule
@file:JsQualifier("default")

package external.shepherd

import web.html.HTMLElement
import kotlin.js.Promise

/**
 * The primary Shepherd Tour class
 */
@JsName("Tour")
external class Tour(options: TourOptions = definedExternally) {
    fun addStep(stepId: String, options: StepOptions): Step
    fun addStep(options: StepOptions): Step
    fun start()
    fun complete()
    fun cancel()
    fun next()
    fun back()
}

/**
 * A Shepherd Tour step
 */
@JsName("Step")
external class Step {
    fun show()
    fun hide()
}

/**
 * Options for constructing a Tour
 */
external interface TourOptions {
    var defaultStepOptions: StepOptions?
    var useModalOverlay: Boolean?
    var exitOnEsc: Boolean?
    var keyboardNavigation: Boolean?
    // Add others as needed
}

/**
 * Options for configuring a single Step
 */
external interface StepOptions {
    var attachTo: StepOptionsAttachTo?
    var advanceOn: StepOptionsAdvanceOn?
    var arrow: Boolean?
    var beforeShowPromise: (() -> Promise<dynamic>)?
    var buttons: Array<StepOptionsButton>?
    var cancelIcon: StepOptionsCancelIcon?
    var canClickTarget: Boolean?
    var classes: String?
    var extraHighlights: Array<String>?
    var highlightClass: String?
    var id: String?
    var modalOverlayOpeningPadding: Number?
    /**
     * Either a single number or an object with up to 4 corner radii.
     */
    var modalOverlayOpeningRadius: dynamic /* Number | object */
    var modalOverlayOpeningXOffset: Number?
    var modalOverlayOpeningYOffset: Number?
    /**
     * For extra Floating UI config. Could be typed more precisely if desired.
     */
    var floatingUIOptions: dynamic
    /**
     * Can be boolean or a scroll-into-view object:
     * { behavior: "smooth", block: "center" }, etc.
     */
    var scrollTo: dynamic
    /**
     * Custom scrolling function.
     */
    var scrollToHandler: ((HTMLElement) -> Unit)?
    /**
     * Optional gating function to skip step if false.
     */
    var showOn: (() -> Boolean)?
    /**
     * The text can be string, array of strings, HTMLElement, or a function returning one of these.
     */
    var text: dynamic
    /**
     * The title can be a string or a function returning a string.
     */
    var title: dynamic
    /**
     * A map of event -> handler functions.
     */
    var `when`: StepOptionsWhen?
}

/**
 * Attach-to config: `element` can be a selector string, an HTMLElement, or a function returning one.
 */
external interface StepOptionsAttachTo {
    var element: dynamic /* HTMLElement | String | (() -> HTMLElement | String | null) */
    var on: String?
}

/** Advance-on config */
external interface StepOptionsAdvanceOn {
    var event: String
    var selector: String
}

/** Button config for the step */
external interface StepOptionsButton {
    var action: (() -> Unit)?
    var classes: String?
    var disabled: dynamic /* Boolean or () -> Boolean */
    var label: dynamic /* String or () -> String */
    var secondary: Boolean?
    var text: dynamic /* String or () -> String */
}

/** Cancel icon config */
external interface StepOptionsCancelIcon {
    var enabled: Boolean?
    var label: String?
}

/**
 * Key/value mapping of events -> handler functions.
 * Using @nativeGetter/@nativeSetter allows arbitrary keys.
 */
external interface StepOptionsWhen {
    @nativeGetter
    operator fun get(event: String): ((Step) -> Unit)?
    @nativeSetter
    operator fun set(event: String, handler: ((Step) -> Unit)?)
}
