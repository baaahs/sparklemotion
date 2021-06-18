package baaahs.mapper

import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

private val FixtureConfigurer = xComponent<FixtureConfigurerProps>("FixtureConfigurer") { props ->

    div {
    }
}

external interface FixtureConfigurerProps : RProps {
}

fun RBuilder.fixtureConfigurer(handler: RHandler<FixtureConfigurerProps>) =
    child(FixtureConfigurer, handler = handler)
