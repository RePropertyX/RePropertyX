package com.github.repropertyx.android

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.widget.CompoundButton
import androidx.core.animation.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.repropertyx.distinctUntilChanged
import com.github.repropertyx.onEach
import com.github.repropertyx.propertyOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
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

suspend fun ValueAnimator.await(): Unit = suspendCancellableCoroutine { cont ->
    val listener = doOnEnd { if (cont.isActive) cont.resume(Unit) }
    cont.invokeOnCancellation { removeListener(listener) }
}

suspend fun View.animatedFloatAwait(
    value: Float,
    onApply: ValueAnimator.(Float, Float) -> Unit = { _, _ ->
        duration = 300
        interpolator = FastOutSlowInInterpolator()
    },
    get: View.(KProperty<*>) -> Float,
    set: View.(Float) -> Unit,
): Unit = suspendCancellableCoroutine { cont ->
    var listener: Animator.AnimatorListener? = null
    var animator: ValueAnimator? = null
    var animatedValue by propertyOf(
        get = { this@animatedFloatAwait.get(it) },
        set = { this@animatedFloatAwait.set(it) }
    ).animated { from, to ->
        onApply(from, to)
        animator = this
        listener = doOnEnd { if (cont.isActive) cont.resume(Unit) }
    }.distinctUntilChanged { old, new ->
        if (old == new) { if (cont.isActive) cont.resume(Unit) }
        old == new
    }
    cont.invokeOnCancellation { listener?.let { animator?.removeListener(it) }  }
    animatedValue = value
}
suspend fun View.animatedIntAwait(
    value: Int,
    onApply: ValueAnimator.(Int, Int) -> Unit = { _, _ ->
        duration = 300
        interpolator = FastOutSlowInInterpolator()
    },
    get: View.(KProperty<*>) -> Int,
    set: View.(Int) -> Unit,
): Unit = suspendCancellableCoroutine { cont ->
    var listener: Animator.AnimatorListener? = null
    var animator: ValueAnimator? = null
    var animatedValue by propertyOf(
        get = { this@animatedIntAwait.get(it) },
        set = { this@animatedIntAwait.set(it) }
    ).animated { from, to ->
        onApply(from, to)
        animator = this
        listener = doOnEnd { if (cont.isActive) cont.resume(Unit) }
    }.distinctUntilChanged { old, new ->
        if (old == new) { if (cont.isActive) cont.resume(Unit) }
        old == new
    }
    cont.invokeOnCancellation { listener?.let { animator?.removeListener(it) }  }
    animatedValue = value
}