# DgLabMine

[ [English](Readme-en.md) | 简体中文 ]

一个为 **郊狼** 开发的 Minecraft 模组 - 将电刺激设备集成到你的 Minecraft 游戏中。

## 项目概述

**DgLabMine** 是一个基于 Fabric 框架的 Minecraft 模组，它可以连接郊狼设备与游戏内事件进行交互，体验独特的游戏机制。

### 主要功能

- 🎮 **郊狼设备集成**：通过 WebSocket 与设备无缝连接
- ⚙️ **自定义规则引擎**：基于 JEXL 的灵活规则系统，支持自定义事件触发
- ⚙️ **Cloth Config GUI**：用户友好的配置界面
- 🔧 **基于规则的自定义**：创建复杂的规则来控制设备行为

### Roadmap

- 🎨 **GUI 重构**：计划重构 GUI 来增强易用性，提供更直观的用户界面
- 📊 **波形编辑与导入**：计划添加波形编辑与导入功能，让用户能够自定义和导入自己的波形
- ⚡ **扩展操作功能**：更多的可执行操作，包括波形入队、波形循环播放等高级功能
- 🎮 **郊狼MC物品**：计划添加郊狼MC物品，可以直接在游戏内远程操控设备


## 安装指南

### 系统要求

- Java 21 或更高版本
- Minecraft 1.21.11
- Fabric Loader 0.18.4 或兼容版本
- Fabric API 0.141.2 或更高版本

### 安装步骤

请确保你安装了[Vital Signals](https://github.com/Rainy-day-y/Vital-Signals)，可以从[Release](https://github.com/Rainy-day-y/Vital-Signals/releases)下载安装它，或通过Modrinth搜索下载

#### 从Release下载

在本项目的[release](https://github.com/Rainy-day-y/DgLabMine/releases)中下载文件

#### 编译安装

1. **克隆仓库**
   ```bash
   git clone https://github.com/Rainy-day-y/DgLabMine.git
   cd DgLabMine
   ```

2. **编译模组**
   ```bash
   # Linux/macOS 系统
   ./gradlew build
   
   # Windows 系统
   gradlew.bat build
   ```

3. **安装模组**
   - 找到你的 Minecraft `.minecraft/mods` 文件夹
   - 将编译生成的 JAR 文件从 `build/libs/` 复制到 mods 文件夹
   - 使用 Fabric 配置文件启动 Minecraft

## 配置说明

本模组使用 **Cloth Config** 进行配置。通过以下步骤访问模组设置：

1. 打开 Minecraft
2. 进入存档
3. 点击O键打开设置
4. 根据需要调整设置（可以使用 JEXL 规则自定义行为）

## 依赖库

### 核心依赖
- **Fabric API**：Fabric 的核心 API 库
- **Fabric Language Kotlin**：Fabric 模组的 Kotlin 支持
- **Cloth Config API**：配置 GUI 框架
- **Vital Signals**：提供伤害信息

### 额外库
- **Java-WebSocket** (1.6.0)：与 DgLab 设备进行 WebSocket 通信
- **Apache Commons JEXL3** (3.6.1)：规则引擎的表达式语言
- **ZXing** (3.5.4)：二维码生成和扫描

## 开发指南

### 项目结构

```
DgLabMine/
├── src/
│   ├── main/          # 主模组代码（Kotlin/Java）
│   └── client/        # 客户端代码
├── build.gradle       # Gradle 构建配置
├── gradle.properties  # 项目属性和版本信息
└── LICENSE           # 许可证文件
```

### 从源码编译

```bash
# 清空并重新编译
./gradlew clean build

# 刷新依赖并编译
./gradlew build --refresh-dependencies

# 在开发环境运行 Minecraft
./gradlew runClient
```

## 功能详解

### WebSocket 通信
模组使用 Java-WebSocket 库与 DgLab 设备建立实时通信连接，在游戏过程中实现动态交互。

### 规则引擎
由 Apache Commons JEXL3 驱动的规则引擎，允许你创建复杂的条件逻辑，根据游戏内事件触发设备操作。

### 二维码支持
使用 ZXing 库内置的二维码生成和扫描功能，方便设备配对和快速配置。

## 贡献指南

欢迎贡献！如果你想参与开发：

1. Fork 本仓库
2. 创建功能分支（`git checkout -b feature/AmazingFeature`）
3. 提交你的改动（`git commit -m 'Add some AmazingFeature'`）
4. 推送到分支（`git push origin feature/AmazingFeature`）
5. 提交 Pull Request

## 许可证

本项目采用 [MIT 许可证](LICENSE)。

## 反馈与问题

如遇到问题或有功能建议，请在 [GitHub Issues](https://github.com/Rainy-day-y/DgLabMine/issues) 页面提出。

## 致谢

- **开发者**：Rainy-day-y
- **技术栈**：Fabric、Minecraft、Kotlin
- **特别感谢**：DgLab、Cloth Config

## 相关链接

- 🔗 [GitHub 仓库](https://github.com/Rainy-day-y/DgLabMine)
- 📝 [DgLab 官方网站](https://dungeon-lab.com/home.php)
- 🎮 [Minecraft Fabric](https://fabricmc.net/)
- 📖 [Fabric 文档](https://fabricmc.net/wiki)

---

**免责声明**：本模组仅供娱乐目的使用。使用前请确保合法拥有必要的设备，并遵守所有安全指南。