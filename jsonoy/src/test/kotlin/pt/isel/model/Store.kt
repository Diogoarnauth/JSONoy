package pt.isel.model

class Store(
    val name: String,
    val floor: Floor,
    val customers: List<Customer> = emptyList()
)
