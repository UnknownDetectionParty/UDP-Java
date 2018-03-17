# UDP Java: Core

Unknown Detection Party's core library for editing Minecraft written in Java. The core comes with a powerful mapping API that allows you to easily make facade's for minecraft classes and reference them just like you would in a typical forge mod. Compiling is easy and handled by maven, you can find further instruction on compilation and usage below.

***

## Getting Started

UDP-Java is fairly easy to set up as most of the process is handled automatically by maven.

### Installation

1. Make sure you have the JDK, Minecraft, and [Maven](https://maven.apache.org/install.html) installed.
2. Clone/download the repository then open it in your IDE
    1. For eclipse you open the project via `File > Import > Maven > Existing Maven Project`
3. Change the target version *(Minecraft version to modify, default is 1.12.2)*
    1. Open `\src\main\java\mapping\MappingGen.java`
    2. Modify the version string to whichever version you want to target.
        1. Version must either be supported by MCP *(use the MCPOnlineJsonGen)* or by Forge *(use the MCPJsonGen)*
        2. If the version is supported by MCP, just run it. You can find the supported version list [here](http://export.mcpbot.bspk.rs/versions.json).
        3. If the version is supported only by forge, [download the forge MDK](https://files.minecraftforge.net/) and install it as if you were going to make your own mod *(This downloads important files that the tool uses to generate mappings. You can delete the downloaded forge files after running their install script)*
4. Run `\src\main\java\mapping\MappingGen.java` to generate your mappings file for the targeted version *(printed in console)*
    1. Paste contents of output into `\src\main\resources\cfg.json` *(This is the mappings of obfuscated names from that Minecraft version to our API's facades)*
    2. Make sure to refresh the project in your IDE so it detected the changes in the file *(You can refresh a project by selecting it and hitting F5)*


### Deployment

1. Open a console in the root directory *(Wherever you downloaded the project)*
2. Run the command `mvn clean package`
    1. This creates `%root%\target\` which contains the compiled jar files.
3. Open the Minecraft launcher and create a new profile
    1. Set the java executable to the JDK's `bin.exe`. An example would look like: `C:\Program Files\Java\jdk1.8.0_131\bin\javaw.exe`
	2. Add these two arguments to the jvm-arguments
		1. `-noverify`
		2. `-javaagent:<path/to/UDPMinecraftClient-agent.jar>`
	    3. Example of my jvm-args: `-client -noverify -javaagent:D:\Java\UDP\JavaCore\target\UDPMinecraftClient-agent.jar -Xmx2G -Xms2G -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:-UseAdaptiveSizePolicy`
	3. Set the game version to match what your agent was built off of.
4. Save the profile and launch the game
	1. Plugins are loaded from `.minecraft/udp/plugins/` 
	2. For developing plugins see the following tutorial: [Plugin Development](PluginDev.md)

### Logs

Do not be afraid if you see error blobs in the launcher's console tab. There are some *valid* cases where you may see something like the folloiwng: 
```
[2018-03-16 19:40:53] UDP[ERROR]: party.unknown.detection.hook.InvalidHookException: Failed to generate getter for: bih#getSpeed
    at party.unknown.detection.hook.HookController$1.visitEnd(HookController.java:351)
```
This was generated when running the client with 1.12.2. The UDP-Core was compiled with mappings for the `Timer` class *(bih)* but the methods were unable to be found. This will only be an issue if you were to attempt to use those methods. In this case, something like *Kill Aura* would be unaffected. 

This is a known problem with mapping generation *(MappingGen tol)* where invalid versioned mappings are included in the output `cfg.json`.

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