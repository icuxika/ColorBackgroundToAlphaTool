@file:Suppress("FunctionName")

package studio.attect.tool

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skiko.toImage
import studio.attect.tool.ComputeBackgroundColor.*
import java.awt.Cursor
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(frameWindowScope: FrameWindowScope) {
    MaterialTheme {
        Column(Modifier.fillMaxSize().background(CurrentUiColor.窗口_背景颜色)) {
            Box(Modifier.fillMaxWidth().weight(1f).background(CurrentUiColor.预览区_背景颜色)) {
                Box(modifier = Modifier.fillMaxSize().width(IntrinsicSize.Max).height(IntrinsicSize.Max)) {
                    val currentImage = UiImageData.previewImage
                    if (currentImage == null) {
                        Text("请按底部提示提供素材", color = uiColor.预览区_文字颜色, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    } else {
                        //画自定义背景
                        UiImageData.previewBackgroundImage?.let { backgroundImage ->
                            Image(backgroundImage, "自定义预览背景", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                        }

                        //预览图片的一些鼠标操作
                        var boxModifier = Modifier
                            .fillMaxSize()
                            .onDrag { offset ->
                                UiImageData.previewImageOffsetX += offset.x
                                UiImageData.previewImageOffsetY += offset.y
                            }
                        if (UiImageData.previewBackgroundImage == null) {
                            boxModifier = boxModifier.drawBehind {
                                val xCount = ceil(size.width / 10).toInt()
                                val yCount = ceil(size.height / 10).toInt()
                                repeat(yCount) { y ->
                                    repeat(xCount) { x ->
                                        if ((x + y) % 2 == 0) {
                                            drawRect(uiColor.预览区_格子图_差色, Offset((x * 10).toFloat(), (y * 10).toFloat()), Size(10f, 10f))
                                        } else {
                                            drawRect(uiColor.预览区_格子图_底色, Offset((x * 10).toFloat(), (y * 10).toFloat()), Size(10f, 10f))
                                        }
                                    }
                                }
                            }
                        }
                        boxModifier = boxModifier.onPointerEvent(PointerEventType.Move) {
                                if (UiImageData.previewMouseRotationLock) {
                                    if (UiImageData.previewMouseRotationStartX != Float.MIN_VALUE) {
                                        UiImageData.previewImageRotate += UiImageData.previewMouseRotationStartX - it.changes.first().position.x
                                    }

                                    UiImageData.previewMouseRotationStartX = it.changes.first().position.x
                                }
                            }
                            .onPointerEvent(PointerEventType.Press) {
                                it.button?.let { pointerButton ->
                                    if (pointerButton == PointerButton.Secondary) {
                                        UiImageData.previewMouseRotationLock = true
                                        UiImageData.previewMouseRotationStartX = Float.MIN_VALUE
                                    }
                                }
                            }
                            .onPointerEvent(PointerEventType.Release) {
                                it.button?.let { pointerButton ->
                                    if (pointerButton == PointerButton.Tertiary) {
                                        UiImageData.resetPreview()
                                    } else if (pointerButton == PointerButton.Secondary) {
                                        UiImageData.previewMouseRotationLock = false
                                    }
                                }
                            }
                            .onPointerEvent(PointerEventType.Scroll) {
                                it.changes.firstOrNull()?.scrollDelta?.y?.let { y ->
                                    if (y < 0 && UiImageData.previewImageScale < 5f) {
                                        UiImageData.previewImageScale += 0.05f
                                    } else if (y > 0 && UiImageData.previewImageScale > 0.05f) {
                                        UiImageData.previewImageScale -= 0.05f
                                    }
                                }
                            }
                        Box(modifier = boxModifier) {
                            Box(modifier = Modifier.fillMaxSize()
                                .scale(UiImageData.previewImageScale)
                                .offset {
                                    IntOffset((UiImageData.previewImageOffsetX / UiImageData.previewImageScale).toInt(), (UiImageData.previewImageOffsetY / UiImageData.previewImageScale).toInt())
                                }) {

                                Image(
                                    currentImage, contentDescription = "预览图片", contentScale = ContentScale.Inside, modifier = Modifier
                                        .fillMaxSize()
                                        .rotate(UiImageData.previewImageRotate)
                                )
                            }
                        }

                    }

                }
                SlideOptionPanel(Modifier.align(Alignment.CenterEnd))
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(uiColor.分割线颜色))
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                SourceItemPanel(frameWindowScope, modifier = Modifier.weight(1f))
                ActionPanel(modifier = Modifier.fillMaxHeight(), frameWindowScope)
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(uiColor.分割线颜色))
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

    val icon = painterResource("icon/app.png")

    CurrentUiColor.init()
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "差色图透明工具",
        icon = icon
    ) {
        App(this)
    }
}

/**
 * 素材面板
 */
@Composable
fun SourceItemPanel(frameWindowScope: FrameWindowScope, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(200.dp).background(CurrentUiColor.素材区_背景颜色)) {
        val scrollState = rememberScrollState()
        Row(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)) {
            SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.whiteBackgroundImage, contentDescription = "白色背景图片", noImageText = "点击添加白色背景图片", targetByteArray = UiImageData::whiteBackgroundImageData)
            SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.blackBackgroundImage, contentDescription = "黑色背景图片", noImageText = "点击添加黑色背景图片", targetByteArray = UiImageData::blackBackgroundImageData)
            SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.colorABackgroundImage, contentDescription = "纯色A背景图片", noImageText = "点击添加纯色A背景图片", targetByteArray = UiImageData::colorABackgroundImageData)
            SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.colorBBackgroundImage, contentDescription = "纯色B背景图片", noImageText = "点击添加纯色B背景图片", targetByteArray = UiImageData::colorBBackgroundImageData)
            SelectImageItem(frameWindowScope = frameWindowScope, imageBitmap = UiImageData.previewBackgroundImage, contentDescription = "预览区背景图片", noImageText = "默认格子图背景", targetByteArray = UiImageData::previewBackgroundImageData)
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
}

