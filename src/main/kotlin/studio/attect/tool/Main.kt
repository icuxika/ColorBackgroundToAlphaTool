@file:Suppress("FunctionName")

package studio.attect.tool

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skiko.toImage
import java.awt.Cursor
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.net.URI
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KMutableProperty0


//region 运行配置
/**
 * 限制文件图片在指定大小范围内，防止内存爆炸
 */
var noImageFileSizeLimit by mutableStateOf(false)
//endregion

val uiColor = CurrentUiColor
var globalHint by mutableStateOf("欢迎使用")

val fileDialogScope = object : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default + CoroutineName("文件选择器")
}

@Composable
@Preview
fun App(frameWindowScope: FrameWindowScope) {
    MaterialTheme {
        Column(Modifier.fillMaxSize().background(CurrentUiColor.窗口_背景颜色)) {
            Row(Modifier.fillMaxWidth().weight(1f).background(CurrentUiColor.预览区_背景颜色)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().width(IntrinsicSize.Max).height(IntrinsicSize.Max)) {
                    val currentImage = UiImageData.previewImage
                    if (currentImage == null) {
                        Text("请按底部提示提供素材", color = uiColor.预览区_文字颜色, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    } else {
                        Image(currentImage, contentDescription = "预览图片", modifier = Modifier.fillMaxSize())
                    }

                }
                OptionPanel(Modifier.width(240.dp).fillMaxHeight())

            }
            Box(modifier = Modifier.height(200.dp).background(CurrentUiColor.素材区_背景颜色)) {
                val scrollState = rememberScrollState()
                Row(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)) {
                    SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.whiteBackgroundImage, contentDescription = "白色背景图片", noImageText = "点击添加白色背景图片", targetByteArray = UiImageData::whiteBackgroundImageData)
                    SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.blackBackgroundImage, contentDescription = "黑色背景图片", noImageText = "点击添加黑色背景图片", targetByteArray = UiImageData::blackBackgroundImageData)
                    SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.colorABackgroundImage, contentDescription = "纯色A背景图片", noImageText = "点击添加纯色A背景图片", targetByteArray = UiImageData::colorABackgroundImageData)
                    SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.colorBBackgroundImage, contentDescription = "纯色B背景图片", noImageText = "点击添加纯色B背景图片", targetByteArray = UiImageData::colorBBackgroundImageData)
                }
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    adapter = rememberScrollbarAdapter(scrollState),
                    style = ScrollbarStyle(
                        minimalHeight = 16.dp,
                        thickness = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        hoverDurationMillis = 300,
                        unhoverColor = CurrentUiColor.素材区_滚动条颜色,
                        hoverColor = CurrentUiColor.素材区_滚动条激活颜色
                    )
                )
            }
            Row(modifier = Modifier.fillMaxWidth().background(CurrentUiColor.底部提示条_背景颜色).padding(5.dp)) {
                Text(globalHint, color = CurrentUiColor.底部提示条_文字颜色)
            }
        }
    }
}

lateinit var windowState: WindowState
fun main(args: Array<String>) = application {
    noImageFileSizeLimit = args.contains("--no-image-file-size-limit")

    windowState = rememberWindowState()
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    if (screenSize.height < 1080) {
        windowState.placement = WindowPlacement.Maximized
    } else if (screenSize.height in 1080..2159) {
        windowState.size = DpSize((screenSize.width * 0.8).dp, (screenSize.height * 0.8).dp)
    } else if (screenSize.height > 2160) {
        windowState.size = DpSize((screenSize.width * 0.6).dp, (screenSize.height * 0.6).dp)
    }

    UiImageData.whiteBackgroundImageData = FileInputStream(File("F:/透明测试-白背景.png")).readAllBytes()
    UiImageData.blackBackgroundImageData = FileInputStream(File("F:/透明测试-黑背景.png")).readAllBytes()
    UiImageData.colorABackgroundImageData = FileInputStream(File("F:/透明测试-绿背景.png")).readAllBytes()

    CurrentUiColor.init()
    Window(state = windowState, onCloseRequest = ::exitApplication, title = "差色背景去除工具") {
        App(this)
    }
}

