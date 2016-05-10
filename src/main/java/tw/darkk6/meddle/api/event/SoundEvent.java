package tw.darkk6.meddle.api.event;

import java.lang.reflect.Method;

import net.minecraft.util.ResourceLocation;

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
				// byg => ISound
				Class clz=Class.forName("byg");
				//=== a => getSoundLocation() ===
				Method m=clz.getMethod("a");
				ResourceLocation r=(ResourceLocation)m.invoke(iSoundObj);
				this.resLocation=r;
				//=== i => getXPosF() ===
				m=clz.getMethod("i");
				this.x=(Float)m.invoke(iSoundObj);
				//=== j => getYPosF() ===
				m=clz.getMethod("j");
				this.y=(Float)m.invoke(iSoundObj);
				//=== k=> getZPosF() ===
				m=clz.getMethod("k");
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
