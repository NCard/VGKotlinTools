package com.gbtech.lanfarapi.vgTools

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import kotlin.reflect.typeOf

fun Any.propertyDescriptors(): Array<PropertyDescriptor> {
    val beanInfo = Introspector.getBeanInfo(this.javaClass)
    return beanInfo.propertyDescriptors
}

inline fun Any.foreachProperty(action: (key: String, value: Any?, setter: Method?, type: Class<*>) -> Unit) {

    propertyDescriptors().forEach {
        val key = it.name
        val getter = it.readMethod
        val setter = it.writeMethod
        val type = it.propertyType
        val value = getter?.invoke(this)

        if (key == "class") return@forEach

        action(key, value, setter, type)
    }
}

fun <T> copyProperties(obj1: T, obj2: T): T {
    obj1?.foreachProperty { key1, value1, _, _ ->
        obj2?.foreachProperty { key2, _, setter2, _ ->
            if (key2 == key1 && value1 != null) setter2?.invoke(obj2, value1)
        }
    }
    return obj2
}

@Throws(Exception::class)
fun Any.toMap(): MutableMap<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    this.foreachProperty { key, value, _, _ ->
        map[key] = value
    }
    return map
}

@Throws(Exception::class)
fun Any.setValueByMap(map: MutableMap<String, Any?>) {
    this.foreachProperty { key, _, setter, type ->
        val value = map[key]
        if (value == null) return@foreachProperty
        else {
            try {
                setter?.invoke(this, value)
            } catch (e: IllegalArgumentException) {
                setter?.invoke(this, typeSwitch(type, value))
            }
        }
    }
}

fun typeSwitch(type: Class<*>, value: Any?): Any? {
    return when (type.canonicalName) {
        "java.lang.Double" ->
            when(value) {
                is Int -> value.toDouble()
                is Float -> value.toDouble()
                else -> value
            }
        "java.lang.Float" ->
            when(value) {
                is Int -> value.toFloat()
                is Double -> value.toFloat()
                else -> value
            }
        else -> value
    }
}

fun List<Any>.toMap(): Map<String, Any?> {
    return this.map { this.indexOf(it) to it }.toMap()
}

operator fun MutableMap<String, Any?>.plus(other: MutableMap<String, Any?>): MutableMap<String, Any?> = this + other