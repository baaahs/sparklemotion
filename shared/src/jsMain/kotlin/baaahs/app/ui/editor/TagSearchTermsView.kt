package baaahs.app.ui.editor

import baaahs.show.Tag
import baaahs.ui.asTextNode
import baaahs.ui.xComponent
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val TagSearchTermsView = xComponent<TagSearchTermsProps>("TagSearchTerms") { props ->
    val byCategory = props.tags.groupBy { it.category }
    val selectedTags = memo { HashMap<Tag, Boolean>() }

    div {
        byCategory.entries
            .sortedBy { it.key }
            .forEach { (category, tags) ->
                Box {
                    if (category.isNotBlank() && category != "filter") {
                        +category
                        +": "
                    }

                    tags.sortedBy { it.value }.forEach { tag ->
                        Chip {
                            attrs.label = tag.value.asTextNode()
                            attrs.color = ChipColor.primary
                            attrs.size = Size.small
                            val isSelected = selectedTags[tag] == true
                            attrs.variant = if (isSelected) ChipVariant.filled else ChipVariant.outlined
                            attrs.onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags[tag] = true
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
    var onChange: (Map<Tag, Boolean>) -> Unit
}

fun RBuilder.tagSearchTerms(handler: RHandler<TagSearchTermsProps>) =
    child(TagSearchTermsView, handler = handler)