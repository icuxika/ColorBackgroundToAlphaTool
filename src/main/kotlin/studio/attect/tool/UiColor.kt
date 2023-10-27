@file:Suppress("PropertyName")

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
    val 窗口_背景颜色: Color

    val 预览区_背景颜色: Color
    val 预览区_文字颜色: Color

    val 素材区_背景颜色: Color
    val 素材区_滚动条颜色: Color
    val 素材区_滚动条激活颜色: Color

    val 素材项_默认背景颜色: Color
    val 素材项_指向背景颜色: Color
    val 素材项_阴影颜色: Color
    val 素材项_文字颜色: Color
    val 素材项_色彩提示_背景颜色: Color
    val 素材项_色彩提示_标签_背景颜色: Color
    val 素材项_色彩提示_标签_文字颜色: Color
    val 素材项_色彩选项_红色_未选中_背景颜色: Color
    val 素材项_色彩选项_红色_选中_背景颜色: Color
    val 素材项_色彩选项_绿色_未选中_背景颜色: Color
    val 素材项_色彩选项_绿色_选中_背景颜色: Color
    val 素材项_色彩选项_蓝色_未选中_背景颜色: Color
    val 素材项_色彩选项_蓝色_选中_背景颜色: Color

    val 配置面板_触发按钮_背景颜色: Color
    val 配置面板_触发按钮_文字颜色: Color
    val 配置面板_背景颜色: Color
    val 配置面板_标签_文字颜色: Color
    val 配置面板_标签_可点击_文字颜色: Color
    val 配置面板_滑杆_手柄颜色: Color
    val 配置面板_滑杆_有效条颜色: Color
    val 配置面板_滑杆_无效条颜色: Color


    val 底部提示条_背景颜色: Color
    val 底部提示条_文字颜色: Color

    val 分割线颜色: Color
}

