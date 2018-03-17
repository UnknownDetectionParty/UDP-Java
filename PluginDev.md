# Plugin Development

Similarly to forge, once you write a plugin it *should*\* work for any version that the core API supports. The key is to just not use features that are specific to certain versions *(For example, don't expect a dual-wielding plugin to work in 1.8)*

### What is allowed? What classes can I reference?

Anything as long as you have the mappings for it. You can find the current supported classes / methods in `src/party/unknown/detection/hook/impl`. To add your own mappings, add to an existing interface or create your own. 

* Add `@Getter("identifier")` to an interface method that represents a field that you want to get the value of. 
* Add `@Setter("identifier")` to an interface method that represents a field that you want to set the value of.
* Add `@MethodProxy("identifier")` to an interface method that represents a method you want to call. 

**Examples**:

In `Entity.java`, getting/setting fall distance
```java
    @Getter("a")
    float getFallDistance();
    
    @Setter("a")
    void setFallDistance(float fallDistance);
```

In `Entity.java` calling the `getPosition()` method that converts the player's `x`, `y`, and `z` into a `BlockPos` 
```java
    @MethodProxy("a")
    BlockPos getPosition();
```

To register these mappings to the API we need to update our mappings in `cfg.json`. We can auto-generate this file by modifying `src/mapping/AbstractJsonGen`. There is a public method `configure()` that lists all the mappings. 

This is what our Entity class would look like if we wanted to add only those methods to the API. You do not have to do this since the Entity class is already mapped.

```java
cls(Entity.class, "net/minecraft/entity/Entity", 
    new Field() {{
        add("a", "fallDistance");
    }}.array(), 
    new Method() {{
        add("a", "getPosition", "()Lnet/minecraft/util/math/BlockPos;");
    }}.array()),
// more mapping definitions
```

Once your changes have been added run the mapping tool. You should know how to use it by reading the  [readme](README.md).

### Setting up a workspace

This is kinda shitty but what I did was make a new project in my workspace. I then had the project import `Core` *(The client API)* as a dependency and thus I agained all the imports needed for development. 

To compile I exported my plugin  as a jar and deleted the extra uneeded files. 

### Example plugins

All of your plugins will generally look like this:

**Template**:
```java
package YourPackageHere;

import org.lwjgl.input.Keyboard;
import party.detection.unknown.io.config.Setting;
import party.detection.unknown.event.EventListener;
import party.detection.unknown.event.impl.external.*;
import party.detection.unknown.hook.impl.*;
import party.detection.unknown.plugin.*;
import party.detection.unknown.plugin.annotations.*;

/**
 * @author YourNameHere
 * @since 3/16/2018
 */
@Plugin(
	id = "MyPluginID",
	name = "My Plugin Name",								
	description = "My description of the plugin.",	
	author = "YourNameHere",
	versions = {"1.12", "1.12.2", "more versions"}
)
public class MyPlugin extends KeyPlugin.Toggle {
	public MyPlugin(){
		setKey(Keyboard.KEY_WhateverKey);
	}
	
	@EventListener
	public void onSomeEvent(SomeEventClass e) {
		// Do thing
	}
}
```
This is a standard toggle mod. There are other kinds of mods you can create but those will typically go unused *(The cool ones aren't fully implemented so you're limited to the keybind ones)*. If you wanted you plugin to be on *while* a key is pressed instead of toggling change `KeyPlugin.Toggle` to `KeyPlugin.Press`.

You can check out the [examples directory](examples/) for more example plugins. They come compiled if you want to test them out.

### Creating custom events

Adding your own events is just an additional step to the mapping process. Lets look at an example, Minecraft where we add a KeyPress event.

```java
cls(Minecraft.class, "net/minecraft/client/Minecraft", 
    new Field() {{
        // removed for brevity
    }}.array(), 
    new Method() {{
    	add("sb", "dispatchKeypresses", "()V", 
    		new Local() {{
    			add(KeyDispatchEvent.class, 0);
    		}}.array());
    }}.array()), 
```

Now in this case we do not actually have `void dispatchKeypresses()` in our static-handler for Minecraft *(SHMinecraft)*. Nor is it in the normal mapping interface *(Minecraft)*. This is because we don't need to actually call it, it just needs to be in our `cfg.json`. So, we know the name of the method, we know the descriptor of the method. To hook a local variable in the method we simply add:
```java
new Local() {{
	add(OurEventClass.class, VariableIndex);
}}.array()
```
And with out key example where the first variable *(zero-index)* is the key-value:
```java
new Local() {{
	add(KeyDispatchEvent.class, 0);
}}.array()
```