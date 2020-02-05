package vgTools

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Method
import java.sql.Timestamp
import java.text.SimpleDateFormat

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

@Throws(Exception::class)
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
        "java.lang.String" -> value.toString()
        "java.lang.Boolean" -> value != 0
        else -> value
    }
}

fun List<Any>.toMap(): Map<String, Any?> {
    return this.map { this.indexOf(it) to it }.toMap()
}

operator fun MutableMap<String, Any?>.plus(other: MutableMap<String, Any?>): MutableMap<String, Any?> = this + other

fun Timestamp.isInDate(checkedDate: Timestamp, rangeDate: Int): Boolean {

    val startDate = Timestamp(this.time)
    val endDate = startDate + rangeDate
    return checkedDate.inRange(startDate, endDate)
}

operator fun Timestamp.plus(date: Int): Timestamp {
    val newTime = Timestamp(this.time)
    newTime.date = newTime.date + date
    return newTime
}

operator fun Timestamp.minus(date: Int): Timestamp {
    val newTime = Timestamp(this.time)
    newTime.date = newTime.date - date
    return newTime
}

fun Timestamp.inRange(startDate: Timestamp, endDate: Timestamp): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(this) in sdf.format(startDate)..sdf.format(endDate)
}


fun Timestamp.format(format: String = "yyyy-MM-dd HH:mm:ss.SSS"): String = SimpleDateFormat(format).format(this)