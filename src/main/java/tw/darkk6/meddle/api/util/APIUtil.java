package tw.darkk6.meddle.api.util;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class APIUtil {

	public static PlayerControllerMP getPlayerController(){
		return PlayerControllerMP.get();
	}

	
	private static Object gameSettings=null;
	private static Field language_field=null;
	@SuppressWarnings("rawtypes")
	public static String getLanguage(){
		try{
			if(gameSettings==null){
				Class cls=Minecraft.class;
				Field f=cls.getField("u");//Minecraft.gameSettings
				gameSettings=f.get(Minecraft.getMinecraft());
			}
			if(language_field==null){
				language_field=gameSettings.getClass().getField("aC");//gameSettings.languge
			}
			String lang=language_field.get(gameSettings).toString();
			return lang;
		}catch(Exception e){
			APILog.error("Can not get language return default.");
			return "en_US";
		}
	}
	
	//未完成
	public static String getOpcodeName(AbstractInsnNode node){
		StringBuilder result=new StringBuilder();
		switch(node.getType()){
			case AbstractInsnNode.VAR_INSN:
				result.append(node.getOpcode());
				result.append(" ");
				result.append(((VarInsnNode)node).var);
				break;
			default:
				result.append(node.toString());
		}
		return result.toString();
	}
	
}
