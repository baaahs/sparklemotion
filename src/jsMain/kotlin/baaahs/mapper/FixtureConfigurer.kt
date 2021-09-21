package baaahs.mapper

import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val FixtureConfigurer = xComponent<FixtureConfigurerProps>("FixtureConfigurer") { props ->

    div {
    }
}

external interface FixtureConfigurerProps : Props {
}

fun RBuilder.fixtureConfigurer(handler: RHandler<FixtureConfigurerProps>) =
    child(FixtureConfigurer, handler = handler)
