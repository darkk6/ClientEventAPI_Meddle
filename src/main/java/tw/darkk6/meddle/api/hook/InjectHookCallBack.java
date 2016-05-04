package tw.darkk6.meddle.api.hook;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import tw.darkk6.meddle.api.EventRegister;
import tw.darkk6.meddle.api.EventRegister.EventType;
import tw.darkk6.meddle.api.event.SoundEvent;
import tw.darkk6.meddle.api.listener.IGuiOpenListener;
import tw.darkk6.meddle.api.listener.IRenderOverlayListener;
import tw.darkk6.meddle.api.listener.IRenderTickListener;
import tw.darkk6.meddle.api.listener.ISoundListener;
import tw.darkk6.meddle.api.listener.ITickListener;

public class InjectHookCallBack {
/*
 * 	這個 Class 中的 Method 主要是讓  inject 到 minecraft 的 Code 呼叫的
 */
/********************************************************************/
	// 當 Tick 時呼叫 , Minecraft.runTick()
	@SuppressWarnings("unchecked")
	public static void onTick(boolean isStartPhase){
		if(EventRegister.EVENT_BUS.containsKey(EventType.TICK)){
			ArrayList<ITickListener> list=EventRegister.EVENT_BUS.get(EventType.TICK);
			for(ITickListener listener:list){
				if(isStartPhase) listener.onTickStart();
				else listener.onTickEnd();
			}
		}
	}
/********************************************************************/
	// 當 RenderTick 時呼叫 , Minecraft.runGameLoop()
	@SuppressWarnings("unchecked")
	public static void onRenderTick(boolean isStartPhase){
		if(EventRegister.EVENT_BUS.containsKey(EventType.RENDERTICK)){
			ArrayList<IRenderTickListener> list=EventRegister.EVENT_BUS.get(EventType.RENDERTICK);
			for(IRenderTickListener listener:list){
				if(isStartPhase) listener.onRenderTickStart();
				else listener.onRenderTickEnd();
			}
		}
	}
	
/********************************************************************/	
	//當開啟 GUI 時呼叫 Minecraft.displayGuiScreen(GuiScreen)
	private static String prevGui="theNull";
	@SuppressWarnings("unchecked")
	public static void onGuiOpen(GuiScreen gui){
		String guiClass = (gui==null) ? "theNull" : gui.getClass().getName();
		if(prevGui.equals(guiClass)) return;
		prevGui = guiClass;
		if(EventRegister.EVENT_BUS.containsKey(EventType.GUIOPEN)){
			ArrayList<IGuiOpenListener> list=EventRegister.EVENT_BUS.get(EventType.GUIOPEN);
			for(IGuiOpenListener listener:list)
				listener.onGuiOpen(gui);
		}
	}
/********************************************************************/
	//播放聲音時呼叫 SoungManager.playSound(ISound)
	@SuppressWarnings("unchecked")
	public static void onSoundPlay(Object isound){
		SoundEvent event=null;
		if(EventRegister.EVENT_BUS.containsKey(EventType.SOUNDPLAY)){
			ArrayList<ISoundListener> list=EventRegister.EVENT_BUS.get(EventType.SOUNDPLAY);
			if(list.size()<=0) return;
			event=new SoundEvent(isound);
			for(ISoundListener listener:list)
				listener.onSoundPlay(event);
		}
	}
/********************************************************************/
	//播放聲音時呼叫 GuiIngame.renderGameOverlay(float)
	@SuppressWarnings("unchecked")
	public static void onRenderOverlay(boolean isStartPhase){
		if(EventRegister.EVENT_BUS.containsKey(EventType.RENDEROVERLAY)){
			ArrayList<IRenderOverlayListener> list=EventRegister.EVENT_BUS.get(EventType.RENDEROVERLAY);
			for(IRenderOverlayListener listener:list){
				if(isStartPhase) listener.onRenderOverlayStart();
				else listener.onRenderOverlayEnd();
			}
		}
	}
}
