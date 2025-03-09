package pt.isel

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation


fun Any.membersToJson(): String {
    val jsonPairs = this::class.memberProperties.map { property ->
        propToJson(property, this)
    }.filter { it.isNotEmpty() }

    val result =  "{${jsonPairs.joinToString(",")}}"
    println("ahhhhhhhhhhhhhhhhhhhhhhhhhhh $result" )
    return result
}

private fun propToJson(property: KProperty1<*, *>, obj: Any): String {
    val propValue = property.getter.call(obj) ?: return """"${property.name}":null"""


    val jsonKey = property.findAnnotation<ToJsonPropName>()?.name ?: property.name
    println("jsonKey $jsonKey")

    val formatterClass = property.findAnnotation<ToJsonFormatter>()?.formatter
    println("formatterClass $formatterClass")

    val formattedValue = formatterClass?.objectInstance?.format(propValue) ?: propValue
    println("formattedValue $formattedValue")

    return when (formattedValue) {
        is String -> """"$jsonKey":"$formattedValue""""
        is Number, is Boolean -> """"$jsonKey":$formattedValue"""
        is List<*> -> """"$jsonKey":[${formattedValue.joinToString(",") { it?.membersToJson() ?: "null" }}]"""
        else -> """"$jsonKey":${formattedValue.membersToJson()}"""
    }
}
