package tw.darkk6.meddle.api;

import tw.darkk6.meddle.api.util.Reference;

public class ClientEventAPI {
	//檢查 ClientAPI Version 是否符合版本
	public static void checkApiVersionWithException(String minApiVer){
		if(!checkApiVersion(minApiVer)) throw new RuntimeException("Required ClientEvenAPI version is "+minApiVer+" current is "+Reference.MOD_VER);
	}
	public static boolean checkApiVersion(String minApiVer){
		return Reference.MOD_VER.compareTo(minApiVer) >= 0 ;
	}
}
