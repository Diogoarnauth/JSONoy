package pt.isel

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

fun Any.fieldsToJson(): String {
    val jsonPairs = this::class.memberProperties.map { properties ->
        propToJson(properties, this)
    }.filter { it.isNotEmpty() }

    return "{${jsonPairs.joinToString(",")}}"
}

private fun propToJson(properties: KProperty<*>, obj: Any): String {
    val propValue = properties.getter.call(obj) ?: return """"${properties.name}":null"""

    return when (propValue) {
        is String -> """"${properties.name}":"$propValue""""
        is Number, is Boolean -> """"${properties.name}":$propValue"""
        is List<*> -> """"${properties.name}":[${propValue.joinToString(",") { it?.fieldsToJson() ?: "null" }}]"""
        else -> """"${properties.name}":${propValue.fieldsToJson()}"""
    }
}
