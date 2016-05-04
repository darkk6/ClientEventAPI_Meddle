package tw.darkk6.meddle.api.tweak;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.fybertech.meddle.Meddle;
import net.fybertech.meddle.MeddleMod;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import tw.darkk6.meddle.api.Config;
import tw.darkk6.meddle.api.EventRegister;
import tw.darkk6.meddle.api.EventRegister.EventType;
import tw.darkk6.meddle.api.listener.IGuiOpenListener;
import tw.darkk6.meddle.api.listener.IRenderOverlayListener;
import tw.darkk6.meddle.api.listener.IRenderTickListener;
import tw.darkk6.meddle.api.listener.ISoundListener;
import tw.darkk6.meddle.api.listener.ITickListener;
import tw.darkk6.meddle.api.transformer.TransformHandler;
import tw.darkk6.meddle.api.util.Reference;

@MeddleMod(depends={"dynamicmappings", "meddleapi"},id=Reference.MODID, name=Reference.MOD_NAME, version=Reference.MOD_VER, author=Reference.AUTHOR)
public class APITweaker implements ITweaker {

	@Override
	public void acceptOptions(List<String> arg0, File arg1, File arg2, String arg3) {
		//當作 init() 使用，做一些初始化的動作
		Reference.config=new Config(new File(Meddle.getConfigDir(),Reference.MODID+".cfg"));
		
		EventRegister.EVENT_BUS.put(EventType.TICK,new ArrayList<ITickListener>());
		EventRegister.EVENT_BUS.put(EventType.GUIOPEN,new ArrayList<IGuiOpenListener>());
		EventRegister.EVENT_BUS.put(EventType.SOUNDPLAY,new ArrayList<ISoundListener>());
		EventRegister.EVENT_BUS.put(EventType.RENDERTICK,new ArrayList<IRenderTickListener>());
		EventRegister.EVENT_BUS.put(EventType.RENDEROVERLAY,new ArrayList<IRenderOverlayListener>());
	}

	@Override
	public String[] getLaunchArguments() {return new String[0];}

	@Override
	public String getLaunchTarget() { return null; }

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		//classLoader.addTransformerExclusion("");
		classLoader.registerTransformer(TransformHandler.class.getName());
	}

}
