package studio.attect.tool

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import studio.attect.tool.ComputeBackgroundColor.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.system.measureNanoTime

/**
 * 显示在UI上的图片数据
 */
object UiImageData {

    /**
     * 预览的图片
     */
    var previewImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * 预览的图片的数据，实际也为计算结果
     */
    var previewImageData = ByteArray(0)
        set(value) {
            previewImage = if (value.isEmpty()) {
                null
            } else {
                loadImageBitmap(ByteArrayInputStream(value))
            }
            field = value
        }

    /**
     * 预览图片缩放
     */
    var previewImageScale by mutableStateOf(1f)

    /**
     * 预览图片显示横轴偏移
     */
    var previewImageOffsetX by mutableStateOf(0f)

    /**
     * 预览图片显示纵轴偏移
     */
    var previewImageOffsetY by mutableStateOf(0f)

    /**
     * 预览图片显示旋转角度
     */
    var previewImageRotate by mutableStateOf(0f)

    var previewMouseRotationLock = false

    var previewMouseRotationStartX = Float.MIN_VALUE

    /**
     * 用于显示的黑色背景图片
     */
    var blackBackgroundImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * 用于处理的黑色背景图片数据
     */
    var blackBackgroundImageData = ByteArray(0)
        set(value) {
            blackBackgroundImage = loadImageBitmap(ByteArrayInputStream(value))
            field = value
        }


    /**
     * 用于显示的白色背景图片
     */
    var whiteBackgroundImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * 用于处理的白色背景图片数据
     */
    var whiteBackgroundImageData = ByteArray(0)
        set(value) {
            whiteBackgroundImage = loadImageBitmap(ByteArrayInputStream(value))
            field = value
        }

    /**
     * 黑白地图计算平衡
     */
    var whiteBlackBalance by mutableStateOf(0.5f)


    /**
     * 图片A应透明背景色
     */
    var colorA by mutableStateOf(GREEN)

    /**
     * 用于显示的纯色背景图片A
     */
    var colorABackgroundImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * 用于处理的纯色背景图片A数据
     */
    var colorABackgroundImageData = ByteArray(0)
        set(value) {
            colorABackgroundImage = loadImageBitmap(ByteArrayInputStream(value))
            field = value
        }

    /**
     * 图片B应透明背景色
     */
    var colorB by mutableStateOf(BLUE)

    /**
     * 用于显示的纯色背景图片B
     */
    var colorBBackgroundImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * 用于处理的纯色背景图片B数据
     */
    var colorBBackgroundImageData = ByteArray(0)
        set(value) {
            colorBBackgroundImage = loadImageBitmap(ByteArrayInputStream(value))
            field = value
        }


    /**
     * 计算时容许与指定颜色的偏差值
     */
    var colorBackgroundTolerance by mutableStateOf(0f)

    /**
     * 计算前的数据检查
     */
    private fun checkBeforeCompute(): Boolean {
        if (whiteBackgroundImageData.isEmpty()) {
            globalHint = "请提供白色背景图片"
            return false
        }
        if (blackBackgroundImageData.isEmpty()) {
            globalHint = "请提供黑色背景图片"
            return false
        }
        if (colorABackgroundImageData.isEmpty() && colorBBackgroundImageData.isEmpty()) {
            globalHint = "请至少提供一张有色背景（红、绿、蓝）的图片"
            return false
        }

        val currentWhiteBackgroundImage = whiteBackgroundImage ?: return false
        val currentBlackBackgroundImage = blackBackgroundImage ?: return false
        val currentColorABackgroundImage = colorABackgroundImage
        val currentColorBBackgroundImage = colorBBackgroundImage

        if (currentColorABackgroundImage == null && currentColorBBackgroundImage == null) {
            globalHint = "请至少提供一张有色背景（红、绿、蓝）的图片"
            return false
        }

        if (!currentWhiteBackgroundImage.compareSize(currentBlackBackgroundImage)) {
            globalHint = "黑白底色的两张图片尺寸不一致，无法处理"
            return false
        }

        if (currentColorABackgroundImage != null) {
            if (!currentColorABackgroundImage.compareSize(currentWhiteBackgroundImage)) {
                globalHint = "有色背景（${colorA}）图片与其它图片尺寸不一致，无法处理"
                return false
            }
        }

        if (currentColorBBackgroundImage != null) {
            if (!currentColorBBackgroundImage.compareSize(currentWhiteBackgroundImage)) {
                globalHint = "有色背景（${colorB}）图片与其它图片尺寸不一致，无法处理"
            }
        }

        return true
    }