@Suppress("NonAsciiCharacters")
object CurrentUiColor : UiColor {
    private var selectedColor: UiColor by mutableStateOf(LightUiColor)
    lateinit var transition: Transition<UiColor>
    override var 窗口_背景颜色: Color by mutableStateOf(selectedColor.窗口_背景颜色)
    override var 预览区_背景颜色: Color by mutableStateOf(selectedColor.预览区_背景颜色)
    override var 预览区_文字颜色: Color by mutableStateOf(selectedColor.预览区_文字颜色)
    override var 素材区_背景颜色: Color by mutableStateOf(selectedColor.素材区_背景颜色)
    override var 素材区_滚动条颜色: Color by mutableStateOf(selectedColor.素材区_滚动条颜色)
    override var 素材区_滚动条激活颜色: Color by mutableStateOf(selectedColor.素材区_滚动条激活颜色)
    override var 素材项_默认背景颜色: Color by mutableStateOf(selectedColor.素材项_默认背景颜色)
    override var 素材项_指向背景颜色: Color by mutableStateOf(selectedColor.素材项_指向背景颜色)
    override var 素材项_阴影颜色: Color by mutableStateOf(selectedColor.素材项_阴影颜色)
    override var 素材项_文字颜色: Color by mutableStateOf(selectedColor.素材项_文字颜色)
    override var 素材项_色彩提示_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩提示_背景颜色)
    override var 素材项_色彩提示_标签_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩提示_标签_背景颜色)
    override var 素材项_色彩提示_标签_文字颜色: Color by mutableStateOf(selectedColor.素材项_色彩提示_标签_文字颜色)
    override var 素材项_色彩选项_红色_未选中_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩选项_红色_未选中_背景颜色)
    override var 素材项_色彩选项_红色_选中_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩选项_红色_选中_背景颜色)
    override var 素材项_色彩选项_绿色_未选中_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩选项_绿色_未选中_背景颜色)
    override var 素材项_色彩选项_绿色_选中_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩选项_绿色_选中_背景颜色)
    override var 素材项_色彩选项_蓝色_未选中_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩选项_蓝色_未选中_背景颜色)
    override var 素材项_色彩选项_蓝色_选中_背景颜色: Color by mutableStateOf(selectedColor.素材项_色彩选项_蓝色_选中_背景颜色)
    override var 配置面板_触发按钮_背景颜色: Color by mutableStateOf(selectedColor.配置面板_触发按钮_背景颜色)
    override var 配置面板_触发按钮_文字颜色: Color by mutableStateOf(selectedColor.配置面板_触发按钮_文字颜色)
    override var 配置面板_背景颜色: Color by mutableStateOf(selectedColor.配置面板_背景颜色)
    override var 配置面板_标签_文字颜色: Color by mutableStateOf(selectedColor.配置面板_标签_文字颜色)
    override var 配置面板_标签_可点击_文字颜色: Color by mutableStateOf(selectedColor.配置面板_标签_可点击_文字颜色)
    override var 配置面板_滑杆_手柄颜色: Color by mutableStateOf(selectedColor.配置面板_滑杆_手柄颜色)
    override var 配置面板_滑杆_有效条颜色: Color by mutableStateOf(selectedColor.配置面板_滑杆_有效条颜色)
    override var 配置面板_滑杆_无效条颜色: Color by mutableStateOf(selectedColor.配置面板_滑杆_无效条颜色)
    override var 底部提示条_背景颜色: Color by mutableStateOf(selectedColor.底部提示条_背景颜色)
    override var 底部提示条_文字颜色: Color by mutableStateOf(selectedColor.底部提示条_文字颜色)
    override var 分割线颜色: Color by mutableStateOf(selectedColor.分割线颜色)

    @Composable
    fun init() {
        transition = updateTransition(selectedColor)
        窗口_背景颜色 = transition.animateColor { state ->
            state.窗口_背景颜色
        }.value
        预览区_背景颜色 = transition.animateColor { state ->
            state.预览区_背景颜色
        }.value
        预览区_文字颜色 = transition.animateColor { state ->
            state.预览区_文字颜色
        }.value
        素材区_背景颜色 = transition.animateColor { state ->
            state.素材区_背景颜色
        }.value
        素材区_滚动条颜色 = transition.animateColor { state ->
            state.素材区_滚动条颜色
        }.value
        素材区_滚动条激活颜色 = transition.animateColor { state ->
            state.素材区_滚动条激活颜色
        }.value
        素材项_默认背景颜色 = transition.animateColor { state ->
            state.素材项_默认背景颜色
        }.value
        素材项_指向背景颜色 = transition.animateColor { state ->
            state.素材项_指向背景颜色
        }.value
        素材项_阴影颜色 = transition.animateColor { state ->
            state.素材项_阴影颜色
        }.value
        素材项_文字颜色 = transition.animateColor { state ->
            state.素材项_文字颜色
        }.value
        素材项_色彩提示_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩提示_背景颜色
        }.value
        素材项_色彩提示_标签_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩提示_标签_背景颜色
        }.value
        素材项_色彩提示_标签_文字颜色 = transition.animateColor { state ->
            state.素材项_色彩提示_标签_文字颜色
        }.value
        素材项_色彩选项_红色_选中_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩选项_红色_选中_背景颜色
        }.value
        素材项_色彩选项_红色_未选中_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩选项_红色_未选中_背景颜色
        }.value
        素材项_色彩选项_绿色_选中_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩选项_绿色_选中_背景颜色
        }.value
        素材项_色彩选项_绿色_未选中_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩选项_绿色_未选中_背景颜色
        }.value
        素材项_色彩选项_蓝色_选中_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩选项_蓝色_选中_背景颜色
        }.value
        素材项_色彩选项_蓝色_未选中_背景颜色 = transition.animateColor { state ->
            state.素材项_色彩选项_蓝色_未选中_背景颜色
        }.value
        配置面板_触发按钮_背景颜色 = transition.animateColor { state ->
            state.配置面板_触发按钮_背景颜色
        }.value
        配置面板_触发按钮_文字颜色 = transition.animateColor { state ->
            state.配置面板_触发按钮_文字颜色
        }.value
        配置面板_背景颜色 = transition.animateColor { state ->
            state.配置面板_背景颜色
        }.value
        配置面板_标签_文字颜色 = transition.animateColor { state ->
            state.配置面板_标签_文字颜色
        }.value
        配置面板_标签_可点击_文字颜色 = transition.animateColor { state ->
            state.配置面板_标签_可点击_文字颜色
        }.value
        配置面板_滑杆_手柄颜色 = transition.animateColor { state ->
            state.配置面板_滑杆_手柄颜色
        }.value
        配置面板_滑杆_有效条颜色 = transition.animateColor { state ->
            state.配置面板_滑杆_有效条颜色
        }.value
        配置面板_滑杆_无效条颜色 = transition.animateColor { state ->
            state.配置面板_滑杆_无效条颜色
        }.value
        底部提示条_背景颜色 = transition.animateColor { state ->
            state.底部提示条_背景颜色
        }.value
        底部提示条_文字颜色 = transition.animateColor { state ->
            state.底部提示条_文字颜色
        }.value
        分割线颜色 = transition.animateColor { state ->
            state.分割线颜色
        }.value
    }

    fun changeColor(uiColor: UiColor) {
        selectedColor = uiColor
    }

    fun currentColor(): UiColor {
        return selectedColor
    }

}

@Suppress("NonAsciiCharacters")
object LightUiColor : UiColor {
    override val 窗口_背景颜色: Color = Color.White
    override val 预览区_背景颜色: Color = 窗口_背景颜色
    override val 预览区_文字颜色: Color = Color(0xFF333333)
    override val 素材区_背景颜色: Color = Color(0xFFCCCCCC)
    override val 素材区_滚动条颜色: Color = Color.Black.copy(alpha = 0.5f)
    override val 素材区_滚动条激活颜色: Color = Color.Black.copy(alpha = 0.8f)
    override val 素材项_默认背景颜色: Color = Color(0xFFEEEEEE)
    override val 素材项_指向背景颜色: Color = Color(0xFFBBBBBB)
    override val 素材项_阴影颜色: Color = Color(0xFF222222)
    override val 素材项_文字颜色: Color = Color(0xFF333333)
    override val 素材项_色彩提示_背景颜色: Color = Color(0xFFDDDDDD)
    override val 素材项_色彩提示_标签_背景颜色: Color = 素材项_色彩提示_背景颜色
    override val 素材项_色彩提示_标签_文字颜色: Color = 素材项_文字颜色

