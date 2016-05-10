package tw.darkk6.meddle.api.transformer;

import net.fybertech.dynamicmappings.DynamicMappings;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tw.darkk6.meddle.api.Config;
import tw.darkk6.meddle.api.util.APILog;

public class TransformHandler implements IClassTransformer {
	/*
	 *	Minecraft
	 *		bcd : 1.9.4
	 *		bcc : 1.9.2 
	 * 
	 * GuiIngame
	 * 		bcs : 1.9.4
	 * 		bcr : 1.9.2
	 * 
	 * GuiScreen
	 * 		bez : 1.9.4
	 * 		bey : 1.9.2
	 * 
	 * SoundManager
	 * 		byt : 1.9.2 ~ 1.9.4
	 * 
	 * ISound
	 * 		byg : 1.9.2 ~ 1.9.4
	 * 
	 */
	private static String MINECRAFT = DynamicMappings.getClassMapping("net/minecraft/client/Minecraft");
	
	private static String GUIINGAME = DynamicMappings.getClassMapping("net/minecraft/client/gui/GuiIngame");
	
	//net.minecraft.client.audio.SoundManager
	private static String SOUNDMGR = "byt";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(MINECRAFT!=null && MINECRAFT.equals(name)){
			//處理有關 minecraft 這個 class 的事情
			if(Config.tickEnabled) bytes=injectOnTickEvent(bytes);
			if(Config.guiOpenEnabled) bytes=injectOnGuiOpenEvent(bytes);
			if(Config.renderTickEnabled) bytes=injectOnRenderTickEvent(bytes);
		}else if(SOUNDMGR!=null && SOUNDMGR.equals(name)){
			//處理有關  SoundManager 這個 class 的事情
			if(Config.soundPlayEnabled) bytes=injectOnSoundPlayEvent(bytes);
		}else if(GUIINGAME!=null && GUIINGAME.equals(name)){
			//處理 GuiIngame 這個 class
			if(Config.renderOverlayEnabled) bytes=injectOnRenderOverlayEvent(bytes);
		}
		
		return bytes;
	}
/************************* Inject onRenderTick Event in Minecraft.class ******************************/
	private byte[] injectOnRenderTickEvent(byte[] bytes){
		String runGameLoopName = "av";	// 1.9.2~1.9.4
		String methodDecs = "()V";
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		MethodNode method = null;
		//使用 asm 版本的問題... 這邊應該可以直接給 MethodNode m:classNode.methods 才對
		for (Object objMethodNode : classNode.methods) {
			MethodNode m=(MethodNode)objMethodNode;
			if( runGameLoopName.equals(m.name) && (m.desc.equals(methodDecs))){
				method = m;
				break;
			}
		}
		if(method == null) return bytes;
		
		InsnList insnList = method.instructions;
		AbstractInsnNode targetNode=null;
		for(AbstractInsnNode node=insnList.getFirst() ; node!=null ; node=node.getNext()) {
			if(node.getOpcode()==Opcodes.LDC){
				if("gameRenderer".equals(((LdcInsnNode)node).cst.toString())){
					targetNode=node;
					break;
				}
			}
		}
		if(targetNode==null) return bytes;
		
		AbstractInsnNode target_s = null, target_e = null;
		// target 往下找到第一個 invokevirtual , insert after
		for(AbstractInsnNode tmp=targetNode;tmp!=null;tmp=tmp.getNext()){
			if(tmp.getOpcode()==Opcodes.INVOKEVIRTUAL){
				target_s=tmp;
				break;
			}
		}
		if(target_s==null) return bytes;
		
		//從 target_s 往下找，找到下一個 invokevirtual , insert after
		for(AbstractInsnNode tmp=target_s.getNext();tmp!=null;tmp=tmp.getNext()){
			if(tmp.getOpcode()==Opcodes.INVOKEVIRTUAL){
				target_e=tmp;
				break;
			}
		}
		if(target_e==null) return bytes;
		
		InsnNode iconst_0 = new InsnNode(Opcodes.ICONST_0);
		InsnNode iconst_1 = new InsnNode(Opcodes.ICONST_1);
		//經過測試，這個不能重複利用，一定要建一個新的
		AbstractInsnNode invoke_1=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onRenderTick", 
				"(Z)V",
				false
			);
		AbstractInsnNode invoke_2=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onRenderTick", 
				"(Z)V",
				false
			);

		//在  .endStartSection("gameRender") 後插入
		InsnList inject=new InsnList();
		inject.add(iconst_1);
		inject.add(invoke_1);
		insnList.insert(target_s,inject);
		
		//在  updateCameraAndRender(F,L) 之後插入 onRenderTick(false)
		inject.clear();
		inject.add(iconst_0);
		inject.add(invoke_2);
		insnList.insert(target_e,inject);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		byte[] result = writer.toByteArray();
		if (result == null) return bytes;
		
		if(Config.debug) APILog.info("onRenderTick Event hooked.");
		return result;
	}
	
