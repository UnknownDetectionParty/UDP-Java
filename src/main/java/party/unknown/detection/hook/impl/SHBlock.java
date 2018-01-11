package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.MethodProxy;
import party.unknown.detection.hook.StaticHandler;

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
