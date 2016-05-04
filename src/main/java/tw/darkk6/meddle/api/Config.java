package tw.darkk6.meddle.api;

import java.io.File;

import net.fybertech.meddleapi.ConfigFile;

public class Config {
	
	public static boolean tickEnabled=true,guiOpenEnabled=true,soundPlayEnabled=true;
	public static boolean renderOverlayEnabled=true; 
	public static boolean renderTickEnabled=true;
	
	public static boolean debug=false;
	
	private long lastModify=0L;
	private ConfigFile cfg;
	private File file;
	
	public Config(File file){
		cfg=new ConfigFile(file);
		this.file=file;
		reload();
		lastModify = file.lastModified();
	}
	
	public boolean update(){
		if(lastModify != file.lastModified()){
			reload();
			lastModify = file.lastModified();
			return true;
		}
		return false;
	}
	
	private void reload(){
		
		cfg.load();
		
		tickEnabled=((Boolean)cfg.get(ConfigFile.key("general", "enableTickEvent", Boolean.valueOf(tickEnabled), "啟用 Client onTick 事件偵測"))).booleanValue();
		guiOpenEnabled=((Boolean)cfg.get(ConfigFile.key("general", "enableGuiOpenEvent", Boolean.valueOf(guiOpenEnabled), "啟用 Client onGuiOpen 事件偵測"))).booleanValue();
		soundPlayEnabled=((Boolean)cfg.get(ConfigFile.key("general", "enableSoundPlayEvent", Boolean.valueOf(soundPlayEnabled), "啟用 Client onSoundPlay 事件偵測"))).booleanValue();
		renderTickEnabled=((Boolean)cfg.get(ConfigFile.key("general", "enableRenderTickEvent", Boolean.valueOf(renderTickEnabled), "啟用 Client onRenderTick 事件偵測"))).booleanValue();
		renderOverlayEnabled=((Boolean)cfg.get(ConfigFile.key("general", "enableRenderOverlayEvent", Boolean.valueOf(renderOverlayEnabled), "啟用 Client onRenderOverlay 事件偵測"))).booleanValue();
		
		debug=((Boolean)cfg.get(ConfigFile.key("internal", "debugMode", Boolean.valueOf(debug), "開啟偵錯輸出模式"))).booleanValue();
		
		if(cfg.hasChanged()) cfg.save();
	}
}
