# An overview of UDP-Java

--------

## `mapping`

Tool for generating mapping jsons for some version of Minecraft.

**Usage:** 

* `mapping.Mappingen <mc_version> <mapping_source>`
* run via eclipse and edit default values _(fast but improper way)_

**Parameters**: 
* `mc_version` _(default:1.13.0)_: Version of minecaft to build mappings for. Versions should be fully quantified _(don't use `1.12` and expect it to work for `1.12.2`)_
* `mapping_source` _(default:forge)_: The method of fetching mappings. Potential values: 
    *  `mcp` or `online`: Pull mappings from [mcpbot.bspk.rs](http://export.mcpbot.bspk.rs/versions.json). Limited version support to what is in the linked file.
    *  `forge`: Pull mappings from locally stored forge mappings. Forge drops mapping files in your `%home%\.gradle\caches\minecraft\de\oceanlabs\mcp\mcp_snapshot\` directory. When you run the forge Mod Development Kit *(mdk)* it generates all the neccesary mappings and places them there. 

**Result**: Minified JSON is for the version mapping is printed to console. Before you compile be sure to copy-paste the output into `src/main/resources/cfg.json`.

--------

## `party.detection.unknown.api`

Contains a very early draft of how the client could be intergrated with an online web-service that replys with json.  

**Backstory**: UDP-Java was designed to function like the steam workshop. You would have an account on a site and manage your subscribed plugins. Authors could update the plugin independent of the client and you could update them at your leisure. The default plugins would be whatever is popular at the time of account creation.

--------

## `party.detection.unknown.event`

The event system used by the client for pushing data to plugins.

**Usage**: Marking methods for event receiving

Common case:
```java
@EventListener
void myMethod(MyEventType event)
```

Less common case: Plugin wants to be the *first* to receive the event. 
```java
// where no other plugin has a priority >= 100
@EventListener(priority = 100)
void calledFirst(MyEventType event)

// where no other plugin has a priority <= -100
@EventListener(priority = -100)
void calledLast(MyEventType event)
```

**Usage**: Registering an object for receiving events

Plugins are automatically registered, however if you wish to register your own classes you can do the following:
```java
class MyClass {
    public MyClass() {
        EventManager.INSTANCE.register(this);
    }
}
```

**Default event implmentations**:

* `impl.external.AttackEntityEvent`: Fired when the player attacks an entity.
    * `EntityPlayer getPlayer()`: The player.
    * `Entity getTarget()`: The entity attacked.
* `impl.external.GuiRenderEvent`: Fired every render-tick when the in-game HUD is shown.
    * `Gui getGui()`: In-Game HUD instance.
* `impl.external.KeyDispatchEvent`: Fired when a key is pressed.
    * `int getKey()`: Value of key-code. Example value `Keyboard.KEY_W`
    * `boolean isRepeat()`: `true` if event is from repeated input of a held-down key. `false` if key has just been pressed or released.
    * `boolean isDown()` `true` if key has been pressed. `false` otherwise.
    * `boolean isUp()` `true` if key has been released. `false` otherwise.
* `impl.external.PreMotionUpdateEvent`: Fired every tick.
    * `EntityPlayerSP getPlayer()`: The player.

--------

## `party.detection.unknown.hook`

Most of the logic behind the client's back-end is here. Applying the mapping interfaces to obfuscated classes, injecting custom events into vanilla methods, etc. It's all done here. The only part you should need to work with is the `party.detection.unknown.hook.impl` package. This contains all the facades for entries in the mappings file. 

**Usage**:

Most of the important classes have an easy-access method in `party.detection.unknown.hook.impl.Wrapper`. If you do not want to use it *(not reccomended, it would be cleaner to just add to it when you need to)* you can call facades for static members of classes as such:

```java
// SHMyClass where there is also a facade for MyClass.
// For an example, check SHMinecraft and Minecraft.
HookController.INSTANCE.getStaticHandler(SHMyClass.class);
```

For accessing facades for non-static members of classes: *Hopefully your mapping has a static getter for an instance, like how Minecraft does. Otherwise you are sorry out of luck.*

**Creating your own mappings/facades**:

All facades are interfaces that are named to match up with the familiar names of MCP classes most users are familiar with *(You can name them whatever you want though)*. They have a collection of methods to represent getters and setters for fields and proxy calls to methods. Once implmented an entry in the `mapping.AbstractJsonGen` is added to match.

For example we have a getter and setter for a field `fallDistance`, then a proxy-call to `setGlowing(glow)`. We will represent the field with the id `a` and the method with the id of `z`. To access both of these the following changes should be made:

Facade:
```java
public interface Entity {
	@Getter("a")
	float getFallDistance();
	@Setter("a")
	void setFallDistance(float dist);
	
	@MethodProxy("z")
	void setGlowing(boolean glow);
```
AbstractJsonGen:
```java
cls(Entity.class, "net/minecraft/entity/Entity", 
	new Field() {{
		add("a", "fallDistance");
	}}.array(), 
	new Method() {{
		add("z", "setGlowing", "(Z)V");
	}}.array()),
```

It is also worth noting that adding custom events is also managed in AbstractJsonGen. If you wish to add an event to a method:

AbstractJsonGen:
```java
new Method() {{
	add("z", "setGlowing", "(Z)V",
		new Local() {{
		    // syntax: add( event, offset in method-code, local-variable... )
		    // local variables are optional, can include any number of them
		    // in this case, locals 0 and 1 (0 = this = entity, 1 = parameter boolean)
			add(EntityGlowEvent.class, 0, 0, 1);
		}}.array());
}}.array()),
```
EntityGlowEvent:
```java
public class EntityGlowEvent extends Event {
	public final Entity entity;
	public final boolean glow;

	// parameters must match type of local-variable intercepted in bytecode.
	public EntityGlowEvent(Entity entity, boolean glow) {
		this.entity = entity;
		this.glow = glow;
	}
}
```

--------

## `party.detection.unknown.io`

Automated setting managment for plugins.

**Usage**:

```java
public class BunnyHop extends Plugin {
    // annotate fields to store them in the config folder
    // value loaded from saved config when plugin is loaded
    // value saved to config when client exits 
	@Setting(name = "Speed")
	private float speed = 0.6f;
	
	// code that uses speed float
}
```

--------

## `party.detection.unknown.plugin`

Plugin API. Some of the plugin types are unfinished, but the common ones like keybinds and persistent ones are working.

**Usage**: Supported plugin types

```java
class MyPlugin extends KeyPlugin.Toggle {
    MyPlugin(){
        setKey(Keyboard.KEY_F); // // plugin toggles when key is pressed
    }
}
class MyPlugin extends KeyPlugin.Press {
    MyPlugin(){
        setKey(Keyboard.KEY_F);  // plugin is active while key is held down, disables when key is released.
    }
    // alternative 
    MyPlugin(){
        super(true); // inverts press logic
        setKey(Keyboard.KEY_F);  // plugin is active while key is released, disables when key is held down.
    }
}
class MyPlugin extends ActivePlugin.Startup {
    MyPlugin() {} // plugin is activated instantly, cannot be disabled through user-input. 
}
```

KeyPlugins' `setKey` can take an integer value such as `Keyboard.KEY_F` or a multi-key bind from `party.detection.unknown.util.Keybinding`.

**Usage**: For further examples check the [examples directory](examples/)  and the [plugin development tutorial](PluginDev.md).

--------

## `party.detection.unknown.util`

Miscellanous classes with utility functions.

* `ASM`: Bytecode utilities
* `Colors`: int-based colors
* `HookGen`: c̛̳͔̳̖̙͙̣̓ų̭̞̹͇̙͉̻̓͆ͩ͒̌̽r̵̪̪̗͈̤̫ͭͤś̗̞̯̘̟͊ͬ̽̃̑ͣe̳̞̟̻̰ͥͣ͛ḏ͕̥̪̖̃̋ ̼̳̃̍́c̪͋ͥͅǫ̞̘̝̔̎͒̾͂͒d̩̗͕͎̖̗͚̐͛̉ͤ͝e̞͆
* `Keybinding`: Underlying code for `KeyPlugin`. Essentially a linked list.
* `MCVersion`: Version utility
* `OS`: Operating system detection
* `URLReader`: Reads json from urls.