/**
 * 操作面板
 */
@Composable
fun ActionPanel(modifier: Modifier = Modifier, frameWindowScope: FrameWindowScope) {
    Column(modifier.background(uiColor.操作面板_背景颜色)) {
        DayNightModeSwitchButton(modifier = Modifier.align(Alignment.End))
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = {
                UiImageData.compute()
            }) {
                Text("计算")
            }

            var showSaveFileDialog by remember { mutableStateOf(false) }

            if (showSaveFileDialog) {
                fileDialogScope.launch {
                    val dialog = ImageFileDialog(frameWindowScope.window, true, "保存计算结果", onVisibleChange = { showSaveFileDialog = false }, UiImageData::previewImageData)
                    dialog.isVisible = true
                }
            }
            Button(onClick = {
                if (!showSaveFileDialog) {
                    showSaveFileDialog = true
                }
            }, enabled = UiImageData.previewImage != null) {
                Text("保存")
            }
        }
    }
}

@Composable
fun SlideOptionPanel(modifier: Modifier = Modifier) {
    var isShowing by remember { mutableStateOf(false) }
    val transition = updateTransition(isShowing)
    val offset by transition.animateDp {
        if (it) {
            0.dp
        } else {
            244.dp
        }
    }
    Row(modifier.fillMaxHeight().offset(x = offset)) {
        Box(modifier = Modifier
            .align(Alignment.CenterVertically)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(uiColor.配置面板_触发按钮_背景颜色)
            .focusable(true)
            .clickable { isShowing = !isShowing }
        ) {
            VerticalText("细节调整", Modifier.padding(8.dp), fontSize = 12.sp)
        }
        Box(Modifier.width(8.dp).height(8.dp)) {
            //占位
        }

        OptionPanel(Modifier.width(240.dp).fillMaxHeight())
    }
}

@Composable
fun VerticalText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Column(modifier) {
        text.forEach { character ->
            Text(
                text = character.toString(),
                modifier = Modifier,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                onTextLayout = onTextLayout,
                style = style
            )
        }
    }
}

@Composable
fun TolerancePanel(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, shape = RoundedCornerShape(8.dp))
                .background(uiColor.配置面板_背景颜色)
                .padding(8.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
        ) {
            Text("容差 ${String.format("%.0f", UiImageData.colorBackgroundTolerance * 100)}", color = uiColor.配置面板_标签_文字颜色, fontSize = 14.sp)
            Slider(
                value = UiImageData.colorBackgroundTolerance,
                onValueChange = {
                    UiImageData.colorBackgroundTolerance = it
                },
                colors = SliderDefaults.colors(
                    thumbColor = uiColor.配置面板_滑杆_手柄颜色,
                    activeTrackColor = uiColor.配置面板_滑杆_有效条颜色,
                    inactiveTrackColor = uiColor.配置面板_滑杆_无效条颜色,
                )
            )
        }
    }
}

