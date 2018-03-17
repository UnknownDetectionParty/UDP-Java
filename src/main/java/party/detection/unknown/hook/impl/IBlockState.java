package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;

public interface IBlockState {
	@MethodProxy("a")
	Block getBlock();
}