    /**
     * 重置预览图片的相关参数
     */
    fun resetPreview() {
        previewImageScale = 1f
        previewImageOffsetX = 0f
        previewImageOffsetY = 0f
        previewImageRotate = 0f
    }

    fun compute() {
        if (!checkBeforeCompute()) return
        //todo 多核优化
        if (colorABackgroundImage == null || colorBBackgroundImage == null) {
            compote3ImageMode()
        } else if (colorABackgroundImage != null && colorBBackgroundImage != null) {
            compute4ImageBitmap()
        }
    }

    private fun compote3ImageMode() {
        val currentColorBackgroundColor: ComputeBackgroundColor

        val colorBackgroundImage: ImageBitmap = if (colorABackgroundImage != null) {
            currentColorBackgroundColor = colorA
            colorABackgroundImage ?: throw IllegalStateException("colorABackgroundImage为null，存在其它线程修改了值？")
        } else if (colorBBackgroundImage != null) {
            currentColorBackgroundColor = colorB
            colorBBackgroundImage ?: throw IllegalStateException("colorBBackgroundImage为null，存在其它线程修改了值？")
        } else {
            throw IllegalStateException("有色图片数据均为null")
        }

        val currentWhiteBackgroundImage = whiteBackgroundImage ?: throw IllegalStateException("whiteBackgroundImage为null，存在其它线程修改了值？")
        val currentBlackBackgroundImage = blackBackgroundImage ?: throw IllegalStateException("blackBackgroundImage为null，存在其它线程修改了值？")


        val tolerance = (colorBackgroundTolerance * 100).toInt()
        val fullTransparentColor = ComputePixel(0, 0, 0, 0).toInt()

        val computeImage = BufferedImage(currentWhiteBackgroundImage.width, currentWhiteBackgroundImage.height, BufferedImage.TYPE_INT_ARGB)

        foreach3ImageBitmap(currentWhiteBackgroundImage, currentBlackBackgroundImage, colorBackgroundImage) { x, y, whitePixel, blackPixel, colorPixel ->
            if (colorPixel.alpha == 255 && colorPixel.isWithinTolerance(currentColorBackgroundColor, tolerance)) {
                computeImage.setRGB(x, y, fullTransparentColor)
            } else if (colorPixel == whitePixel) {
                computeImage.setRGB(x, y, whitePixel.toInt())
            } else if (colorPixel.isWithinTolerance(whitePixel, currentColorBackgroundColor, tolerance)) {
                val alphaAValue = when (currentColorBackgroundColor) {
                    RED -> ComputePixel::blue
                    GREEN -> ComputePixel::red
                    BLUE -> ComputePixel::green
                }
                val alphaBValue = when (currentColorBackgroundColor) {
                    RED -> ComputePixel::green
                    GREEN -> ComputePixel::blue
                    BLUE -> ComputePixel::red
                }

                val alphaA = (255 - (alphaAValue.get(colorPixel) - alphaAValue(whitePixel)).absoluteValue)
                val alphaB = (255 - (alphaBValue.get(colorPixel) - alphaBValue(whitePixel)).absoluteValue)

                val whiteBalance = whiteBlackBalance
                val blackBalance = 1 - whiteBalance

                val pixel = ComputePixel(
                    alpha = ((alphaA * whiteBalance) + (alphaB * blackBalance)).toInt(),
                    red = (whitePixel.red - (whitePixel.red - ((whitePixel.red * whiteBalance) + (blackPixel.red * blackBalance)))).toInt(),
                    green = (whitePixel.green - (whitePixel.green - ((whitePixel.green * whiteBalance) + (blackPixel.green * blackBalance)))).toInt(),
                    blue = (whitePixel.blue - (whitePixel.blue - ((whitePixel.blue * whiteBalance) + (blackPixel.blue * blackBalance)))).toInt(),
                )
                computeImage.setRGB(x, y, pixel.toInt())
            } else {
                computeImage.setRGB(x, y, whitePixel.toInt())
            }
        }

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(computeImage, "png", outputStream)
        previewImageData = outputStream.toByteArray()
        resetPreview()
    }

