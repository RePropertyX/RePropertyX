package com.github.repropertyx.android

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.repropertyx.mutablePropertyOf
import com.github.repropertyx.onEachBefore
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T, V : Any> ReadWriteProperty<T, V>.animatedTyped(
    evaluator: TypeEvaluator<V>,
    onApply: ValueAnimator.(V, V) -> Unit = { _, _ -> },
): ReadWriteProperty<T, V> {
    val original = this
    var runningAnimator: ValueAnimator? by mutablePropertyOf<ValueAnimator?>(null).onEachBefore { it?.cancel() }

    return object : ReadWriteProperty<T, V> by original {
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
): ReadWriteProperty<T, Int> = animatedTyped(duration, interpolator, onApply) { fraction, start, end ->
    start + ((end - start) * fraction).toInt()
}

@JvmName("animatedFloat")
fun <T> ReadWriteProperty<T, Float>.animated(
    onApply: ValueAnimator.(Float, Float) -> Unit = { _, _ ->
        duration = 300
        interpolator = FastOutSlowInInterpolator()
    }
): ReadWriteProperty<T, Float> = animatedTyped(duration, interpolator, onApply) { fraction, start, end ->
    start + ((end - start) * fraction)
}