package VGTools

import java.beans.Introspector
import java.beans.PropertyDescriptor

fun Any.propertyDescriptors(): Array<PropertyDescriptor> {
    val beanInfo = Introspector.getBeanInfo(this.javaClass)
    return beanInfo.propertyDescriptors
}

fun Any.foreachProperty(action: (String, Any?) -> Unit) {

    fun String.isClass(): Boolean = this.compareTo(other = "class", ignoreCase = true) == 0

    propertyDescriptors().forEach {
        val key = it.name
        val getter = it.readMethod
        val value = getter?.invoke(this)

        if (key.isClass()) return@forEach

        action(key, value)
    }
}

