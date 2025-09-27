package com.github.repropertyx.android

import android.animation.IntEvaluator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.widget.CompoundButton
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T, V : Any> ReadWriteProperty<T, V>.animatedTyped(
    onApply: ValueAnimator.(V, V) -> Unit = { _, _ -> },
    evaluator: TypeEvaluator<V>,
): ReadWriteProperty<T, V> {
    val original = this
    return object : ReadWriteProperty<T, V> by original {
        var runningAnimator: ValueAnimator? = null
            set(value) {
                field?.cancel()
                field = value
            }

        override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
            val from = original.getValue(thisRef, property)

            runningAnimator = ValueAnimator.ofObject(evaluator, from, value).apply {
                onApply(from, value)
                addUpdateListener { animation ->
                    @Suppress("UNCHECKED_CAST")
                    val animatedValue = animation.animatedValue as V
                    original.setValue(thisRef, property, animatedValue)
                }
                start()
            }
        }
    }
}

/**
 * Usage:
 *
 * ```
 * var viewAnimatedY by propertyOf(
 *     get = { view.translateY },
 *     set = { view.translateY = it },
 *   )
 *   .distinctUntilChanged()
 *   .onEach { … }
 *   .animated()
 *   .distinctUntilChanged()
 * ```
 */
@JvmName("animatedInt")
fun <T> ReadWriteProperty<T, Int>.animated(
    onApply: ValueAnimator.(Int, Int) -> Unit = { _, _ ->
        duration = 300
        interpolator = FastOutSlowInInterpolator()
    }
): ReadWriteProperty<T, Int> = animatedTyped(onApply, IntEvaluator())

@JvmName("animatedFloat")
fun <T> ReadWriteProperty<T, Float>.animated(
    onApply: ValueAnimator.(Float, Float) -> Unit = { _, _ ->
        duration = 300
        interpolator = FastOutSlowInInterpolator()
    }
): ReadWriteProperty<T, Float> = animatedTyped(onApply) { fraction, start, end ->
    start + ((end - start) * fraction)
}

fun CompoundButton.setOnCheckedChangeListener(
    property: ReadWriteProperty<Any?, Boolean>,
) {
    var isCheckedState by property
    isChecked = isCheckedState
    setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
        isCheckedState = isChecked
    }
}