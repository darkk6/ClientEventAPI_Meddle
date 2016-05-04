package tw.darkk6.meddle.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tw.darkk6.meddle.api.util.APILog.TextFormatting;

public class APILog {
	public static Logger log=LogManager.getLogger(Reference.AUTHOR);
	
	public static Logger info(String msg){ APILog.info(msg,Reference.LOG_TAG);return log;}
	public static Logger info(String msg,String tag){
		log.info("["+tag+"] "+msg);
		return log;
	}
	
	public static Logger error(String msg){ APILog.error(msg,Reference.LOG_TAG);return log;}
	public static Logger error(String msg,String tag){
		log.error("["+tag+"] "+msg);
		return log;
	}
	
	public static void logChat(String msg){ logChat(msg,Reference.LOG_TAG); }
	public static void logChat(String msg,String tag){
		try{
			ChatComponentText txt=new ChatComponentText(TextFormatting.RED+"["+tag+"] ");
			txt.a(TextFormatting.RESET+msg);
			Minecraft.getMinecraft().thePlayer.addChatMessage(txt);
		}catch(Exception e){
			APILog.info(msg,tag);
		}
	}
	
	public static void infoChat(String msg){ infoChat(msg,Reference.LOG_TAG); }
	public static void infoChat(String msg,String tag){
		try{
			ChatComponentText txt=new ChatComponentText(TextFormatting.GOLD+"["+tag+"] ");
			txt.a(TextFormatting.RESET+msg);
			Minecraft.getMinecraft().thePlayer.addChatMessage(txt);
		}catch(Exception e){
			APILog.info(msg,tag);
		}
	}
	
	// Meddle 沒有 EnumTextFormat 的 Mapping 只好自己做
	public static class TextFormatting {
		public static String PREFIX = "§";
		public static String BLACK = PREFIX+"0";
		public static String DARK_BLUE = PREFIX+"1";
		public static String DARK_GREEN = PREFIX+"2";
		public static String DARK_AQUA = PREFIX+"3";
		public static String DARK_RED = PREFIX+"4";
		public static String DARK_PURPLE = PREFIX+"5";
		public static String GOLD = PREFIX+"6";
		public static String GRAY = PREFIX+"7";
		public static String DARK_GRAY = PREFIX+"8";
		public static String BLUE = PREFIX+"9";
		public static String GREEN = PREFIX+"a";
		public static String AQUA = PREFIX+"b";
		public static String RED = PREFIX+"c";
		public static String LIGHT_PURPLE = PREFIX+"d";
		public static String YELLOW = PREFIX+"e";
		public static String WHITE = PREFIX+"f";
		public static String OBFUSCATED = PREFIX+"k";
		public static String BOLD = PREFIX+"l";
		public static String STRIKETHROUGH = PREFIX+"m";
		public static String UNDERLINE = PREFIX+"n";
		public static String ITALIC = PREFIX+"o";
		public static String RESET = PREFIX+"r";
	}
}
