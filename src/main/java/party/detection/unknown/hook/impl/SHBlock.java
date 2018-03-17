package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;
import party.detection.unknown.hook.StaticHandler;

/**
 * @author GenericSkid
 * @since 8/11/2017
 */
@StaticHandler(Block.class)
public interface SHBlock {
	@MethodProxy("a")
	int isFromBlock(Block block);

	@MethodProxy("b")
	Block blockFromID(int id);
}
