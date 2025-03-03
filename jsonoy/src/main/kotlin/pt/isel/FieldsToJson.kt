package pt.isel

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

fun Any.fieldsToJson(): String {
    val jsonPairs = this::class.memberProperties.map { prop ->
        propToJson(prop, this)
    }.filter { it.isNotEmpty() }

    return "{${jsonPairs.joinToString(",")}}"
}

private fun propToJson(prop: KProperty<*>, target: Any): String {
    val propValue = prop.getter.call(target) ?: return """"${prop.name}":null"""

    return when (propValue) {
        is String -> """"${prop.name}":"$propValue""""
        is Number, is Boolean -> """"${prop.name}":$propValue"""
        is List<*> -> """"${prop.name}":[${propValue.joinToString(",") { it?.fieldsToJson() ?: "null" }}]"""
        else -> """"${prop.name}":${propValue.fieldsToJson()}"""
    }
}
