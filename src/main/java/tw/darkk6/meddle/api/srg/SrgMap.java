package tw.darkk6.meddle.api.srg;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import net.fybertech.meddle.Meddle;
import net.fybertech.meddle.MeddleUtil;
import tw.darkk6.meddle.api.util.APILog;

public class SrgMap {
	public static final String MC_VER=MeddleUtil.findMinecraftVersion();
	private static HashMap<String,String> clzMap;
	private static HashMap<MethodKey,String> methodMap;
	private static HashMap<FieldKey,String> fieldMap;
	
	public static boolean initSrgMap(){
		clzMap = new HashMap<String, String>();
		methodMap = new HashMap<MethodKey, String>();
		fieldMap = new HashMap<FieldKey, String>();
		//從 jar 檔案中載入這個版本設定的 srg map
		if(!loadFormJar()) return false;
		APILog.info("已載入內建 Mapping "+MC_VER);
		
		//尋找 meddle/config/ 底下的檔案
		File srgDir=new File(Meddle.getConfigDir(),"srgDir");
		String targetName=MC_VER+".srg";
		if(!srgDir.exists()) srgDir.mkdir();
		for(File f:srgDir.listFiles()){
			if(!f.isFile()) continue;
			if(targetName.equals(f.getName())){
				APILog.info("找到外部 Mapping 檔案，將執行資料替換...");
				if(!loadFromExtFile(f)) APILog.error("載入外部 Mapping 失敗...");
				else APILog.info("已載入外部 Mapping "+targetName);
				break;
			}
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getClassFromName(String clz){
		String name=getClassName(clz);
		if(name==null) return null;
		try{ return Class.forName(name); }
		catch(ClassNotFoundException e) {
			APILog.error("找不到 Class from name : "+clz);
			return null;
		}
	}
	public static String getClassName(String clz){
		String real=clz.replaceAll("\\.","/");
		if(!clzMap.containsKey(real)){
			APILog.error("找不到 Class name : "+clz);
			return null;
		}
		return clzMap.get(real);
	}
	
	
	public static String getMethodName(String name,String decs,String owner){
		return getMethodName(MethodKey.get(name,decs,owner));
	}
	public static String getMethodName(MethodKey key){
		if(!methodMap.containsKey(key)){
			APILog.error("找不到 Method name : "+key.getName());
			return null;
		}
		return methodMap.get(key);
	}
	
	public static String getFieldName(String name,String type,String owner){
		return getFieldName(FieldKey.get(name,type,owner));
	}
	public static String getFieldName(FieldKey key){
		if(!fieldMap.containsKey(key)){
			APILog.error("找不到 Field name : "+key.getName());
			return null;
		}
		return fieldMap.get(key);
	}
	
	
	public static void printDebug(){
		for(String str:clzMap.keySet())
			System.out.println("CL: "+str+" => "+clzMap.get(str));
		for(MethodKey key:methodMap.keySet())
			System.out.println(key.toString());
		for(FieldKey key:fieldMap.keySet())
			System.out.println(key.toString());
	}
/***************************************************************************/
	private static boolean loadFromExtFile(File f){
		try{
			return loadMapFromFile(new FileInputStream(f));
		}catch(Exception e){
			APILog.error(e.getMessage());
			return false;
		}
	}
	
	private static boolean loadMapFromFile(InputStream srgStream){
		Scanner ipt=new Scanner(srgStream);
		while(ipt.hasNextLine()){
			String line=ipt.nextLine();
			if(line.startsWith("#")) continue;
			String[] data=line.split(" ");
			if(data.length<2) continue;
			try{
				//正式開始讀取分析
				if("CL:".equals(data[0])){
					if(clzMap.containsKey(data[2]))
						APILog.info(data[2]+"已存在，將取代為 "+data[1]+" [原"+clzMap.get(data[2])+"]");
					clzMap.put(data[2], data[1]);
				}else if("MD:".equals(data[0])){
					MethodKey key=MethodKey.get(data[3],data[4],data[2],data[1]);
					if(methodMap.containsKey(key))
						APILog.info(data[2]+"->"+data[3]+data[4]+"已存在，將取代為 "+data[1]+" [原"+methodMap.get(key)+"]");
					methodMap.put(key, key.getSrgName());
				}else if("FD:".equals(data[0])){
					FieldKey key=FieldKey.get(data[3],data[4],data[2],data[1]);
					if(fieldMap.containsKey(key))
						APILog.info(data[2]+"->"+data[3]+"已存在，將取代為 "+data[1]+" [原"+fieldMap.get(key)+"]");
					fieldMap.put(key, key.getSrgName());
				}
			}catch(Exception e){
				APILog.error("分析 "+line+" 錯誤，已跳過");
			}
		}
		ipt.close();
		return true;
	}
	private static boolean loadFormJar(){
		InputStream srgStream=SrgMap.class.getResourceAsStream("/srgmapping/"+MC_VER+".srg");
		if(srgStream==null) return false;
		return loadMapFromFile(srgStream);
	}
/***************************************************************************/
	
	public static class MethodKey{
		public static MethodKey get(String name,String desc,String owner){
			return new MethodKey(name,desc,owner);
		}
		public static MethodKey get(String name,String desc,String owner,String srgName){
			return new MethodKey(name,desc,owner,srgName);
		}

		private String owner,desc,name;
		private String srgName;
		public MethodKey(String name,String desc,String owner){
			this(name,desc,owner,null);
		}
		public MethodKey(String name,String desc,String owner,String srgName){
			this.owner=owner.replaceAll("\\.","/");
			this.name=name;
			this.desc=desc.replaceAll("\\.","/");
			this.srgName=srgName;
		}
		
		public String getName(){ return this.name; }
		public String getSrgName(){ return this.srgName; }
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((desc == null) ? 0 : desc.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			MethodKey other = (MethodKey) obj;
			return other.hashCode() == this.hashCode();
		}
		
		@Override
		public String toString(){
			return String.format("MD: %s%s in [%s]",name,desc,owner);
		}
	}
	
	public static class FieldKey{
		public static FieldKey get(String name,String type,String owner){
			return new FieldKey(name,type,owner);
		}
		public static FieldKey get(String name,String type,String owner,String srgName){
			return new FieldKey(name,type,owner,srgName);
		}
		private String owner,type,name;
		private String srgName;
		public FieldKey(String name,String type,String owner){
			this(name,type,owner,null);
		}
		public FieldKey(String name,String type,String owner,String srgName){
			this.owner=owner.replaceAll("\\.","/");
			this.name=name;
			this.type=type.replaceAll("\\.","/");
			this.srgName=srgName;
		}
		
		public String getName(){ return this.name; }
		public String getSrgName(){ return this.srgName; }
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			FieldKey other = (FieldKey) obj;
			return other.hashCode() == this.hashCode();
		}
		
		@Override
		public String toString(){
			return String.format("FD: %s->%s in [%s]",name,type,owner);
		}
	}
}