/************************* Inject onGuiOpen Event in Minecraft.class ******************************/	
	private byte[] injectOnGuiOpenEvent(byte[] bytes){
		//Minecraft.displayGuiScreen(GuiScreen)
		String clGuiScreenName = DynamicMappings.getClassMapping("net/minecraft/client/gui/GuiScreen");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		// 為了不破壞原本的資料，這邊不指定 SKIP_FRAME 和 SKIP_DEBUG 
		classReader.accept(classNode,0);
		MethodNode method = DynamicMappings.getMethodNodeFromMapping(classNode, "net/minecraft/client/Minecraft displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V");
		if(method == null) return bytes;
		//尋找： astore_1 和 aload_1 連在一起的地方 (中間會有 LABEL FRAME LINENUMBER Nodes)
		InsnList insnList = method.instructions;
		AbstractInsnNode targetNode = null;
		for(AbstractInsnNode node=insnList.getFirst() ; node!=null ; node=node.getNext() ) {
			if (node.getOpcode() == Opcodes.ASTORE && ((VarInsnNode)node).var==1) {
				//找到 astore_1 , 檢查下一個是不是 aload_1 (中間會有 LabelNode... 等)
				AbstractInsnNode next = node.getNext();
				//跳過中間的 Frame,Debug 資訊以及 LabelNode
				while( next !=null ){
					if( next.getType() != AbstractInsnNode.LABEL &&
						next.getType() != AbstractInsnNode.LINE &&
						next.getType() != AbstractInsnNode.FRAME
						)
						break;
					next=next.getNext();
				}
				
				if( next!=null && next.getOpcode()==Opcodes.ALOAD && ((VarInsnNode)node).var==1 ){
					//astore_1 和 aload_1 連接再一起，之後要用 insertBefore , 所以這邊的 target 是 "next"
					targetNode = next;
					break;
				}
			}
		}
		if(targetNode==null) return bytes;
		
		VarInsnNode aload_1 = new VarInsnNode(Opcodes.ALOAD,1);
		//經過測試，這個不能重複利用，一定要建一個新的
		AbstractInsnNode invoke=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onGuiOpen", 
				"(L"+clGuiScreenName+";)V",
				false
			);
		
		InsnList inject=new InsnList();
		inject.add(aload_1);
		inject.add(invoke);
		insnList.insertBefore(targetNode,inject);
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		byte[] result = writer.toByteArray();
		if (result == null) return bytes;
		
		if(Config.debug) APILog.info("onGuiOpen Event hooked.");
		return result;
	}
	
/************************* Inject onTick Event in Minecraft.class ******************************/
	private byte[] injectOnTickEvent(byte[] bytes){
		// runTick() 在 1.9.2 ~ 1.9.4 => t ()V
		String runTickName = "t";	//method name
		String runTickDesc = "()V";	//method descriptor
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		MethodNode method = null;
		//使用 asm 版本的問題... 這邊應該可以直接給 MethodNode m:classNode.methods 才對
		for (Object objMethodNode : classNode.methods) {
			MethodNode m=(MethodNode)objMethodNode;
			if( runTickName.equals(m.name) && (m.desc.equals(runTickDesc))){
				method = m;
				break;
			}
		}
		if(method == null) return bytes;
		InsnList insnList = method.instructions;
		AbstractInsnNode targetNode = null;
		for(AbstractInsnNode node=insnList.getLast() ; node!=null ; node=node.getPrevious()  ) {
			if (node.getType() == AbstractInsnNode.INSN && node.getOpcode() == Opcodes.RETURN) {
				targetNode = node;
				break;
			}
		}
		if(targetNode==null) return bytes;
		
		InsnNode iconst_0 = new InsnNode(Opcodes.ICONST_0);
		InsnNode iconst_1 = new InsnNode(Opcodes.ICONST_1);
		//經過測試，這個不能重複利用，一定要建一個新的
		AbstractInsnNode invoke_1=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onTick", 
				"(Z)V",
				false
			);
		AbstractInsnNode invoke_2=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onTick", 
				"(Z)V",
				false
			);

		InsnList inject=new InsnList();
		//在一開始的地方插入 onTick(true)
		inject.add(iconst_1);
		inject.add(invoke_1);
		insnList.insertBefore(insnList.getFirst(),inject);
		
		//在 return 之前插入 onTick(false)
		inject.clear();
		inject.add(iconst_0);
		inject.add(invoke_2);
		insnList.insertBefore(targetNode,inject);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		byte[] result = writer.toByteArray();
		if (result == null) return bytes;
		
		if(Config.debug) APILog.info("onTick Event hooked.");
		return result;
	}
	
