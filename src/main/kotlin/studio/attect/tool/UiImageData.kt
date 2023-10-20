package studio.attect.tool

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.io.ByteArrayInputStream

/**
 * 显示在UI上的图片数据
 */
object UiImageData {

    /**
     * 用于显示的黑色背景图片
     */
    var blackBackgroundImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * 用于处理的黑色背景图片数据
     */
    var blackBackgroundImageData = ByteArray(0)
        set(value){
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
     * 图片A应透明背景色
     */
    var colorA by mutableStateOf(ComputeBackgroundColor.GREEN)

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
    var colorB by mutableStateOf(ComputeBackgroundColor.BLUE)

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
    var colorBackgroundOffset by mutableStateOf(0f)
}