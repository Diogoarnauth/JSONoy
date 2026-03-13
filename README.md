# JSONoy – JSON Encoder using Reflection

Implementation of a **JSON encoder using Reflection in Kotlin**, capable of converting objects into JSON format by inspecting their fields, properties, and functions at runtime.

This project was developed to explore and understand the **Java Reflection API** and **Kotlin Reflection API**, as well as recursive object serialization.

> "Programming is understanding." — Kristen Nygaard

---

# Project Goals

The objective of this project is to implement a **JSON encoder** capable of converting Kotlin objects into JSON strings without using external JSON libraries.

The encoder must:

- Inspect object structure dynamically using **reflection**
- Serialize **primitive values and complex objects**
- Handle **collections**
- Support **custom JSON property names**
- Support **custom formatting of values through annotations**
- Recursively encode nested objects

The implementation is divided into two main parts:

1. `fieldsToJson` – serialization using reflection over object fields.
2. `membersToJson` – serialization using Kotlin reflection over properties and functions.

---

# 1. fieldsToJson – Encoding Fields

The `fieldsToJson` extension function converts the **fields of an object into JSON**.

```kotlin
fun Any.fieldsToJson(): String
```

This function uses Kotlin reflection to inspect all properties of a class using:

```kotlin
this::class.memberProperties
```

For each property, the function:

1. Retrieves the property value.
2. Determines its type.
3. Converts it into JSON format.
4. If the property is a complex object, the function calls itself recursively.

## Supported Types

The implementation handles:

| Type | JSON Output |
|-----|-----|
| String | `"value"` |
| Number | `value` |
| Boolean | `true / false` |
| List | `[ ... ]` |
| Object | `{ ... }` |

## Example

Given the object:

```kotlin
data class Address(val city: String, val zip: Int)
```

Calling:

```kotlin
address.fieldsToJson()
```

Produces:

```json
{"city":"Lisbon","zip":1000}
```

## Handling Lists

Lists are encoded by iterating through each element and recursively converting them to JSON.

```kotlin
is List<*> -> "[ ... ]"
```

Each element is processed with `fieldsToJson()`.

---

# 2. membersToJson – Encoding Members

The `membersToJson` function extends the encoder to support:

- **Properties**
- **Parameterless functions**
- **Custom annotations**

```kotlin
fun Any.membersToJson(): String
```

Unlike `fieldsToJson`, this function inspects both:

```kotlin
memberProperties
memberFunctions
```

## Function Handling

Only functions that:

- return a value (not `Unit`)
- have no parameters

are included.

```kotlin
it.returnType.classifier != Unit::class && it.parameters.size == 1
```

These functions are invoked dynamically using:

```kotlin
callable.call(obj)
```

---

# Custom Annotations

Two annotations were implemented to customize JSON serialization.

---

## @ToJsonPropName

Allows renaming a property or function in the generated JSON.

```kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class ToJsonPropName(
    val name: String
)
```

Example:

```kotlin
@ToJsonPropName("full_name")
val name: String
```

JSON output:

```json
{
  "full_name": "John"
}
```

---

## @ToJsonFormatter

Allows applying a **custom formatter** to transform the value before serialization.

```kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class ToJsonFormatter(
    val formatter: KClass<out ToJsonValueFormatter>
)
```

The formatter must implement the interface:

```kotlin
interface ToJsonValueFormatter {
    fun format(value: Any): String
}
```

This allows developers to define custom formatting logic.

Example:

```kotlin
class UpperCaseFormatter : ToJsonValueFormatter {
    override fun format(value: Any): String {
        return value.toString().uppercase()
    }
}
```

The formatter is instantiated using reflection:

```kotlin
createInstance()
```

---

# Recursive Serialization

Both `fieldsToJson` and `membersToJson` support **recursive encoding**.

If a property contains another object:

```kotlin
else -> value.membersToJson()
```

This ensures nested objects are properly converted to JSON.

Example structure:

```
Person
 ├── name
 ├── address
      ├── city
      └── zip
```

Resulting JSON:

```json
{
  "name": "Alice",
  "address": {
    "city": "Lisbon",
    "zip": 1000
  }
}
```

---

# Main Concepts Learned

This project focuses on understanding **reflection and dynamic programming in Kotlin**.

Key learning points include:

## 1. Reflection

Reflection allows a program to inspect and interact with its own structure at runtime.

Concepts used:

- `KClass`
- `KProperty`
- `KFunction`
- `KCallable`
- `memberProperties`
- `memberFunctions`

---

## 2. Dynamic Invocation

Methods and properties can be invoked dynamically using:

```kotlin
callable.call(obj)
```

This allows accessing members without knowing them at compile time.

---

## 3. Annotations Processing

Annotations can be retrieved at runtime using:

```kotlin
findAnnotation<T>()
```

This enables metadata-driven behavior such as:

- renaming properties
- applying custom formatting

---

## 4. Recursive Serialization

Objects may contain nested objects or collections.  
Recursive serialization ensures that the entire object graph is converted to JSON.

---

## 5. Type Inspection

Using Kotlin's type system to determine how each value should be encoded:

```kotlin
when (value) {
    is String
    is Number
    is Boolean
    is List<*>
}
```

---

# Challenges Encountered

Some challenges during the implementation included:

- correctly identifying **which functions should be serialized**
- dynamically **invoking methods using reflection**
- handling **null values**
- ensuring **recursive encoding without infinite loops**
- implementing **annotation-based customization**

---

# Conclusion

This project demonstrates how reflection can be used to build a **custom JSON serialization framework** from scratch.

Instead of relying on libraries such as Jackson or Gson, the encoder dynamically analyzes the structure of objects and converts them into JSON.

Through this project it was possible to gain a deeper understanding of:

- Kotlin Reflection API
- dynamic program inspection
- annotations and metadata
- recursive data processing

These concepts are fundamental for understanding how many real-world frameworks operate internally.