    override val 素材项_色彩选项_红色_选中_背景颜色: Color = Color.Red
    override val 素材项_色彩选项_红色_未选中_背景颜色: Color = 素材项_色彩选项_红色_选中_背景颜色.copy(0.2f)
    override val 素材项_色彩选项_绿色_选中_背景颜色: Color = Color.Green
    override val 素材项_色彩选项_绿色_未选中_背景颜色: Color = 素材项_色彩选项_绿色_选中_背景颜色.copy(0.2f)
    override val 素材项_色彩选项_蓝色_选中_背景颜色: Color = Color.Blue
    override val 素材项_色彩选项_蓝色_未选中_背景颜色: Color = 素材项_色彩选项_蓝色_选中_背景颜色.copy(0.2f)

    override val 配置面板_触发按钮_背景颜色: Color = Color.White
    override val 配置面板_触发按钮_文字颜色: Color = Color.Black
    override val 配置面板_背景颜色: Color = Color(0xFFE7F8FF)
    override val 配置面板_标签_文字颜色: Color = 素材项_文字颜色
    override val 配置面板_标签_可点击_文字颜色: Color = Color(0xFF0652C9)
    override val 配置面板_滑杆_手柄颜色 = Color(0xFFCF1B1B)
    override val 配置面板_滑杆_有效条颜色 = Color(0xFF1B93FF)
    override val 配置面板_滑杆_无效条颜色 = Color(0xFFB2D3FF)

    override val 底部提示条_背景颜色: Color = Color(0XFFF8F8F8)
    override val 底部提示条_文字颜色: Color = 素材项_文字颜色

    override val 分割线颜色: Color = Color.DarkGray
}

@Suppress("NonAsciiCharacters")
object DarkUiColor : UiColor {
    override val 窗口_背景颜色: Color = Color.Black
    override val 预览区_背景颜色: Color = 窗口_背景颜色
    override val 预览区_文字颜色: Color = Color(0xFFCCCCCC)
    override val 素材区_背景颜色: Color = Color(0xff080808)
    override val 素材区_滚动条颜色: Color = Color.White.copy(alpha = 0.5f)
    override val 素材区_滚动条激活颜色: Color = Color.White.copy(alpha = 0.8f)
    override val 素材项_默认背景颜色: Color = Color(0xFF222222)
    override val 素材项_指向背景颜色: Color = Color(0xFF555555)
    override val 素材项_阴影颜色: Color = Color.Black
    override val 素材项_文字颜色: Color = Color(0xFFCCCCCC)
    override val 素材项_色彩提示_背景颜色: Color = Color(0xFF444444)
    override val 素材项_色彩提示_标签_背景颜色: Color = Color(0xFF444444)
    override val 素材项_色彩提示_标签_文字颜色: Color = 素材项_文字颜色

    override val 素材项_色彩选项_红色_选中_背景颜色: Color = Color.Red
    override val 素材项_色彩选项_红色_未选中_背景颜色: Color = 素材项_色彩选项_红色_选中_背景颜色.copy(0.2f)
    override val 素材项_色彩选项_绿色_选中_背景颜色: Color = Color.Green
    override val 素材项_色彩选项_绿色_未选中_背景颜色: Color = 素材项_色彩选项_绿色_选中_背景颜色.copy(0.2f)
    override val 素材项_色彩选项_蓝色_选中_背景颜色: Color = Color.Blue
    override val 素材项_色彩选项_蓝色_未选中_背景颜色: Color = 素材项_色彩选项_蓝色_选中_背景颜色.copy(0.2f)

    override val 配置面板_触发按钮_背景颜色: Color = Color(0xFFCCCCCC)
    override val 配置面板_触发按钮_文字颜色: Color = Color.White
    override val 配置面板_背景颜色: Color = Color(0xFF1B2629)
    override val 配置面板_标签_文字颜色: Color = 素材项_文字颜色
    override val 配置面板_标签_可点击_文字颜色: Color = Color(0xFF72A1EB)
    override val 配置面板_滑杆_手柄颜色 = Color(0xFFFF8585)
    override val 配置面板_滑杆_有效条颜色 = Color(0xFF8FD5FF)
    override val 配置面板_滑杆_无效条颜色 = Color(0xFF0B2EBD)

    override val 底部提示条_背景颜色: Color = Color(0xFF333333)
    override val 底部提示条_文字颜色: Color = 素材项_文字颜色

    override val 分割线颜色: Color = Color.LightGray

}