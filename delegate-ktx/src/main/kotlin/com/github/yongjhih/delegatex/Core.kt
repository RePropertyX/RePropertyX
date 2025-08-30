package com.github.yongjhih.delegatex

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> byWeakReference(value: T? = null, queue: ReferenceQueue<in T?>? = null) =
    WeakReference(value, queue).let {
        object : ReadWriteProperty<Any, T?> {
            private var ref: WeakReference<T?> = it
            override fun getValue(thisRef: Any, property: KProperty<*>): T? = ref.get()
            override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
                ref = WeakReference(value, queue)
            }
        }
    }