@Composable
fun WhiteBlackBalancePanel(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, shape = RoundedCornerShape(8.dp))
                .background(uiColor.配置面板_背景颜色)
                .padding(8.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
        ) {
            Row {
                Text("黑白底色平衡:${String.format("%.2f", UiImageData.whiteBlackBalance)}", color = uiColor.配置面板_标签_文字颜色, fontSize = 14.sp)
                Text("重置", color = uiColor.配置面板_标签_可点击_文字颜色, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    UiImageData.whiteBlackBalance = 0.5f
                })
            }
            Slider(
                value = UiImageData.whiteBlackBalance,
                onValueChange = {
                    UiImageData.whiteBlackBalance = it
                },
                colors = SliderDefaults.colors(
                    thumbColor = uiColor.配置面板_滑杆_手柄颜色,
                    activeTrackColor = uiColor.配置面板_滑杆_有效条颜色,
                    inactiveTrackColor = uiColor.配置面板_滑杆_无效条颜色,
                )
            )
        }
    }
}

/**
 * 选项面板
 */
@Composable
fun OptionPanel(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier.verticalScroll(scrollState)
    ) {
        TolerancePanel()
        WhiteBlackBalancePanel()

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
    Box(modifier = modifier) {
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
    var hintText by remember { mutableStateOf(noImageText) }
    var showSelectFileDialog by remember { mutableStateOf(false) }

    if (showSelectFileDialog) {
        fileDialogScope.launch {
            val dialog = ImageFileDialog(frameWindowScope.window, false, contentDescription, onVisibleChange = { showSelectFileDialog = false }, targetByteArray)
            dialog.isVisible = true
        }
    }

    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp), spotColor = uiColor.素材项_阴影颜色)
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
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Image(
                        BitmapPainter(it), contentDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            .background(
                                when (targetByteArray) {
                                    UiImageData::whiteBackgroundImageData -> Color.White
                                    UiImageData::blackBackgroundImageData -> Color.Black
                                    UiImageData::colorABackgroundImageData -> {
                                        when (UiImageData.colorA) {
                                            RED -> Color.Red
                                            GREEN -> Color.Green
                                            BLUE -> Color.Blue
                                        }
                                    }

                                    UiImageData::colorBBackgroundImageData -> {
                                        when (UiImageData.colorB) {
                                            RED -> Color.Red
                                            GREEN -> Color.Green
                                            BLUE -> Color.Blue
                                        }
                                    }
                                    UiImageData::previewBackgroundImageData -> {
                                        Color.White
                                    }

                                    else -> Color.Transparent
                                }
                            )
                    )

                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(0.dp, 0.dp, 6.dp, 0.dp))
                        .background(Color.White.copy(alpha = .5f))
                        .width(18.dp).height(18.dp)
                        .clickable {
                            targetByteArray.set(ByteArray(0))
                        }
                        .padding(2.dp)) {
                        Image(painter = painterResource("icon/delete.svg"), contentDescription = "清除")
                    }
                }

            } ?: run {
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    Text(hintText, color = CurrentUiColor.素材项_文字颜色, modifier = Modifier.align(Alignment.Center), fontSize = 12.sp)
                }
            }
            Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp, bottom = 4.dp)) {
                Text(contentDescription, color = CurrentUiColor.素材项_文字颜色, fontSize = 12.sp)
            }
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
                        if (targetColor.get() == RED) {
                            uiColor.素材项_色彩选项_红色_选中_背景颜色
                        } else {
                            uiColor.素材项_色彩选项_红色_未选中_背景颜色
                        }
                    )
                    .focusable()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .clickable {
                        targetColor.set(RED)
                    }
                    .padding(5.dp)
                ) {
                    Text("　红色  ", color = Color.White, fontSize = 12.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (targetColor.get() == GREEN) {
                            uiColor.素材项_色彩选项_绿色_选中_背景颜色
                        } else {
                            uiColor.素材项_色彩选项_绿色_未选中_背景颜色
                        }
                    )
                    .focusable()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .clickable {
                        targetColor.set(GREEN)
                    }
                    .padding(5.dp)
                ) {
                    Text("　绿色　", color = Color.Black, fontSize = 12.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (targetColor.get() == BLUE) {
                            uiColor.素材项_色彩选项_蓝色_选中_背景颜色
                        } else {
                            uiColor.素材项_色彩选项_蓝色_未选中_背景颜色
                        }
                    )
                    .focusable()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .clickable {
                        targetColor.set(BLUE)
                    }
                    .padding(5.dp)
                ) {
                    Text("　蓝色　", color = Color.White, fontSize = 12.sp)
                }
            }
        }

    }

}