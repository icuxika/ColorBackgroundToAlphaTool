package studio.attect.tool

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.KMutableProperty0

/**
 * 选择图片对话框<br>
 * 用于选择单张图片，不限制扩展名
 */
class ImageFileDialog(parent: Frame, val isSave: Boolean = false, val imageItemName: String, val onVisibleChange: (Boolean) -> Unit, val targetByteArray: KMutableProperty0<ByteArray>) : FileDialog(parent, imageItemName, LOAD) {

    init {
        isMultipleMode = false
        if (isSave) {
            mode = SAVE
            file = "图片.png"
        } else {
            mode = LOAD
        }
    }

    override fun setVisible(b: Boolean) {
        super.setVisible(b)
        onVisibleChange(b)
        if(!b){
            dispose()
        }
    }

    override fun dispose() {
        super.dispose()
        directory?.let { dir ->
            file?.let { filename ->
                if (isSave) {
                    var saveName = filename
                    saveName = saveName.substringBeforeLast(".") + ".png"
                    val file = File(dir + saveName)
                    val result = runCatching {
                        val fileOutputStream = FileOutputStream(file)
                        fileOutputStream.write(targetByteArray.get())
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    }
                    globalHint = if (result.isSuccess) {
                        "成功保存计算结果到：" + file.absolutePath
                    } else {
                        "保存失败，请选择其它位置或文件名"
                    }

                } else {
                    val file = File(dir + filename)
                    file.tryLoadToStock(imageItemName, "选择", targetByteArray)
                }
            }
        }
    }
}

fun File.tryLoadToStock(imageItemName: String,actionName:String,targetByteArray:KMutableProperty0<ByteArray>){
    if (!exists()) {
        globalHint = "${actionName}的文件不存在：${absolutePath}"
        return
    }
    if (!canRead()) {
        globalHint = "${actionName}的文件无法读取，可能被占用或没有权限：${absolutePath}"
        return
    }
    if (length() == 0L) {
        globalHint = "${actionName}的文件没有内容：${absolutePath}"
        return
    }
    if (!noImageFileSizeLimit && length() > 200*1024*1024){
        globalHint = "${actionName}的文件大于200MiB，超过限制。如果内存足够，修改设置或使用--no-image-file-size-limit参数运行本程序以解除大小限制"
    }
    val result = runCatching {
        targetByteArray.set(readBytes())
    }
    globalHint = if (result.isSuccess) {
        "${imageItemName}位置使用图片文件：${absolutePath}"
    } else {
        "无法作为图片处理，或格式不支持：${absolutePath}"
    }
}