package tw.darkk6.meddle.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ItemUseResult;
import net.minecraft.util.MainOrOffHand;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import tw.darkk6.meddle.api.mapping.APIMap;
import tw.darkk6.meddle.api.mapping.APINameMap;
import tw.darkk6.meddle.api.srg.SrgMap;

public class PlayerControllerMP {
	
	private static PlayerControllerMP instance=null;
	private static Object cachePC=null;
	
	public static PlayerControllerMP get(){
		Object pmObj=getPlayerController();
		if(pmObj==null) return null;
		if(instance==null || cachePC!=pmObj){
			instance = new PlayerControllerMP(pmObj);
			cachePC=pmObj;
		}
		return instance;
	}
	@SuppressWarnings("rawtypes")
	private static Object getPlayerController(){
		try{
			Class cls = Minecraft.class;
			Field f=cls.getField(SrgMap.getFieldName(APINameMap.fPlayerController));
			Object pc=f.get(Minecraft.getMinecraft());
			return pc;
		}catch(Exception e){
			throw new RuntimeException("Can not get playerController object");
		}
	}
	
	private Object pmObj;
	private Class cls;
	private Method rightClick,rightClickBlock,getBlockReachDistance;
	
	@SuppressWarnings("unchecked")
	private PlayerControllerMP(Object pmObj){
		try{
			this.pmObj=pmObj;
			cls = APIMap.get().PlayerControllerMPClz;
			
			rightClickBlock = cls.getMethod(APIMap.get().processRightClick,
					EntityPlayerSP.class,WorldClient.class,ItemStack.class,
					BlockPos.class,EnumFacing.class,Vec3.class,MainOrOffHand.class
				);
			
			rightClick = cls.getMethod(APIMap.get().processRightClickBlock,
					EntityPlayer.class,World.class,ItemStack.class,MainOrOffHand.class
				);
			
			getBlockReachDistance = cls.getMethod(APIMap.get().getBlockReachDistance);
		}catch(Exception e){
			throw new RuntimeException("Can not create PlayerControllerMP object");
		}
	}
	public ItemUseResult processRightClick(EntityPlayer player, World world, ItemStack stack, MainOrOffHand hand){
		try {
			Object res=rightClick.invoke(pmObj,player,world,stack,hand);
			return (ItemUseResult)res;
		}catch(Exception e){
			APILog.error("Invoke processRightClick fail");
			return ItemUseResult.FAIL;
		}
	}
	public ItemUseResult processRightClickBlock(EntityPlayerSP player,WorldClient world,ItemStack stack, BlockPos pos,EnumFacing facing,Vec3 posVec,MainOrOffHand hand){
		try {
			Object res=rightClickBlock.invoke(pmObj,
					player, world,stack,pos,facing,posVec,hand
				);
			return (ItemUseResult)res;
		}catch(Exception e){
			APILog.error("Invoke processRightClickBlock fail");
			return ItemUseResult.FAIL;
		}
	}
	
	public float getBlockReachDistance(){
		try{
			Float result=(Float)getBlockReachDistance.invoke(pmObj);
			return result.floatValue();
		}catch(Exception e){
			APILog.error("Invoke getBlockReachDistance fail");
			return 4.5F;
		}
	}
}