/**
 * 选项面板
 */
@Composable
fun OptionPanel(modifier: Modifier = Modifier) {
    Column(modifier
        .background(uiColor.配置面板_背景颜色)
        .focusable(true)
        .drawBehind {
            val borderSize = 1.dp.toPx();
            drawLine(
                color = uiColor.分割线颜色,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = borderSize
            )
        }
    ) {
        DayNightModeSwitchButton()
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text("容差 ${String.format("%.0f", UiImageData.colorBackgroundTolerance * 100)}", color = uiColor.配置面板_标签_文字颜色)
            Slider(
                value = UiImageData.colorBackgroundTolerance,
                onValueChange = {
                    UiImageData.colorBackgroundTolerance = it
                },
                onValueChangeFinished = {
                    println("value change finished")
                },
                colors = SliderDefaults.colors(
                    thumbColor = uiColor.配置面板_滑杆_手柄颜色,
                    activeTrackColor = uiColor.配置面板_滑杆_有效条颜色,
                    inactiveTrackColor = uiColor.配置面板_滑杆_无效条颜色,
                )
            )
        }

        Button(onClick = {
            UiImageData.compute()
        }) {
            Text("计算")
        }

    }
}

/**
 * 明暗模式切换按钮
 */
@Composable
fun DayNightModeSwitchButton(modifier: Modifier = Modifier) {
    val iconPath = if (uiColor.currentColor() == LightUiColor) {
        "icon/daytime-mode.svg"
    } else {
        "icon/night-mode.svg"
    }
    val description = if (uiColor.currentColor() == LightUiColor) {
        "切换到夜间模式"
    } else {
        "切换到日间模式"
    }
    Box(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .align(Alignment.CenterEnd)
            .width(32.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 0.dp, 12.dp))
            .focusable(true)
            .clickable {
                if (uiColor.currentColor() == LightUiColor) {
                    uiColor.changeColor(DarkUiColor)
                } else {
                    uiColor.changeColor(LightUiColor)
                }
            }
            .padding(4.dp)
        ) {
            Image(painter = painterResource(iconPath), contentDescription = description)
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectImageItem(modifier: Modifier = Modifier, frameWindowScope: FrameWindowScope, imageBitmap: ImageBitmap?, contentDescription: String, noImageText: String, targetByteArray: KMutableProperty0<ByteArray>) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val transition = updateTransition(isHovered)
    val backgroundColor = transition.animateColor { state ->
        if (state) {
            CurrentUiColor.素材项_指向背景颜色
        } else {
            CurrentUiColor.素材项_默认背景颜色
        }
    }
    val a = UiImageData::colorABackgroundImageData


    var hintText by remember { mutableStateOf(noImageText) }
    var showSelectFileDialog by remember { mutableStateOf(false) }

    if (showSelectFileDialog) {
        fileDialogScope.launch {
            val dialog = ImageFileDialog(frameWindowScope.window, contentDescription, onVisibleChange = { showSelectFileDialog = false }, targetByteArray)
            dialog.isVisible = true
        }
    }

    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(190.dp)
                .background(backgroundColor.value)
                .focusable(true)
                .hoverable(interactionSource)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                .onExternalDrag(true,
                    onDragStart = { },
                    onDragExit = {
                        hintText = noImageText
                        globalHint = "取消了拖拽图片到素材位置的操作"
                    },
                    onDrag = { externalDragValue ->
                        val dragData = externalDragValue.dragData
                        hintText = if (dragData is DragData.Image || dragData is DragData.FilesList) {
                            "放到此处"
                        } else {
                            "你拽的不是图片！"
                        }
                        if (dragData is DragData.Image) {
                            globalHint = "你拖拽了一个图片数据，将放到：${contentDescription}位置"
                        } else if (dragData is DragData.FilesList) {
                            globalHint = "你拖拽了文件数据，将尝试作为图片放到：${contentDescription}位置"

                        }
                    },
                    onDrop = { externalDragValue ->
                        hintText = noImageText
                        val dragData = externalDragValue.dragData
                        if (dragData is DragData.Image) {
                            val image = dragData.readImage().toAwtImage(Density(1f), LayoutDirection.Ltr)
                            val bufferedImage = BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)
                            bufferedImage.graphics.drawImage(image, 0, 0, null)
                            val data = bufferedImage.toImage().encodeToData()?.bytes
                            if (data != null) {
                                targetByteArray.set(data)
                            }
                        } else if (dragData is DragData.FilesList) {
                            val fileList = dragData.readFiles()
                            if (fileList.isEmpty()) return@onExternalDrag
                            val file = File(URI.create(fileList.first()).path.substring(1))
                            file.tryLoadToStock(contentDescription, "拖拽", targetByteArray)
                        }
                    })
                .clickable {
                    if (!showSelectFileDialog) {
                        showSelectFileDialog = true
                    }
                }
        ) {
            imageBitmap?.let {
                Image(BitmapPainter(it), contentDescription, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().weight(1f))
            } ?: run {
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    Text(hintText, color = CurrentUiColor.素材项_文字颜色, modifier = Modifier.align(Alignment.Center), fontSize = 12.sp)
                }
            }
            Text(contentDescription, color = CurrentUiColor.素材项_文字颜色, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        if (targetByteArray == UiImageData::colorABackgroundImageData || targetByteArray == UiImageData::colorBBackgroundImageData) {
            val targetColor: KMutableProperty0<ComputeBackgroundColor> = if (targetByteArray == UiImageData::colorABackgroundImageData) {
                UiImageData::colorA
            } else {
                UiImageData::colorB
            }
            Column(modifier = Modifier.fillMaxHeight().width(IntrinsicSize.Max).background(uiColor.素材项_色彩提示_背景颜色)) {
                Box(modifier = Modifier.fillMaxWidth().background(uiColor.素材项_色彩提示_标签_背景颜色).padding(5.dp)) {
                    Text("背景颜色", color = uiColor.素材项_色彩提示_标签_文字颜色, fontSize = 12.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (targetColor.get() == ComputeBackgroundColor.RED) {
                            uiColor.素材项_色彩选项_红色_选中_背景颜色
                        } else {
                            uiColor.素材项_色彩选项_红色_未选中_背景颜色
                        }
                    )
                    .focusable()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .clickable {
                        targetColor.set(ComputeBackgroundColor.RED)
                    }
                    .padding(5.dp)
                ) {
                    Text("　红色  ", color = Color.White, fontSize = 12.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (targetColor.get() == ComputeBackgroundColor.GREEN) {
                            uiColor.素材项_色彩选项_绿色_选中_背景颜色
                        } else {
                            uiColor.素材项_色彩选项_绿色_未选中_背景颜色
                        }
                    )
                    .focusable()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .clickable {
                        targetColor.set(ComputeBackgroundColor.GREEN)
                    }
                    .padding(5.dp)
                ) {
                    Text("　绿色　", color = Color.Black, fontSize = 12.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (targetColor.get() == ComputeBackgroundColor.BLUE) {
                            uiColor.素材项_色彩选项_蓝色_选中_背景颜色
                        } else {
                            uiColor.素材项_色彩选项_蓝色_未选中_背景颜色
                        }
                    )
                    .focusable()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .clickable {
                        targetColor.set(ComputeBackgroundColor.BLUE)
                    }
                    .padding(5.dp)
                ) {
                    Text("　蓝色　", color = Color.White, fontSize = 12.sp)
                }
            }
        }

    }

}