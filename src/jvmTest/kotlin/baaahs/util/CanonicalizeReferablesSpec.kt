@file:ContextualSerialization(Address::class, Item::class)

package baaahs.util

import baaahs.camelize
import baaahs.util.CanonicalizeReferables.ReferenceType
import describe
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import kotlin.test.expect

object CanonicalizeReferablesSpec : Spek({
    describe<CanonicalizeReferables<*>> {
        val testData by value {
            val smallPants = Item("Pants", "S")
            val mediumPants = Item("Pants", "M")
            val socks = Item("Socks", "M")

            UserOrders(
                listOf(
                    Order(Address("94114"), listOf(OrderItem(smallPants, 1))),
                    Order(Address("50421"), listOf(OrderItem(smallPants, 2))),
                    Order(Address("94114"), listOf(OrderItem(socks, 3))),
                    Order(Address("94114"), listOf(OrderItem(mediumPants, 3)))
                )
            )
        }

        val canonicalizer by value {
            CanonicalizeReferables(
                UserOrders.serializer(), "test", listOf(
                    ReferenceType("addresses", Address::class, Address.serializer()),
                    ReferenceType("items", Item::class, Item.serializer())
                )
            )
        }

        val jsonx by value { Json(JsonConfiguration.Stable.copy(prettyPrint = true)) }
        val stringified by value { jsonx.stringify(canonicalizer, testData) }
        val roundTripJson by value { Json(JsonConfiguration.Stable).parseJson(stringified) }
        val roundTripObject by value { jsonx.parse(canonicalizer, stringified) }

        it("retains JsonConfiguration") {
            expect(true, stringified) { stringified.contains("\n    \"items\": {\n") }
        }

        it("replaces referable instances with an reference") {
            val expectedJson = json {
                "orders" to jsonArray {
                    +json {
                        "address" to "0"
                        "items" to jsonArray {
                            +json { "item" to "pants"; "count" to 1 }
                        }
                    }
                    +json {
                        "address" to "1"
                        "items" to jsonArray {
                            +json { "item" to "pants"; "count" to 2 }
                        }
                    }
                    +json {
                        "address" to "0"
                        "items" to jsonArray {
                            +json { "item" to "socks"; "count" to 3 }
                        }
                    }
                    +json {
                        "address" to "0"
                        "items" to jsonArray {
                            +json { "item" to "pants1"; "count" to 3 }
                        }
                    }
                }
                "addresses" to json {
                    "0" to json { "zip" to "94114" }
                    "1" to json { "zip" to "50421" }
                }
                "items" to json {
                    "pants" to json { "name" to "Pants"; "size" to "S" }
                    "socks" to json { "name" to "Socks"; "size" to "M" }
                    "pants1" to json { "name" to "Pants"; "size" to "M" }
                }

            }

            expect(expectedJson as JsonElement) { roundTripJson }
        }

        it("deserializes to equivalent objects") {
            expect(testData) { roundTripObject }
        }
    }
})

@Serializable
private data class UserOrders(val orders: List<Order>)

@Serializable
private data class Order(val address: Address, val items: List<OrderItem>)

@Serializable
private data class Address(val zip: String) : Referable

@Serializable
private data class OrderItem(val item: Item, val count: Int)

@Serializable
private data class Item(val name: String, val size: String) : Referable {
    override fun suggestId(): String? = name.camelize()
}
