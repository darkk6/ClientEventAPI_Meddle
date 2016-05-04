package tw.darkk6.meddle.api;

import java.util.ArrayList;
import java.util.HashMap;

import tw.darkk6.meddle.api.listener.IGuiOpenListener;
import tw.darkk6.meddle.api.listener.IRenderOverlayListener;
import tw.darkk6.meddle.api.listener.IRenderTickListener;
import tw.darkk6.meddle.api.listener.ISoundListener;
import tw.darkk6.meddle.api.listener.ITickListener;

public class EventRegister {

	@SuppressWarnings("rawtypes")
	public static HashMap<EventType,ArrayList> EVENT_BUS=new HashMap<EventRegister.EventType, ArrayList>();
	
	@SuppressWarnings("unchecked")
	public static void addTickListener(ITickListener listener){
		if(!Config.tickEnabled) throw new RuntimeException("[ClientEventAPI] TickEvent is not enabled in config.");
		if(!EVENT_BUS.containsKey(EventType.TICK)){
			EVENT_BUS.put(EventType.TICK,new ArrayList<ITickListener>());
		}
		EVENT_BUS.get(EventType.TICK).add(listener);
	}
	
	@SuppressWarnings("unchecked")
	public static void addRenderTickListener(IRenderTickListener listener){
		if(!Config.renderTickEnabled) throw new RuntimeException("[ClientEventAPI] RenderTickEvent is not enabled in config.");
		if(!EVENT_BUS.containsKey(EventType.RENDERTICK)){
			EVENT_BUS.put(EventType.RENDERTICK,new ArrayList<IRenderTickListener>());
		}
		EVENT_BUS.get(EventType.RENDERTICK).add(listener);
	}
	
	@SuppressWarnings("unchecked")
	public static void addGuiOpenListener(IGuiOpenListener listener){
		if(!Config.guiOpenEnabled) throw new RuntimeException("[ClientEventAPI] GuiOpenEvent is not enabled in config.");
		if(!EVENT_BUS.containsKey(EventType.GUIOPEN)){
			EVENT_BUS.put(EventType.GUIOPEN,new ArrayList<IGuiOpenListener>());
		}
		EVENT_BUS.get(EventType.GUIOPEN).add(listener);
	}
	
	@SuppressWarnings("unchecked")
	public static void addSoundPlayListener(ISoundListener listener){
		if(!Config.soundPlayEnabled) throw new RuntimeException("[ClientEventAPI] SoundPlayEvent is not enabled in config.");
		if(!EVENT_BUS.containsKey(EventType.SOUNDPLAY)){
			EVENT_BUS.put(EventType.SOUNDPLAY,new ArrayList<ISoundListener>());
		}
		EVENT_BUS.get(EventType.SOUNDPLAY).add(listener);
	}
	
	@SuppressWarnings("unchecked")
	public static void addRenderOverlayListener(IRenderOverlayListener listener){
		if(!Config.renderOverlayEnabled) throw new RuntimeException("[ClientEventAPI] RenderOverlayEvent is not enabled in config.");
		if(!EVENT_BUS.containsKey(EventType.RENDEROVERLAY)){
			EVENT_BUS.put(EventType.RENDEROVERLAY,new ArrayList<IRenderOverlayListener>());
		}
		EVENT_BUS.get(EventType.RENDEROVERLAY).add(listener);
	}
	
	
	public static enum EventType{
		TICK,
		GUIOPEN,
		SOUNDPLAY,
		RENDERTICK,
		RENDEROVERLAY
	}
}
