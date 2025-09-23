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
    duration: Duration = 300.milliseconds,
    interpolator: Interpolator = FastOutSlowInInterpolator(),
    evaluator: TypeEvaluator<V>,
    onApply: ValueAnimator.(V) -> Unit = {}
): ReadWriteProperty<T, V> {
    val original = this
    var runningAnimator: ValueAnimator? by mutablePropertyOf<ValueAnimator?>(null).onEachBefore { it?.cancel() }

    return object : ReadWriteProperty<T, V> by original {
        override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
            val from = original.getValue(thisRef, property)

            runningAnimator = ValueAnimator.ofObject(evaluator, from, value).apply {
                this.duration = duration.inWholeMilliseconds
                this.interpolator = interpolator
                addUpdateListener { animation ->
                    @Suppress("UNCHECKED_CAST")
                    val animatedValue = animation.animatedValue as V
                    original.setValue(thisRef, property, animatedValue)
                }
                onApply(from)
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
    duration: Duration = 300.milliseconds,
    interpolator: Interpolator = FastOutSlowInInterpolator(),
    onApply: ValueAnimator.(Int) -> Unit = { }
): ReadWriteProperty<T, Int> = animatedTyped(duration, interpolator, { fraction, start, end ->
    start + ((end - start) * fraction).toInt()
}, onApply)

@JvmName("animatedFloat")
fun <T> ReadWriteProperty<T, Float>.animated(
    duration: Duration = 300.milliseconds,
    interpolator: Interpolator = FastOutSlowInInterpolator(),
    onApply: ValueAnimator.(Float) -> Unit = { },
): ReadWriteProperty<T, Float> = animatedTyped(duration, interpolator, { fraction, start, end ->
    start + ((end - start) * fraction)
}, onApply)
