package tw.darkk6.meddle.api.mapping;

import tw.darkk6.meddle.api.srg.SrgMap;

// 提供給其他 Mod 方便快速存取的 Map
// NOTE : 如果使用 static 會因為要先載入該 Class (Class.forName) 而不會被 transform ,因此改為 instance
public class APIMap {
	private static APIMap instance;
	public static APIMap get(){
		if(instance==null){
			instance=new APIMap();
		}
		return instance;
	}
	
	public final String Minecraft = SrgMap.getClassName(APINameMap.clzMinecraft);
	public final String ISound = SrgMap.getClassName(APINameMap.clzISound);
	public final String SoundManager = SrgMap.getClassName(APINameMap.clzSoundManager);
	public final String PlayerControllerMP = SrgMap.getClassName(APINameMap.clzPlayerControllerMP);
	public final String GameSettings = SrgMap.getClassName(APINameMap.clzGameSettings);
	
	public final String getBlockReachDistance = SrgMap.getMethodName(APINameMap.mGetBlockReachDistance);
	public final String processRightClick = SrgMap.getMethodName(APINameMap.mProcessRightClick);
	public final String processRightClickBlock = SrgMap.getMethodName(APINameMap.mProcessRightClickBlock);
	
	// Classes
	public final Class MinecraftClz = SrgMap.getClassFromName(APINameMap.clzMinecraft);
	public final Class ISoundClz = SrgMap.getClassFromName(APINameMap.clzISound);
	public final Class SoundManagerClz = SrgMap.getClassFromName(APINameMap.clzSoundManager);
	public final Class PlayerControllerMPClz = SrgMap.getClassFromName(APINameMap.clzPlayerControllerMP);
	public final Class GameSettingsClz = SrgMap.getClassFromName(APINameMap.clzGameSettings);
}
