package cn.sweetberry.mcmod.dglab.screen

import cn.sweetberry.mcmod.dglab.config.ModConfig
import cn.sweetberry.mcmod.dglab.config.ServerConfig
import cn.sweetberry.mcmod.dglab.manager.ConnectionManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

/**
 * DgLab 连接管理界面
 * 左右布局：左侧配置，右侧二维码
 */
class ConnectionScreen : Screen(Text.translatable("gui.dglab-minecraft.connection.title")) {

    companion object {
        fun open() {
            MinecraftClient.getInstance().setScreen(ConnectionScreen())
        }

        private const val QR_CODE_SIZE = 120
        private const val PANEL_WIDTH = 140

        private fun getServerConfig(): ServerConfig {
            return AutoConfig.getConfigHolder(ModConfig::class.java).getConfig().serverConfig
        }
    }

    private val logger = LoggerFactory.getLogger(ConnectionScreen::class.java)

    private var editingType = getServerConfig().serverType
    private var editingAddress = getServerConfig().serverAddress
    private var editingPort = getServerConfig().serverPort

    private var currentType = getServerConfig().serverType
    private var currentAddress = getServerConfig().serverAddress
    private var currentPort = getServerConfig().serverPort

    private lateinit var qrTextureId: Identifier
    private lateinit var qrTexture: NativeImageBackedTexture

    private lateinit var addressField: TextFieldWidget
    private lateinit var portField: TextFieldWidget
    private lateinit var applyButton: ButtonWidget
    private lateinit var closeButton: ButtonWidget
    private lateinit var typeButton: ButtonWidget

    private val isConfigApplied: Boolean
        get() = editingType == currentType && editingAddress == currentAddress && editingPort == currentPort

    // 检查端口输入是否合法
    private fun isPortValid(): Boolean {
        val portText = portField.text
        val port = portText.toIntOrNull()
        return port != null && port in 1..65535
    }

    override fun init() {
        super.init()

        val totalWidth = PANEL_WIDTH * 2 + 20
        val startX = (width - totalWidth) / 2
        val startY = 40

        // ========== 左侧：配置面板 ==========
        var currentY = startY

        // 类型选择
        typeButton = ButtonWidget.builder(getTypeButtonText()) { _ ->
            cycleServerType()
        }
            .dimensions(startX, currentY, PANEL_WIDTH, 20)
            .build()
        addDrawableChild(typeButton)
        currentY += 35

        // 地址输入
        addressField = TextFieldWidget(
            textRenderer,
            startX,
            currentY,
            PANEL_WIDTH,
            20,
            Text.translatable("gui.dglab-minecraft.connection.server_address")
        )
        addressField.setMaxLength(256)
        addressField.text = editingAddress
        addressField.setChangedListener { newText ->
            editingAddress = newText
            updateApplyButtonState()
        }
        // 使用 addDrawableChild 以便渲染和交互
        addDrawableChild(addressField)
        currentY += 35

        // 端口输入
        portField = TextFieldWidget(
            textRenderer,
            startX,
            currentY,
            PANEL_WIDTH,
            20,
            Text.translatable("gui.dglab-minecraft.connection.server_port")
        )
        portField.setMaxLength(5)
        portField.text = editingPort.toString()
        portField.setChangedListener { newText ->
            newText.toIntOrNull()?.let { port ->
                if (port in 1..65535) {
                    editingPort = port
                }
            }
            updateApplyButtonState()
        }
        addDrawableChild(portField)
        currentY += 40

        // 应用按钮
        applyButton = ButtonWidget.builder(Text.translatable("gui.dglab-minecraft.connection.apply_restart")) { _ ->
            applyConfiguration()
        }
            .dimensions(startX, currentY, PANEL_WIDTH, 20)
            .build()
        addDrawableChild(applyButton)
        currentY += 28

        // 关闭按钮
        closeButton = ButtonWidget.builder(Text.translatable("gui.dglab-minecraft.connection.close")) { _ ->
            close()
        }
            .dimensions(startX, currentY, PANEL_WIDTH, 20)
            .build()
        addDrawableChild(closeButton)

        // ========== 右侧：二维码面板 ==========
        generateQRCode()
    }

    private fun getTypeButtonText(): Text {
        return Text.literal(editingType.name)
    }

    private fun cycleServerType() {
        val types = ServerConfig.ServerType.entries.toTypedArray()
        val currentIndex = types.indexOf(editingType)
        editingType = types[(currentIndex + 1) % types.size]
        typeButton.message = getTypeButtonText()
        updateApplyButtonState()
    }

    private fun updateApplyButtonState() {
        val canApply = !isConfigApplied && ConnectionManager.isModeAvailable(editingType) && isPortValid()
        applyButton.active = canApply
    }

    private fun applyConfiguration() {
        applyButton.active = false
        applyButton.message = Text.translatable("gui.dglab-minecraft.connection.applying")

        ConnectionManager.applyConfigAndRestart(editingType, editingAddress, editingPort) { success ->
            if (success) {
                currentType = editingType
                currentAddress = editingAddress
                currentPort = editingPort
                regenerateQRCode()
                applyButton.message = Text.translatable("gui.dglab-minecraft.connection.apply_restart")
                updateApplyButtonState()
                ConnectionManager.sendMessage(Text.translatable("gui.dglab-minecraft.connection.apply_success"))
            } else {
                applyButton.message = Text.translatable("gui.dglab-minecraft.connection.apply_restart")
                ConnectionManager.sendMessage(Text.translatable("gui.dglab-minecraft.connection.apply_failed"))
                updateApplyButtonState()
            }
        }
    }

