package pt.isel

import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions


fun Any.membersToJson(): String {
    val jsonPairs = this::class.memberProperties.map { property ->
        callableToJson(property, this)
    }

    val jsonFunctions = this::class.memberFunctions.filter{it.returnType.classifier != Unit::class && it.parameters.size == 1}.map { function ->
        callableToJson(function, this)
    }

    val result = "{${(jsonPairs + jsonFunctions).joinToString(",")}}"
    println("result $result" )
    return result
}

private fun callableToJson(callable: KCallable<*>, obj: Any): String? {
    val value = try {
        callable.call(obj) // Chama tanto propriedades quanto funções
    } catch (e: Exception) {
        return null
    } ?: return """"${callable.name}":null"""

    val jsonKey = callable.findAnnotation<ToJsonPropName>()?.name ?: callable.name
    val formatterClass = callable.findAnnotation<ToJsonFormatter>()?.formatter?.createInstance()
    val formattedValue = formatterClass?.format(value) ?: value

    return when (formattedValue) {
        is String -> """"$jsonKey":"$formattedValue""""
        is Number, is Boolean -> """"$jsonKey":$formattedValue"""
        is List<*> -> """"$jsonKey":[${formattedValue.joinToString(",") { it?.membersToJson() ?: "null" }}]"""
        else -> """"$jsonKey":${formattedValue.membersToJson()}"""
    }
}