package studio.attect.tool

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Suppress("NonAsciiCharacters")
interface UiColor {
    val 窗口背景颜色:Color

    val 预览区背景颜色:Color

    val 素材区背景颜色:Color
    val 素材区滚动条颜色:Color
    val 素材区滚动条激活颜色:Color

    val 素材项默认背景颜色:Color
    val 素材项指向背景颜色:Color
    val 素材项文字颜色:Color

    val 底部提示条背景颜色:Color
    val 底部提示条文字颜色:Color
}

@Suppress("NonAsciiCharacters")
object CurrentUiColor: UiColor {
    private var selectedColor: UiColor by mutableStateOf(LightUiColor)
    lateinit var transition :Transition<UiColor>
    override var 窗口背景颜色: Color by mutableStateOf(selectedColor.窗口背景颜色)
    override var 预览区背景颜色: Color by mutableStateOf( selectedColor.预览区背景颜色)
    override var 素材区背景颜色: Color by mutableStateOf( selectedColor.素材区背景颜色)
    override var 素材区滚动条颜色: Color by mutableStateOf( selectedColor.素材区滚动条颜色)
    override var 素材区滚动条激活颜色: Color by mutableStateOf( selectedColor.素材区滚动条激活颜色)
    override var 素材项默认背景颜色: Color by mutableStateOf( selectedColor.素材项默认背景颜色)
    override var 素材项指向背景颜色: Color by mutableStateOf( selectedColor.素材项指向背景颜色)
    override var 素材项文字颜色: Color by mutableStateOf( selectedColor.素材项文字颜色)
    override var 底部提示条背景颜色: Color by mutableStateOf( selectedColor.底部提示条背景颜色)
    override var 底部提示条文字颜色: Color by mutableStateOf( selectedColor.底部提示条文字颜色)

    @Composable
    fun init(){
        transition = updateTransition(selectedColor)
        窗口背景颜色 = transition.animateColor { state ->
            state.窗口背景颜色
        }.value
        预览区背景颜色 = transition.animateColor { state ->
            state.预览区背景颜色
        }.value
        素材区背景颜色 = transition.animateColor { state ->
            state.素材区背景颜色
        }.value
        素材区滚动条颜色 = transition.animateColor { state ->
            state.素材区滚动条颜色
        }.value
        素材区滚动条激活颜色 = transition.animateColor { state ->
            state.素材区滚动条激活颜色
        }.value
        素材项默认背景颜色 = transition.animateColor { state ->
            state.素材项默认背景颜色
        }.value
        素材项指向背景颜色 = transition.animateColor { state ->
            state.素材项指向背景颜色
        }.value
        素材项文字颜色 = transition.animateColor { state ->
            state.素材项文字颜色
        }.value
        底部提示条背景颜色 = transition.animateColor { state ->
            state.底部提示条背景颜色
        }.value
        底部提示条文字颜色 = transition.animateColor { state ->
            state.底部提示条文字颜色
        }.value
    }

    fun changeColor(uiColor: UiColor){
        selectedColor = uiColor

    }

}

@Suppress("NonAsciiCharacters")
object LightUiColor : UiColor {
    override val 窗口背景颜色: Color = Color.White
    override val 预览区背景颜色: Color = 窗口背景颜色
    override val 素材区背景颜色: Color = Color(0xfff8f8f8)
    override val 素材区滚动条颜色: Color = Color.Black.copy(alpha = 0.5f)
    override val 素材区滚动条激活颜色: Color =  Color.Black.copy(alpha = 0.8f)
    override val 素材项默认背景颜色: Color = Color(0xFFEEEEEE)
    override val 素材项指向背景颜色: Color = Color(0xFFBBBBBB)
    override val 素材项文字颜色: Color = Color(0xFF333333)
    override val 底部提示条背景颜色: Color = Color(0xFFCCCCCC)
    override val 底部提示条文字颜色: Color = 素材项文字颜色
}

@Suppress("NonAsciiCharacters")
object DarkUiColor : UiColor {
    override val 窗口背景颜色: Color = Color.Black
    override val 预览区背景颜色: Color = 窗口背景颜色
    override val 素材区背景颜色: Color = Color(0xff080808)
    override val 素材区滚动条颜色: Color = Color.White.copy(alpha = 0.5f)
    override val 素材区滚动条激活颜色: Color =  Color.White.copy(alpha = 0.8f)
    override val 素材项默认背景颜色: Color = Color(0xFF222222)
    override val 素材项指向背景颜色: Color = Color(0xFF555555)
    override val 素材项文字颜色: Color = Color(0xFFCCCCCC)
    override val 底部提示条背景颜色: Color = Color(0xFF333333)
    override val 底部提示条文字颜色: Color = 素材项文字颜色

}