    private fun generateQRCode() {
        val link = ConnectionManager.getCurrentLink()
            ?: ConnectionManager.generatePreviewLinkFromCurrentConfig()
            ?: return

        try {
            val matrix = QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE)
            val image = NativeImage(QR_CODE_SIZE, QR_CODE_SIZE, true)
            for (x in 0 until QR_CODE_SIZE) {
                for (y in 0 until QR_CODE_SIZE) {
                    val color = if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                    image.setColor(x, y, color)
                }
            }
            qrTexture = NativeImageBackedTexture({ "qr_${link.hashCode()}" }, image)
            qrTextureId = Identifier.of("dglab-minecraft", "qr_${link.hashCode()}")
            MinecraftClient.getInstance().textureManager.registerTexture(qrTextureId, qrTexture)
        } catch (e: Exception) {
            // 生成失败
            logger.error("QR code couldn't be created: {}", e.message)
        }
    }

    private fun regenerateQRCode() {
        if (::qrTextureId.isInitialized) {
            MinecraftClient.getInstance().textureManager.destroyTexture(qrTextureId)
        }
        generateQRCode()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val totalWidth = PANEL_WIDTH * 2 + 20
        val startX = (width - totalWidth) / 2
        val startY = 40
        val qrX = startX + PANEL_WIDTH + 20

        // 标题
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, -1)

        // ========== 左侧面板标签 ==========
        context.drawTextWithShadow(textRenderer, Text.translatable("gui.dglab-minecraft.connection.label.type"), startX, startY - 10, 0xFFAAAAAA.toInt())
        context.drawTextWithShadow(textRenderer, Text.translatable("gui.dglab-minecraft.connection.label.address"), startX, startY + 25, 0xFFAAAAAA.toInt())
        context.drawTextWithShadow(textRenderer, Text.translatable("gui.dglab-minecraft.connection.label.port"), startX, startY + 60, 0xFFAAAAAA.toInt())

        // 端口输入不合法时显示警告
        if (!isPortValid()) {
            context.drawTextWithShadow(
                textRenderer,
                Text.translatable("gui.dglab-minecraft.connection.port_invalid"),
                startX,
                startY + 95,
                0xFFFF5555.toInt()
            )
        }

        // ========== 右侧：二维码区域 ==========
        val qrY = startY + 10

        // 标签
        val label = if (isConfigApplied)
            Text.translatable("gui.dglab-minecraft.connection.current_config")
        else
            Text.translatable("gui.dglab-minecraft.connection.preview_config")
        context.drawTextWithShadow(
            textRenderer, label,
            qrX + (QR_CODE_SIZE - textRenderer.getWidth(label)) / 2,
            qrY - 12,
            if (isConfigApplied) 0xFF55FF55.toInt() else 0xFFFFAA55.toInt()
        )

        // 绘制二维码
        if (::qrTextureId.isInitialized) {
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                qrTextureId,
                qrX, qrY,
                0f, 0f,
                QR_CODE_SIZE, QR_CODE_SIZE,
                QR_CODE_SIZE, QR_CODE_SIZE
            )

            // 未生效时添加遮罩
            if (!isConfigApplied) {
                context.fill(qrX, qrY, qrX + QR_CODE_SIZE, qrY + QR_CODE_SIZE, 0xCC000000.toInt())

                val hintText = Text.translatable("gui.dglab-minecraft.connection.mask.not_applied")
                val hintX = qrX + (QR_CODE_SIZE - textRenderer.getWidth(hintText)) / 2
                val hintY = qrY + QR_CODE_SIZE / 2 - 10
                context.drawTextWithShadow(textRenderer, hintText, hintX, hintY, 0xFFFFAA55.toInt())

                val subHintText = Text.translatable("gui.dglab-minecraft.connection.mask.click_apply")
                val subHintX = qrX + (QR_CODE_SIZE - textRenderer.getWidth(subHintText)) / 2
                context.drawTextWithShadow(textRenderer, subHintText, subHintX, hintY + 12, 0xFFAAAAAA.toInt())
            }
        }

        // ========== 警告/状态区域 ==========
        val infoY = qrY + QR_CODE_SIZE + 12

        // 如果是非LOCAL模式，显示警告；否则显示状态
        if (!ConnectionManager.isModeAvailable(editingType)) {
            // 分两行显示警告
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("gui.dglab-minecraft.connection.warning.line1"),
                qrX + QR_CODE_SIZE / 2,
                infoY,
                0xFFFF5555.toInt()
            )
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("gui.dglab-minecraft.connection.warning.line2"),
                qrX + QR_CODE_SIZE / 2,
                infoY + 10,
                0xFFFF5555.toInt()
            )
        } else {
            // 状态
            val statusText = if (ConnectionManager.isServiceRunning())
                Text.translatable("gui.dglab-minecraft.connection.running")
            else
                Text.translatable("gui.dglab-minecraft.connection.stopped")
            val statusColor = if (ConnectionManager.isServiceRunning()) 0xFF55FF55.toInt() else 0xFFFF5555.toInt()
            context.drawTextWithShadow(
                textRenderer, statusText,
                qrX + (QR_CODE_SIZE - textRenderer.getWidth(statusText)) / 2,
                infoY,
                if (isConfigApplied) statusColor else 0xFF888888.toInt()
            )
        }
    }

    override fun close() {
        if (::qrTextureId.isInitialized) {
            MinecraftClient.getInstance().textureManager.destroyTexture(qrTextureId)
        }
        super.close()
    }
}
