package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.MethodProxy;

public interface IBlockState {
	@MethodProxy("a")
	Block getBlock();
}
