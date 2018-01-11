package party.unknown.detection.hook.impl;

import java.util.List;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 8/11/2017
 */
public interface World {
	@Getter("a")
	List<Entity> getLoadedEntityList();

	@Getter("b")
	List<TileEntity> getLoadedTileEntityList();

	@Getter("c")
	List<EntityPlayer> getLoadedPlayerList();

	@MethodProxy("a")
	IBlockState getBlockState(BlockPos block);
}
