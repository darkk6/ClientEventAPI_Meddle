package tw.darkk6.meddle.api.mapping;

import tw.darkk6.meddle.api.srg.SrgMap;
import tw.darkk6.meddle.api.srg.SrgMap.FieldKey;
import tw.darkk6.meddle.api.srg.SrgMap.MethodKey;

/******
	統一在這裡撰寫這個 Mod 會用到的 Srg Names , 方便未來修改與管理
******/
public class APINameMap {
	/****** Classes ******/
	public static final String clzISound="net/minecraft/client/audio/ISound";
	public static final String clzMinecraft="net/minecraft/client/Minecraft";
	public static final String clzSoundManager="net/minecraft/client/audio/SoundManager";
	public static final String clzPlayerControllerMP="net/minecraft/client/multiplayer/PlayerControllerMP";
	public static final String clzGameSettings="net/minecraft/client/settings/GameSettings";
	public static final String clzGuiIngame="net/minecraft/client/gui/GuiIngame";
	public static final String clzGuiScreen="net/minecraft/client/gui/GuiScreen";
	
	/****** Methods ******/
	// ==== ISound ===
		public static final MethodKey mGetSoundLocation=MethodKey.get("getSoundLocation","()Lnet/minecraft/util/ResourceLocation;",clzISound);
		public static final MethodKey mGetXPosF=MethodKey.get("getXPosF","()F",clzISound);
		public static final MethodKey mGetYPosF=MethodKey.get("getYPosF","()F",clzISound);
		public static final MethodKey mGetZPosF=MethodKey.get("getZPosF","()F",clzISound);
		
	// ==== PlayerControllerMP ====
		public static final MethodKey mGetBlockReachDistance= MethodKey.get("getBlockReachDistance", "()F", clzPlayerControllerMP);
		public static final MethodKey mProcessRightClick=MethodKey.get("processRightClick", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;", clzPlayerControllerMP);
		public static final MethodKey mProcessRightClickBlock=MethodKey.get("processRightClickBlock", "(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;", clzPlayerControllerMP);
		
	// ==== For transformer ===
		public static final MethodKey mPlaySound=MethodKey.get("playSound","(Lnet/minecraft/client/audio/ISound;)V",clzSoundManager);
		public static final MethodKey mRenderGameOverlay=MethodKey.get("renderGameOverlay","(F)V",clzGuiIngame);
		public static final MethodKey mRunTick=MethodKey.get("runTick","()V",clzMinecraft);
		public static final MethodKey mRunGameLoop=MethodKey.get("runGameLoop","()V",clzMinecraft);
		
		
	/****** Method descriptor ******/
	//主要用在 transformer 的部分
		public static final String playSoundDesc="(L"+SrgMap.getClassName(clzISound)+";)V";
		public static final String renderGameOverlayDesc="(F)V";
		public static final String runTickDesc="()V"; 
		public static final String runGameLoopDesc="()V";
		
	/****** Field ******/
		public static final FieldKey fPlayerController=FieldKey.get("playerController","Lnet/minecraft/client/multiplayer/PlayerControllerMP;",clzMinecraft);
		public static final FieldKey fGameSettings=FieldKey.get("gameSettings","Lnet/minecraft/client/settings/GameSettings;",clzMinecraft);
		public static final FieldKey fLanguage=FieldKey.get("language","Ljava/lang/String;",clzGameSettings);
}
