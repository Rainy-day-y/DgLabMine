# DgLabMine

[ English | [简体中文](Readme.md) ]

A Minecraft mod developed for **DgLab Coyote** – Integrating electrostimulation devices into your Minecraft gameplay.

## Project Overview

**DgLabMine** is a Fabric-based Minecraft mod that connects DgLab Coyote devices with in-game events for a unique gameplay experience.

### Key Features

- 🎮 **DgLab Coyote Device Integration**: Seamless WebSocket connection with devices
- ⚙️ **Custom Rule Engine**: Flexible JEXL-based rule system supporting custom event triggers
- ⚙️ **Cloth Config GUI**: User-friendly configuration interface
- 🔧 **Rule-Based Customization**: Create complex rules to control device behavior

### Roadmap

- 🎨 **GUI Refactoring**: Planned GUI overhaul to enhance usability and provide a more intuitive interface
- 📊 **Waveform Editing & Import**: Planned waveform editing and import functionality for custom waveforms
- ⚡ **Extended Operations**: More executable operations including waveform queuing, loop playback, and other advanced features
- 🎮 **DgLab Coyote Minecraft Items**: Planned in-game DgLab Coyote items for remote device control

## Installation Guide

### System Requirements

- Java 21 or higher
- Minecraft 1.21.11
- Fabric Loader 0.18.4 or compatible version
- Fabric API 0.141.2 or higher

### Installation Steps

Ensure you have [Vital Signals](https://github.com/Rainy-day-y/Vital-Signals) installed. Download it from [Releases](https://github.com/Rainy-day-y/Vital-Signals/releases).

You can also find it on Modrinth by searching for "Vital Signals."

#### Download from Releases

Download the file from this project's [releases](https://github.com/Rainy-day-y/DgLabMine/releases).

#### Build from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/Rainy-day-y/DgLabMine.git
   cd DgLabMine
   ```

2. **Build the mod**
   ```bash
   # Linux/macOS
   ./gradlew build
   
   # Windows
   gradlew.bat build
   ```

3. **Install the mod**
    - Locate your Minecraft `.minecraft/mods` folder
    - Copy the compiled JAR file from `build/libs/` to the mods folder
    - Launch Minecraft with the Fabric profile

## Configuration

This mod uses **Cloth Config** for configuration. Access mod settings as follows:

1. Launch Minecraft
2. Enter a world
3. Press the “O” key to open settings
4. Adjust settings as needed (customize behavior using JEXL rules)
5. Press the “I” key to open the QR code and use the DgLab app WebSocket scanner to connect~

## Dependencies

### Core Dependencies
- **Fabric API**: Core API library for Fabric
- **Fabric Language Kotlin**: Kotlin support for Fabric mods
- **Cloth Config API**: Configuration GUI framework
- **Vital Signals**: Provides damage information

### Additional Libraries
- **Java-WebSocket** (1.6.0): WebSocket communication with DgLab devices
- **Apache Commons JEXL3** (3.6.1): Expression language for the rule engine
- **ZXing** (3.5.4): QR code generation and scanning

## Development Guide

### Project Structure

```
DgLabMine/
├── src/
│   ├── main/          # Main mod code (Kotlin/Java)
│   └── client/        # Client code
├── build.gradle       # Gradle build configuration
├── gradle.properties  # Project properties and version info
└── LICENSE           # License file
```

### Building from Source

```bash
# Clean and rebuild
./gradlew clean build

# Refresh dependencies and build
./gradlew build --refresh-dependencies

# Run Minecraft in development environment
./gradlew runClient
```

## Feature Details

### WebSocket Communication
The mod uses the Java-WebSocket library to establish real-time communication with DgLab Coyote devices, enabling dynamic interaction during gameplay.

### Rule Engine
Powered by Apache Commons JEXL3, the rule engine allows you to create complex conditional logic that triggers device operations based on in-game events.

### QR Code Support
Built-in QR code generation and scanning functionality using the ZXing library for easy device pairing and quick configuration.

## Contributing

Contributions are welcome! To participate in development:

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Submit a Pull Request

## License

This project is licensed under the [MIT License](LICENSE).

## Feedback & Issues

For issues or feature suggestions, please submit them on [GitHub Issues](https://github.com/Rainy-day-y/DgLabMine/issues).

## Acknowledgements

- **Developer**: Rainy-day-y
- **Tech Stack**: Fabric, Minecraft, Kotlin
- **Special Thanks**: DgLab, Cloth Config

## Related Links

- 🔗 [GitHub Repository](https://github.com/Rainy-day-y/DgLabMine)
- 📝 [DgLab Official Website](https://dungeon-lab.com/home.php)
- 🎮 [Minecraft Fabric](https://fabricmc.net/)
- 📖 [Fabric Documentation](https://fabricmc.net/wiki)

---

**Disclaimer**: This mod is for entertainment purposes only. Please ensure you legally own the necessary equipment and follow all safety guidelines before use.