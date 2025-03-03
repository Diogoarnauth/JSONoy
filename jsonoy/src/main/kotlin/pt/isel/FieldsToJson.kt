package pt.isel

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

fun Any.fieldsToJson(): String {
    val jsonPairs = this::class.memberProperties.map { field ->
        propToJson(field, this)
    }.filter { it.isNotEmpty() }

    return "{${jsonPairs.joinToString(",")}}"
}

private fun propToJson(field: KProperty<*>, obj: Any): String {
    val propValue = field.getter.call(obj) ?: return """"${field.name}":null"""

    return when (propValue) {
        is String -> """"${field.name}":"$propValue""""
        is Number, is Boolean -> """"${field.name}":$propValue"""
        is List<*> -> """"${field.name}":[${propValue.joinToString(",") { it?.fieldsToJson() ?: "null" }}]"""
        else -> """"${field.name}":${propValue.fieldsToJson()}"""
    }
}
