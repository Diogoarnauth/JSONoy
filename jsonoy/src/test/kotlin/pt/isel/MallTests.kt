package pt.isel

import com.google.gson.Gson
import org.assertj.core.api.Assertions.assertThat
import pt.isel.model.*
import kotlin.test.Test

class MallTests {
    private val mall = buildExpectedMall()
    private val gson = Gson()

    @Test
    fun testFloorFieldsToJson() {
        val floor = Floor(2, 5)
        val json = floor.fieldsToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Floor::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(floor)
    }

    @Test
    fun testStoreFieldsToJson() {
        val json = mall.stores[0].fieldsToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Store::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(mall.stores[0])
    }

    @Test
    fun testMallFieldsToJson() {
        val json = mall.fieldsToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Mall::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(mall)
    }

    @Test
    fun testCustomerFieldsToJson() {
        val customer = Customer("Alice", 25)
        val json = customer.fieldsToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Customer::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(customer)
    }

    private fun buildExpectedMall(): Mall =
        Mall(
            id = "Mall123",
            stores =
            listOf(
                Store(
                    "Tech Store",
                    Floor(1, 10),
                    listOf(Customer("Alice", 25), Customer("Bob", 30))
                ),
                Store(
                    "Fashion Store",
                    Floor(2, 7),
                    listOf(Customer("Charlie", 22))
                )
            )
        )
}
