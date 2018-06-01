package com.gome.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class JSONUtils {
	
	private static final Log LOG = LogFactory.getLog(JSONUtils.class.getName());

    private static final SerializeConfig config;  
    
    static {  
        config = new SerializeConfig();   
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格�?  
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格�?  
    }  
  
    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段  
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，�?�不是null  
            SerializerFeature.WriteNullNumberAsZero, // 数�?�字段如果为null，输出为0，�?�不是null  
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，�?�不是null  
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，�?�不是null  
    };  
    
    
    public static String toJSONString(Object object) {  
        return JSON.toJSONString(object, config, features);  
    }  
      
    public static String toJSONNoFeatures(Object object) {  
    	
        return JSON.toJSONString(object, config);  
    }  
    
    
    public static Object toBean(String text) {  
        return JSON.parse(text);  
    }  
  
    public static <T> T toBean(String text, Class<T> clazz) {  
        return JSON.parseObject(text, clazz);  
    }  
    
    // 转换为数�?  
    public static <T> Object[] toArray(String text) {  
        return toArray(text, null);  
    }  
  
    // 转换为数�?  
    public static <T> Object[] toArray(String text, Class<T> clazz) {  
        return JSON.parseArray(text, clazz).toArray();  
    } 
    
    
    // 转换为List  
    public static <T> List<T> toList(String text, Class<T> clazz) {  
        return JSON.parseArray(text, clazz);  
    }  
  
    /**  
     * 将javabean转化为序列化的json字符�?  
     * @param keyvalue  
     * @return  
     */  
    public static Object beanToJson(KeyValue keyvalue) {  
        String textJson = JSON.toJSONString(keyvalue);  
        Object objectJson  = JSON.parse(textJson);  
        return objectJson;  
    }  
      
    /**  
     * 将string转化为序列化的json字符�?  
     * @param text
     * @return  
     */  
    public static Object textToJson(String text) {  
        Object objectJson  = JSON.parse(text);  
        return objectJson;  
    }  
    
    
    
    /**  
     * json字符串转化为map  
     * @param s  
     * @return  
     */  
    public static Map stringToCollect(String s) {  
        Map m = JSONObject.parseObject(s);  
        return m;  
    }  
      
    /**  
     * 将map转化为string  
     * @param m  
     * @return  
     */  
    public static String collectToString(Map m) {  
        String s = JSONObject.toJSONString(m);  
        return s;  
    }  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	public static String getStringValue(JSONObject jobj,String jkey) throws JSONException{
		if(jobj.containsKey(jkey)){			
			return jobj.getString(jkey).trim().replaceAll("\\\n", " ").replaceAll("\\\r", "");
		}else{
			return "";
		}
	}
	
	public static String getValueFromCookie(String key,String cookie){
		Map<String,String> cookieMap = new HashMap<String, String>();
		if(cookie != null){
			String[] cookieentry = cookie.split(";");
			for(int i=0;i<cookieentry.length;i++){
				String kvstr  = cookieentry[i];
				String[] kv = kvstr.split("=");
				//cookie中会存在";=;"这样的情�?
				if(kv.length ==0){
					LOG.error(" url excption --- > ["+cookie+"]");
					continue;
				}
				cookieMap.put(kv[0].trim(), kv.length>1?kv[1]:"");
			}
			return cookieMap.get(key) ==null?"":cookieMap.get(key);
		}
		return "";
	}
	

	
	
	
	
	
	public static void main(String[] args) {
//		String key = "SSO_USER_ID";
//		String cookie="__clickidc=986671370154338355; uid=CjozJ1eKhgM2/2d5DjH6Ag==; __c_visitor=986671370154338355; s_getNewRepeat=986838658226-Repeat; g_co=show; proid120517atg=%5B%229134300563-1123240631%22%2C%229134290354-1123230210%22%2C%229134242780-1123181311%22%2C%229134080861-1123060110%22%2C%229100024852-1000039596%22%2C%229133430844-1122280136%22%2C%22A0004968681-pop8004583017%22%2C%22A0004969374-pop8004585446%22%2C%22A0005820123-pop8008709167%22%2C%22A0005820150-pop8008709247%22%5D; _jzqco=%7C%7C%7C%7C%7C1.548451863.986692284894.986816654942.986816685972.986816654942.986816685972.0.0.0.10.10; s_ev13=%5B%5B'cps_7628_9472_444%257C273%257C175%257C65%257C20%257C4ee6e890636be8fbf2f1023522ab40c0'%2C'986792503525'%5D%2C%5B'cps_13121_20040_wanmei3c'%2C'986796966479'%5D%2C%5B'cps_7628_9472_444%257C273%257C175%257C65%257C20%257C4ee6e890636be8fbf2f1023522ab40c0'%2C'986808713417'%5D%2C%5B'dsp_bh14_3_o_2_o_20140414_o_o_o'%2C'986816648820'%5D%2C%5B'dh_sogou_mz'%2C'986820598826'%5D%2C%5B'ad_ty_zj_yule-h_ttieneitl03_201607_top718'%2C'986838649894'%5D%5D; sid=7628; feedback=444%7C273%7C175%7C65%7C20%7C4ee6e890636be8fbf2f1023522ab40c0; wid=9472; s_uuid=8979f06d1da14314bbbb837b47e4e263; category=; cpsid=; _ga=GA1.3.1267861964.986820785; atgregion=23080100%7C%E6%B1%9F%E8%8B%8F%E7%9C%81%E6%B3%B0%E5%B7%9E%E5%B8%82%E6%B5%B7%E9%99%B5%E5%8C%BA%7C23080000%7C23000000; DSESSIONID=76743a82dbd440b9bd39cd8ec9457e55; _idusin=72286870739; cmpid=ad_ty_zj_yule-h_ttieneitl03_201607_top718; s_cc=true; gpv_pn=%E4%BF%83%E9%94%80%E4%B8%93%E5%8C%BA%3A7%E6%9C%88%E4%B8%80%E5%85%83%E5%A4%BA%E5%AE%9D; gpv_p22=no%20value; s_sq=gome-prd%3D%2526pid%253D%2525E4%2525BF%252583%2525E9%252594%252580%2525E4%2525B8%252593%2525E5%25258C%2525BA%25253A7%2525E6%25259C%252588%2525E4%2525B8%252580%2525E5%252585%252583%2525E5%2525A4%2525BA%2525E5%2525AE%25259D%2526pidt%253D1%2526oid%253Dhttp%25253A%25252F%25252Fprom.gome.com.cn%25252Fhtml%25252Fprodhtml%25252Ftopics%25252F201607%25252F8%25252F3873784074.html%2526ot%253DA%2526oi%253D21; s_ppv=-%2C14%2C14%2C887";
//		String s = getValueFromCookie(key,cookie);		
//		System.out.println("["+s+"]");		
//		String jsonStr="{\"cat2_3m\":[{\"weight\":\"1.02\",\"tagname\":\"cat10000012\"},{\"weight\":\"0.09\",\"tagname\":\"cat10000327\"},{\"weight\":\"0.17\",\"tagname\":\"cat21435570\"}],\"cat2_1m_view\":[{\"weight\":\"0.17\",\"tagname\":\"cat21435570\"}],\"brand_3m_view\":[{\"weight\":\"0.12\",\"tagname\":\"241908288\"},{\"weight\":\"0.08\",\"tagname\":\"10001744\"},{\"weight\":\"0.01\",\"tagname\":\"10007234\"}],\"brand_1m_view\":[{\"weight\":\"0.08\",\"tagname\":\"10001744\"}],\"cat2_3m_search\":[{\"weight\":\"0.18\",\"tagname\":\"cat10000012\"},{\"weight\":\"0.02\",\"tagname\":\"cat10000327\"}],\"hourpref\":[{\"weight\":\"0.22\",\"tagname\":\"10\"},{\"weight\":\"0.18\",\"tagname\":\"16\"},{\"weight\":\"0.18\",\"tagname\":\"8\"}],\"city\":\"台州市\",\"cat2_1m\":[{\"weight\":\"0.17\",\"tagname\":\"cat21435570\"}],\"cat2_3m_view\":[{\"weight\":\"0.17\",\"tagname\":\"cat21435570\"},{\"weight\":\"0.32\",\"tagname\":\"cat10000012\"}],\"brand_1m\":[{\"weight\":\"0.08\",\"tagname\":\"10001744\"}],\"brand_3m\":[{\"weight\":\"0.08\",\"tagname\":\"10001744\"},{\"weight\":\"0.01\",\"tagname\":\"10007234\"},{\"weight\":\"0.12\",\"tagname\":\"241908288\"}],\"device\":\"PC\",\"province\":\"浙江省\"}";		
//		TagCookId cookie = toBean(jsonStr,TagCookId.class);
//		System.out.println(cookie.toString());
	}
}


