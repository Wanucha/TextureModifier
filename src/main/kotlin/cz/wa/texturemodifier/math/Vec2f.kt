package cz.wa.texturemodifier.math

class Vec2f(val x: Float, val y: Float) {

	constructor(v: Vec2i) : this(v.x.toFloat(), v.y.toFloat())

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Vec2f) return false

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
		val ZERO = Vec2f(0f, 0f)
	}
}
