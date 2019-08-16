package vgTools

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Method

fun <T> T.propertyDescriptors(): Array<PropertyDescriptor> {
    val beanInfo = Introspector.getBeanInfo(this?.javaClass)
    return beanInfo.propertyDescriptors
}

inline fun <T> T.foreachProperty(action: (key: String, value: Any?, setter: Method?) -> Unit) {

    propertyDescriptors().forEach {
        val key = it.name
        val getter = it.readMethod
        val setter = it.writeMethod
        val value = getter?.invoke(this)

        if (key == "class") return@forEach

        action(key, value, setter)
    }
}

@Throws(Exception::class)
fun <T> T.toMap(): MutableMap<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    this?.foreachProperty { key, value, _ ->
        map[key] = value
    }
    return map
}

@Throws(Exception::class)
fun <T> T.setValueByMap(map: MutableMap<String, Any?>) {
    this?.foreachProperty { key, _, setter ->
        if (map[key] == null) return@foreachProperty
        else setter?.invoke(this, map[key])
    }
}

fun <T> List<T>.toMap(): Map<String, Any?> {
    return this.map { this.indexOf(it) to it }.toMap()
}