# UDP Java: Core

Unknown Detection Party's core library for editing Minecraft written in Java. The core comes with a powerful mapping API that allows you to easily make facade's for minecraft classes and reference them just like you would in a typical forge mod. Compiling is easy and handled by maven, you can find further instruction on compilation and usage below.

***

## Getting Started

UDP-Java is fairly easy to set up as most of the process is handled automatically by maven.

### Installation

1. Clone/download the repository 
2. Change the target version *(Minecraft version to modify, default is 1.12.2)*
    1. Open the project in your IDE and open `\src\main\java\mapping\MappingGen.java`
    3. Modify the version string to whichever version you want to target.
        1. Version must either be supported by MCP *(use the MCPOnlineJsonGen)* or by Forge *(use the MCPJsonGen)*
        2. If the version is supported by MCP, just run it. You can find the supported version list [here](http://export.mcpbot.bspk.rs/versions.json).
        3. If the version is supported only by forge, [download the forge MDK](https://files.minecraftforge.net/) and install it as if you were going to make your own mod *(This downloads important files that the tool uses to generate mappings. You can delete the downloaded forge files after running their install script)*
4. Run `\src\main\java\mapping\MappingGen.java` to generate your mappings file for the targeted version *(printed in console)*
    1. Paste contents of output into `\src\main\resources\cfg.json` *(This is the mappings of obfuscated names from that Minecraft version to our API's facades)*
    2. Make sure to refresh the project in your IDE so it detected the changes in the file *(You can refresh a project by selecting it and hitting F5)*


### Deployment

To compile UDP-Java as an agent simply navigate to the root directory *(should contain `pom.xml`)* and execute the following command in your console: `mvn clean package` *(Assuming you have completed all steps in the installation section)*

This will remove outdated build artifacts and recompile the agent. Maven will place the build files in a new folder under the root called `target`.

To use this in Minecraft, open the game launcher and navigate to the profile editor. Create a new profile and select the version of minecraft you are targeting with UDP-Java. Then under the settings ensure the java executale is a JDK exe. On a windows machine a possible value would look like: `C:\Program Files\Java\jdk1.8.0_131\bin\javaw.exe`
Then in the JVM arguments insert the following arguments:

* `-noverify`
* `-javaagent:<path/to/UDPMinecraftClient-agent.jar>`

For reference my entire JVM arguments are as follows: ` -client -noverify -javaagent:D:\Java\UDP\JavaCore\target\UDPMinecraftClient-agent.jar -Xmx2G -Xms2G -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:-UseAdaptiveSizePolicy`

Save your profile and launch the game. When the game starts it will search for plugins in the `.minecraft/plugins` folder. For developing plugins see the following tutorial: [Plugin Development](PluginDev.md)

### Dependencies

UDP-Java is build on the following libraries and frameworks:

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Objectweb ASM: 5.2](http://asm.ow2.org/)
* [Google Guava: 22.0](https://github.com/google/guava)
* [Google Gson: 2.8.1](https://github.com/google/gson)
* [LWJGL: 2.9.3](https://www.lwjgl.org/)

***

## Contributing

Please read [the contributing guide](CONTRIBUTING.md) for details on how to create proper contributions through pull requests and issues.

***

## Authors and Acknowledgements

* bloo
    * Hooking API _(A majority of the client essentially)_
    * Plugin API
* GenericSkid
    * Plugin API
    * Event system