package party.detection.unknown.example;

import org.lwjgl.input.Keyboard;

import party.detection.unknown.hook.impl.Minecraft;
import party.detection.unknown.plugin.HookType;
import party.detection.unknown.plugin.KeyPlugin;
import party.detection.unknown.plugin.annotations.*;

@Plugin(
		id = "RunHookTest",
		name = "HookTest",								
		description = "Hook shit.",	
		author = "GenericSkid",
		versions = {"1.7.10", "1.8.8", "1.12"}
)
public class HookTest extends KeyPlugin.Toggle {
	// Inject call to runhook, will print mc class instance on game startup for the given versions
	@Hook(
	    method = {
	        @Method(version = "1.7.10", signature = "bao/f()V"),
	        @Method(version = "1.8.8", signature = "ave/a()V"),
	        @Method(version = "1.8.9", signature = "ave/a()V"),
	        @Method(version = "1.12", signature = "bhz/aq()V")
	    },
	    type = HookType.BEGIN,
	    locals = 0
	)
	public void runHook(Minecraft mc) {
	    System.out.println(">> RunHook (MC: "  + mc.toString() + ")");
	}
	
	public HookTest() {
		setKey(Keyboard.KEY_O);
	}
}
