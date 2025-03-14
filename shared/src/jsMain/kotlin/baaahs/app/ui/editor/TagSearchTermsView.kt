package baaahs.app.ui.editor

import baaahs.show.Tag
import baaahs.ui.*
import baaahs.ui.asTextNode
import mui.material.Box
import mui.material.Chip
import mui.material.ChipVariant
import react.*
import react.dom.div
import kotlin.collections.forEach
import kotlin.collections.set
import kotlin.collections.sortedBy

private val TagSearchTermsView = xComponent<TagSearchTermsProps>("TagSearchTerms") { props ->
    val byCategory = props.tags.groupBy { it.category }
    val selectedTags = memo { HashMap<Tag, Boolean>() }
    val selectedByDefault = props.selectedByDefault != false

    div {
        byCategory.entries
            .sortedBy { it.key }
            .forEach { (category, tags) ->
                Box {
                    +category
                    +": "
                    tags.sortedBy { it.value }.forEach { tag ->
                        Chip {
                            attrs.label = tag.value.asTextNode()
                            val isSelected = selectedTags[tag] ?: selectedByDefault
                            attrs.variant = if (isSelected) ChipVariant.filled else ChipVariant.outlined
                            attrs.onClick = {
                                selectedTags[tag] = !isSelected
                                this@xComponent.forceRender()
                                props.onChange(selectedTags)
                            }
                        }
                    }
                }
            }
    }
}

external interface TagSearchTermsProps : Props {
    var tags: Set<Tag>
    var selectedByDefault: Boolean?
    var onChange: (Map<Tag, Boolean>) -> Unit
}

fun RBuilder.tagSearchTerms(handler: RHandler<TagSearchTermsProps>) =
    child(TagSearchTermsView, handler = handler)