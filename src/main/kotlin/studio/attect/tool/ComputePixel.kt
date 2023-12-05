package studio.attect.tool

import studio.attect.tool.ComputeBackgroundColor.*
import java.nio.ByteBuffer
import kotlin.math.abs

class ComputePixel {

    var alpha = 0
    var red = 0
    var green = 0
    var blue = 0

    constructor(source: ByteBuffer) {
        alpha = source.readColor()
        red = source.readColor()
        green = source.readColor()
        blue = source.readColor()
    }

    constructor(argb: Int) {
        alpha = (argb shr 24) and 0xFF
        red = (argb shr 16) and 0xFF
        green = (argb shr 8) and 0xFF
        blue = argb and 0xFF
    }

    constructor(alpha: Int, red: Int, green: Int, blue: Int) {
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue
    }

    constructor(red: Int, green: Int, blue: Int) : this(255, red, green, blue)

    /**
     * 计算给定的两个颜色是否相差在指定范围内
     */
    fun isWithinTolerance(other: ComputePixel, otherBackgroundColor: ComputeBackgroundColor, tolerance: Int): Boolean {
        val diff = when (otherBackgroundColor) {
            RED -> abs(this.red - other.red)
            GREEN -> abs(this.green - other.green)
            BLUE -> abs(this.blue - other.blue)
        }
//        println("this.green:${this.green} other.green:${other.green} diff:$diff $otherBackgroundColor")
        return diff <= tolerance
    }

    fun isWithinTolerance(computeBackgroundColor: ComputeBackgroundColor, tolerance: Int): Boolean {
        return when (computeBackgroundColor) {
            RED -> isWithinTolerance(255, 0, 0, tolerance)
            GREEN -> isWithinTolerance(0, 255, 0, tolerance)
            BLUE -> isWithinTolerance(0, 0, 255, tolerance)
        }
    }

    fun isWithinTolerance(red: Int, green: Int, blue: Int, tolerance: Int): Boolean {
        val redDiff = abs(this.red - red)
        val greenDiff = abs(this.green - green)
        val blueDiff = abs(this.blue - blue)

        // 将容差值从0-100的范围转换为0-255的范围

        return redDiff <= tolerance && greenDiff <= tolerance && blueDiff <= tolerance
    }

    fun isWithinTolerance(other: ComputePixel, tolerance: Int): Boolean {
        val redDiff = abs(this.red - other.red)
        val greenDiff = abs(this.green - other.green)
        val blueDiff = abs(this.blue - other.blue)

        // 将容差值从0-100的范围转换为0-255的范围

        return redDiff <= tolerance && greenDiff <= tolerance && blueDiff <= tolerance
    }

    fun toInt(): Int {
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }


    private fun ByteBuffer.readColor(): Int {
        return (get().toInt() and 0xFF)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComputePixel

        if (alpha != other.alpha) return false
        if (red != other.red) return false
        if (green != other.green) return false
        if (blue != other.blue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alpha
        result = 31 * result + red
        result = 31 * result + green
        result = 31 * result + blue
        return result
    }

    override fun toString(): String {
        return "ComputePixel(alpha=$alpha, red=$red, green=$green, blue=$blue)"
    }
}