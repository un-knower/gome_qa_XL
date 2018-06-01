package com.gome.process;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Read_WriteText {

	//格式化输出当前时间戳
		private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		public static String currentTimeString(){
			return df.format(System.currentTimeMillis());
		} 
		
		public static void currentTimePrint(String content){
			System.out.println(content+df.format(System.currentTimeMillis())); 
		}
		public static void currentTimePrint(){
			System.out.println(df.format(System.currentTimeMillis())); 
		}
		
		public static void currentTimeMiliPrint(String content){
			System.out.println(content+" --->"+System.currentTimeMillis()); 
		}
		//字符串是否包含汉字
		public static boolean hasHanZi(String word){
			int count =0;
			String regEx = "[\\u4e00-\\u9fa5]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(word);
			while (m.find()) {      
	            for (int i = 0; i <= m.groupCount(); i++) {      
	                 count = count + 1;      
	             }      
	         }     
	         if(count>=1){
	        	 return true;
	         }else{
	        	 return false;
	         }
		}
		
		//计算对数
		public static  double myLog(double value, double base) {
				return Math.log(value) / Math.log(base);
		}

		//判断是否是数字 
		public static boolean isNum(String str) {
			return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
		}
		
		//判断是否含有字母 
		public static boolean hasLetter(String str) {
			boolean isLetter = false;
			 for(int i=0 ; i<str.length() ; i++){ //循环遍历字符串   
		         if(Character.isLetter(str.charAt(i))){   //用char包装类中的判断字母的方法判断每一个字符
		             isLetter = true;
		         }
		     }
			 return isLetter;
		}
		
		//读入一个文件词表转换成为map
		public static HashMap<String,String> readWordListToMap(String path){
			
			HashMap<String,String> commonWordMap= new HashMap<String,String>();
			String[] lines = readTxtFile(path).split("\n");
			
			for(int i=0;i<lines.length;i++)
	        {	
				commonWordMap.put(lines[i],"");
	        }
			return commonWordMap;
		}
		//读入一个文件词表转换成为list
		public static String[] readWordListToList(String path){
			String[] lines = readTxtFile(path).split("\n");
			return lines;
		}
		
		//读入一个文件词表转换成为list
		public static String[] readWordListToList(String path,String encoding){
			String[] lines = readTxtFile(path,encoding).split("\n");
			return lines;
		}
		//词串含有冠词
		public static boolean hasArticle(String phrase){
			
			ArrayList<String> articleList = new ArrayList<String>();
			articleList.add("a");
			articleList.add("an");
			articleList.add("the");
			articleList.add("A");
			articleList.add("AN");
			articleList.add("THE");
			
			for(String article:articleList){
				String[] words  = phrase.trim().split(" ");
				for(String str:words){
					if(article.equals(str)){
						return true;
					}
				}
			}
			
			return false;
		}
		//读取TXT文件指定字符编码
		public static String readTxtFile(String path,String encoding){

			InputStream inputStream = null;
	        InputStreamReader inputReader = null;
	        BufferedReader bufferReader = null;
	        StringBuffer strBuffer = new StringBuffer();
	        try
	        {
	            inputStream = new FileInputStream(path);
	            inputReader = new InputStreamReader(inputStream,encoding);
	            bufferReader = new BufferedReader(inputReader);
	             
	            // 读取一行
	            String line = null;
	            while ((line = bufferReader.readLine()) != null)
	            {
	                strBuffer.append(line+"\n");
	            }
	            
	        }
	        catch (IOException e)
	        {
	        	e.printStackTrace();
	        }
	        finally
	        {	
	        	try {
					if(bufferReader!=null){
						bufferReader.close();
					}
					if(inputReader!=null){
						inputReader.close();
					}
					if(inputStream!=null){
						inputStream.close();
					}
				} catch (IOException e) {
				}
	        }
			
	        return strBuffer.toString();
		}
		
		//读取TXT文件
		public static String readTxtFile(String path){
			
			InputStream inputStream = null;
	        InputStreamReader inputReader = null;
	        BufferedReader bufferReader = null;
	        StringBuffer strBuffer = new StringBuffer();
	        try
	        {
	            inputStream = new FileInputStream(path);
	            inputReader = new InputStreamReader(inputStream);
	            bufferReader = new BufferedReader(inputReader);
	             
	            // 读取一行
	            String line = null;
	            while ((line = bufferReader.readLine()) != null)
	            {
	                strBuffer.append(line+"\n");
	            }
	            
	        }
	        catch (IOException e)
	        {
	        	e.printStackTrace();
	        }
	        finally
	        {	
	        	try {
					if(bufferReader!=null){
						bufferReader.close();
					}
					if(inputReader!=null){
						inputReader.close();
					}
					if(inputStream!=null){
						inputStream.close();
					}
				} catch (IOException e) {
				}
	        }
			
	        return strBuffer.toString();
		}
		//写入ＴＸＴ文件指定编码
		public static void writeTxtFile(String path,String content,String encode,boolean append){
			OutputStreamWriter write =null;
			BufferedWriter writer = null;
	        try {
	        	   File f = new File(path);
	        	   if (!f.exists()) {
	        	    f.createNewFile();
	        	   }
	        	  write = new OutputStreamWriter(new FileOutputStream(f,append),encode);
	        	  writer = new BufferedWriter(write);  
	        	   writer.write(content);
	        	   writer.write("\n");
	        	   
	        	   writer.close();
	        	  } catch (Exception e) {
	        		  e.printStackTrace();
	        
	        }finally{
	        	try {
					if(write!=null){
						write.close();
					}
					if(writer!=null){
						writer.close();
					}
				} catch (IOException e){}
	        } 
		}	
		
		//写入ＴＸＴ文件指定编码
		public static void writeTxtFile(String path,String content,String encode){
			OutputStreamWriter write =null;
			BufferedWriter writer = null;
	        try {
	        	   File f = new File(path);
	        	   if (!f.exists()) {
	        	    f.createNewFile();
	        	   }
	        	  write = new OutputStreamWriter(new FileOutputStream(f),encode);
	        	  writer = new BufferedWriter(write);  
	        	   writer.write(content);
	        	   
	        	   writer.close();
	        	  } catch (Exception e) {
	        		  e.printStackTrace();
	        
	        }finally{
	        	try {
					if(write!=null){
						write.close();
					}
					if(writer!=null){
						writer.close();
					}
				} catch (IOException e){}
	        } 
		}		
		//写入ＴＸＴ文件
		public static void writeTxtFile(String path,String content){
			
			FileWriter write = null;
			BufferedWriter writer = null;
	        try {
	        	  /*
	        	   File f = new File(path);
	        	   if (!f.exists()) {
	        	    f.createNewFile();
	        	   }
	        	   write = new OutputStreamWriter(new FileOutputStream(f,true));
	        	  * 
	        	  */
	        	   write = new FileWriter(path,true);
	        	   writer = new BufferedWriter(write);
	        	   writer.write(content + "\n");
	        	   
	        	   writer.close();
	        	  } catch (Exception e) {
	        		  e.printStackTrace();
	        
	        }finally{
	        	try {
					if(write!=null){
						writer.close();
					}
					if(writer!=null){
						writer.close();
					}
				} catch (IOException e){}
	        } 
		}
		
		//读取一个目录下文件,获取所有文件绝对路径
		public static String[] readAllFlies(String path){
				
				File file = new File(path);
				String[] flies = file.list();
				for(int i=0;i<flies.length;i++){
					flies[i]=path+flies[i];
				}
				return flies;
		}
}
