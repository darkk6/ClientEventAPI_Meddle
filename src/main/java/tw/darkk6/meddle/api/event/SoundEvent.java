package tw.darkk6.meddle.api.event;

import java.lang.reflect.Method;

import net.minecraft.util.ResourceLocation;
import tw.darkk6.meddle.api.mapping.APIMap;
import tw.darkk6.meddle.api.mapping.APINameMap;
import tw.darkk6.meddle.api.srg.SrgMap;

public class SoundEvent {
	public String name;
	public ISound sound;
	public Object rawISoundObj;
	
	public SoundEvent(Object obj){
		this.sound=new ISound(obj);
		this.name=sound.getSoundLocation().a();
		this.rawISoundObj=obj;
	}
	
	public static class ISound{
		
		private ResourceLocation resLocation;
		private float x,y,z;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public ISound(Object iSoundObj){
			try{
				Class clz=APIMap.get().ISoundClz;
				
				Method m=clz.getMethod(SrgMap.getMethodName(APINameMap.mGetSoundLocation));
				ResourceLocation r=(ResourceLocation)m.invoke(iSoundObj);
				this.resLocation=r;
				
				m=clz.getMethod(SrgMap.getMethodName(APINameMap.mGetXPosF));
				this.x=(Float)m.invoke(iSoundObj);
				
				m=clz.getMethod(SrgMap.getMethodName(APINameMap.mGetYPosF));
				this.y=(Float)m.invoke(iSoundObj);
				
				m=clz.getMethod(SrgMap.getMethodName(APINameMap.mGetZPosF));
				this.z=(Float)m.invoke(iSoundObj);
				
			}catch(Exception e){
				throw new RuntimeException("SoundEvent : ISound object parse error.");
			}
		}
		
		public float getXPosF(){
			return x;
		}

		public float getYPosF(){
			return y;
		}

		public float getZPosF(){
			return z;
		}
		
		public ResourceLocation getSoundLocation(){
			return resLocation;
		}
	}
}
