package cn.sweetberry.mcmod.dglab.screen

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.min

class QRScreen private constructor(
    private val data: String,
    private val titleText: Text
) : Screen(titleText) {

    companion object {
        fun open(data: String, titleKey: String = "Scan QR Code") {
            MinecraftClient.getInstance().setScreen(
                QRScreen(data, Text.literal(titleKey))
            )
        }
    }

    private lateinit var textureId: Identifier
    private lateinit var texture: NativeImageBackedTexture
    private var qrSize: Int = 256

    override fun init() {
        super.init()

        qrSize = (min(width, height) * 0.6f).toInt()
        generateTexture()

        addDrawableChild(
            ButtonWidget.builder(Text.literal("Close")) { close() }
                .dimensions(width / 2 - 50, height - 40, 100, 20)
                .build()
        )
    }

    private fun generateTexture() {
        val matrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, qrSize, qrSize)
        val image = NativeImage(qrSize, qrSize, true)

        for (x in 0 until qrSize) {
            for (y in 0 until qrSize) {
                val color = if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                image.setColor(x, y, color)
            }
        }

        texture = NativeImageBackedTexture({ "qr_${data.hashCode()}" }, image)
        textureId = Identifier.of("dglab-minecraft", "qr_${data.hashCode()}")
        MinecraftClient.getInstance().textureManager.registerTexture(textureId, texture)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // 背景
        context.fill(0, 0, width, height, 0xAA000000.toInt())

        val qrX = (width - qrSize) / 2
        val qrY = (height - qrSize) / 2

        // 二维码
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            textureId,
            qrX,
            qrY,
            0f,
            0f,
            qrSize,
            qrSize,
            qrSize,
            qrSize
        )

        // 计算位置
        val titleY = (qrY - 20).coerceAtLeast(10)
        val displayText = if (data.length > 60) data.substring(0, 57) + "..." else data
        val textY = (qrY + qrSize + 10).coerceAtMost(height - 50)

        // 绘制文本 - 使用 drawCenteredTextWithShadow
        context.drawCenteredTextWithShadow(textRenderer, titleText, width / 2, titleY, -1)  // -1 = 白色 (ARGB格式)
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(displayText), width / 2, textY, 0xCCCCCC or 0xFF000000.toInt())  // 添加 Alpha

        // 按钮
        super.render(context, mouseX, mouseY, delta)
    }

    override fun close() {
        MinecraftClient.getInstance().textureManager.destroyTexture(textureId)
        super.close()
    }
}