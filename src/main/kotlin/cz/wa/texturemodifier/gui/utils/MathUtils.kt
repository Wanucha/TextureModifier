package cz.wa.texturemodifier.gui.utils

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sqrt

object MathUtils {
    fun gauss(s: Double, x: Double): Double {
        val s2 = 2 * s * s
        return exp(-x * x / s2) / sqrt(s2 * PI)
    }

    fun mod(x: Int, m: Int): Int {
        val d = x % m
        return if (d < 0) d + m else d
    }
}