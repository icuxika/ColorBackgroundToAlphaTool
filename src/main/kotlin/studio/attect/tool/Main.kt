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
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import org.jetbrains.skiko.toImage
import java.awt.Cursor
import java.awt.FileDialog
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
        Column(Modifier.fillMaxSize().background(CurrentUiColor.窗口背景颜色)) {
            Row(Modifier.fillMaxWidth().weight(1f).background(CurrentUiColor.预览区背景颜色)) {
                Text("test1")
                Button(onClick = {
                    CurrentUiColor.changeColor(LightUiColor)
                }) {
                    Text("Light")
                }
                Button(onClick = {
                    CurrentUiColor.changeColor(DarkUiColor)
                }) {
                    Text("Dark")
                }
            }
            Box(modifier = Modifier.height(200.dp).background(CurrentUiColor.素材区背景颜色)) {
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
                        unhoverColor = CurrentUiColor.素材区滚动条颜色,
                        hoverColor = CurrentUiColor.素材区滚动条激活颜色
                    )
                )
            }
            Row(modifier = Modifier.fillMaxWidth().background(CurrentUiColor.底部提示条背景颜色).padding(5.dp)) {
                Text(globalHint, color = CurrentUiColor.底部提示条文字颜色)
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

    UiImageData.whiteBackgroundImageData = FileInputStream(File("F:/cap/1_white.png")).readAllBytes()
    UiImageData.blackBackgroundImageData = FileInputStream(File("F:/cap/1_black.png")).readAllBytes()

    CurrentUiColor.init()
    Window(state = windowState, onCloseRequest = ::exitApplication) {
        App(this)
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
            CurrentUiColor.素材项指向背景颜色
        } else {
            CurrentUiColor.素材项默认背景颜色
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

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(190.dp)
            .padding(5.dp)
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
                Text(hintText, color = CurrentUiColor.素材项文字颜色, modifier = Modifier.align(Alignment.Center))
            }
        }
        Text(contentDescription, color = CurrentUiColor.素材项文字颜色, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}