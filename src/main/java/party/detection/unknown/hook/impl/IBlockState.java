package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 12/27/2017
 */
public interface IBlockState {
	@MethodProxy("a")
	Block getBlock();
}
