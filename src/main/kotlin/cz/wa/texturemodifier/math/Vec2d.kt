package cz.wa.texturemodifier.math

class Vec2d (val x: Double, val y: Double) {

    constructor(v: Vec2i): this(v.x.toDouble(), v.y.toDouble())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2d) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
    override fun toString(): String {
        return "${x}:${y}"
    }

    companion object {
        val ZERO = Vec2d(0.0, 0.0)
    }
}