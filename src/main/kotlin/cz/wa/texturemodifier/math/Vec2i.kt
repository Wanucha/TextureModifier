package cz.wa.texturemodifier.math

class Vec2i(val x: Int, val y: Int) {

	constructor(v: Vec2d) : this(v.x.toInt(), v.y.toInt())

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Vec2i) return false

		if (x != other.x) return false
		if (y != other.y) return false

		return true
	}

	override fun hashCode(): Int {
		var result = x
		result = 31 * result + y
		return result
	}

	override fun toString(): String {
		return "${x}:${y}"
	}

	fun add(p: Vec2i): Vec2i {
		return Vec2i(x + p.x, y + p.y)
	}

	fun subtract(p: Vec2i): Vec2i {
		return Vec2i(x - p.x, y - p.y)
	}

	fun toDouble(): Vec2d {
		return Vec2d(this)
	}

	companion object {
		val ZERO = Vec2i(0, 0)
		val NEGATIVE = Vec2i(-1, -1)
	}
}