/************************* Inject onSoundPlay Event in SoundManager.class ******************************/
	//當 Client 播放聲音時 playSound(ISound p_sound)V  => c(byg isound)V
	//byg net/minecraft/client/audio/ISound , 1.9.2~1.9.4
	private byte[] injectOnSoundPlayEvent(byte[] bytes){
		String iSoundName = "byg";	//1.9.2~1.9.4
		String playMethod = "c";	//1.9.2~1.9.4
		String playMethodDecs = "(L"+iSoundName+";)V";
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		MethodNode method = null;
		//使用 asm 版本的問題... 這邊應該可以直接給 MethodNode m:classNode.methods 才對
		for (Object objMethodNode : classNode.methods) {
			MethodNode m=(MethodNode)objMethodNode;
			if( playMethod.equals(m.name) && (m.desc.equals(playMethodDecs))){
				method = m;
				break;
			}
		}
		if(method == null) return bytes;
		InsnList insnList = method.instructions;
		AbstractInsnNode targetNode = null;
		for(AbstractInsnNode node=insnList.getFirst() ; node!=null ; node=node.getNext()){
			/*  找到 List 的 isEmpty() , 之後回頭三個
			  		aload_0 <==== target
					getfield byt.o:Ljava/util/List;
					invokeinterface java/util/List->isEmpty()Z
			 */
			if(node.getOpcode()==Opcodes.INVOKEINTERFACE){
				if(((MethodInsnNode)node).name.equals("isEmpty")){
					AbstractInsnNode tmp=node.getPrevious();
					if(tmp!=null && tmp.getOpcode()==Opcodes.GETFIELD){
						tmp=tmp.getPrevious();
						if(tmp!=null && tmp.getOpcode()==Opcodes.ALOAD){
							if(((VarInsnNode)tmp).var==0){
								targetNode=tmp;
								break;
							}
						}
					}
				}
			}
		}
		
		if(targetNode==null) return bytes;
		AbstractInsnNode aload_1=new VarInsnNode(Opcodes.ALOAD,1);
		AbstractInsnNode invoke=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onSoundPlay", 
				"(Ljava/lang/Object;)V",
				false
			);

		InsnList inject=new InsnList();
		inject.add(aload_1);
		inject.add(invoke);
		insnList.insertBefore(targetNode,inject);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		byte[] result = writer.toByteArray();
		if (result == null) return bytes;
		
		if(Config.debug) APILog.info("onSoundPlay Event hooked.");
		return result;
	}
/************************* Inject onRenderOverlay Event in GuiIngame.class ******************************/
	private byte[] injectOnRenderOverlayEvent(byte[] bytes){
		// in Minecraft 1.9.2 ~ 1.9.4 => renderGameOverlay=> a(F)V
		String renderGOMethod = "a";
		String renderGOMethodDecs = "(F)V";
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		MethodNode method = null;
		//使用 asm 版本的問題... 這邊應該可以直接給 MethodNode m:classNode.methods 才對
		for (Object objMethodNode : classNode.methods) {
			MethodNode m=(MethodNode)objMethodNode;
			if( renderGOMethod.equals(m.name) && (m.desc.equals(renderGOMethodDecs))){
				method = m;
				break;
			}
		}
		if(method == null) return bytes;
		InsnList insnList = method.instructions;
		AbstractInsnNode target_start = null,target_end = null;
		//尋找第二個 ASTORE , insert after
		boolean found_first=false;
		for(AbstractInsnNode node=insnList.getFirst() ; node!=null ; node=node.getNext()) {
			if(node.getOpcode()==Opcodes.ASTORE){
				if(found_first){
					target_start=node;
					break;
				}
				found_first=true;
			}
		}
		//從後面往前找到 RETURN , insertBefore
		for(AbstractInsnNode node=insnList.getLast() ; node!=null ; node=node.getPrevious()) {
			if(node.getOpcode()==Opcodes.RETURN){
				target_end=node;
				break;
			}
		}
		if(target_start==null || target_end==null) return bytes;

		InsnNode iconst_0 = new InsnNode(Opcodes.ICONST_0);
		InsnNode iconst_1 = new InsnNode(Opcodes.ICONST_1);
		//經過測試，這個不能重複利用，一定要建一個新的
		AbstractInsnNode invoke_1=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onRenderOverlay", 
				"(Z)V",
				false
			);
		AbstractInsnNode invoke_2=new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"tw/darkk6/meddle/api/hook/InjectHookCallBack", 
				"onRenderOverlay", 
				"(Z)V",
				false
			);

		InsnList inject=new InsnList();
		//在取得 FontRenderer 後插入 onRenderOverlay(true)
		inject.add(iconst_1);
		inject.add(invoke_1);
		insnList.insert(target_start,inject);
		
		//在 return 之前插入 onRenderOverlay(false)
		inject.clear();
		inject.add(iconst_0);
		inject.add(invoke_2);
		insnList.insertBefore(target_end,inject);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		byte[] result = writer.toByteArray();
		if (result == null) return bytes;
		
		if(Config.debug) APILog.info("onRenderOverlay Event hooked.");
		return result;
	}
}