    private fun foreach3ImageBitmap(imageBitmapA: ImageBitmap, imageBitmapB: ImageBitmap, imageBitmapC: ImageBitmap, block: (x: Int, y: Int, pixelA: ComputePixel, pixelB: ComputePixel, pixelC: ComputePixel) -> Unit) {
        val width = imageBitmapA.width
        val height = imageBitmapA.height

        val bufferSize = width * height
        val bufferA = IntArray(bufferSize)
        val bufferB = IntArray(bufferSize)
        val bufferC = IntArray(bufferSize)

        imageBitmapA.readPixels(bufferA, 0, 0, width, height)
        imageBitmapB.readPixels(bufferB, 0, 0, width, height)
        imageBitmapC.readPixels(bufferC, 0, 0, width, height)

        val cores = Runtime.getRuntime().availableProcessors()
        val childCount = ceil(height.toDouble() / cores).toInt()
//        measureNanoTime {
//            runBlocking {
//                repeat(cores){processorIndex->
//                    launch {
//                        for (y in processorIndex*childCount until (processorIndex+1)*childCount){
//                            repeat(width){x->
//                                val position = x + (y * width)
//                                val pixelA = ComputePixel(bufferA[position])
//                                val pixelB = ComputePixel(bufferB[position])
//                                val pixelC = ComputePixel(bufferC[position])
//
//                                block(x, y, pixelA, pixelB, pixelC)
//                            }
//                        }
//                    }
//                }
//            }
//        }.also { println("多协程时间:$it") }
//
//        measureNanoTime {
//            ImageWorker.commitJob { processorIndex->
//                for (y in processorIndex*childCount until (processorIndex+1)*childCount){
//                    repeat(width){x->
//                        val position = x + (y * width)
//                        val pixelA = ComputePixel(bufferA[position])
//                        val pixelB = ComputePixel(bufferB[position])
//                        val pixelC = ComputePixel(bufferC[position])
//
//                        block(x, y, pixelA, pixelB, pixelC)
//                    }
//                }
//            }
//        }.also { println("线程池时间:$it") }


        measureNanoTime {
            repeat(height) { y ->
                repeat(width) { x ->
                    val position = x + (y * width)
                    val pixelA = ComputePixel(bufferA[position])
                    val pixelB = ComputePixel(bufferB[position])
                    val pixelC = ComputePixel(bufferC[position])

                    block(x, y, pixelA, pixelB, pixelC)
                }
            }
        }.also { println("单线程时间:$it") }

    }

    private fun compute4ImageBitmap() {
        //todo
    }
}

/**
 * 比较两个ImageBitmap宽高是否一致
 */
private fun ImageBitmap.compareSize(otherImageBitmap: ImageBitmap): Boolean {
    return width == otherImageBitmap.width && height == otherImageBitmap.height
}

private fun ImageBitmap.foreach(block: (x: Int, y: Int, pixel: ComputePixel) -> Unit) {
    val buffer = intArrayOf(width * height)
    readPixels(buffer)

    repeat(height) { y ->
        repeat(width) { x ->
            val pixel = ComputePixel(buffer[x + (y * width)])
            block(x, y, pixel)
        }
    }
}