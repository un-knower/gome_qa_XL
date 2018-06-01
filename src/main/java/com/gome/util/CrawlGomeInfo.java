package com.gome.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlGomeInfo {
	
	public String getGomeProductInfo(String keywords){
		String resultJson = "";
		//创建HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        //HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();    
        String keyword = URLEncoder.encode(keywords);
		String url = "https://search.gome.com.cn/search?question="+keyword+"&searchType=goods";
//        System.out.println(httpGet.getRequestLine());  
        try {
			HttpGet httpGet = new HttpGet(url);
			//执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);  
            //获取响应消息实体  
            HttpEntity entity = httpResponse.getEntity();  
            //响应状态  
            int state = httpResponse.getStatusLine().getStatusCode();
//            System.out.println("status:" + state);  
            //判断响应实体是否为空  
            if (entity != null && state == 200) {  
            	String html = EntityUtils.toString(entity);
            	Document document = Jsoup.parse(html);
            	Elements infos = document.select("#product-box > li"); 
            	//需要加判断每次获取的数据量
            	if(infos.size()>9){
            		int i = 0;
            		for(Element elem : infos){
            			String imgurl = elem.select("li > div > p.item-pic > a > img").attr("gome-src").toString();
//            			System.out.println(imgurl);
            			int flag = imgurl.indexOf("http");
            			if(flag == -1){
            				imgurl = "http:"+imgurl;
            			}
                		String deatilurl = elem.select("li > div > p.item-pic > a").attr("href").toString();
						if(deatilurl.contains("http") != true){
							deatilurl = "http:"+deatilurl;
						}
                		String title = "";
                		double result_price = 0.0;
                		HttpGet httpGet2 = new HttpGet(deatilurl);
                		httpResponse = closeableHttpClient.execute(httpGet2);
                		entity = httpResponse.getEntity();
                		if(entity != null && httpResponse.getStatusLine().getStatusCode() == 200){
                			html = EntityUtils.toString(entity);
                        	document = Jsoup.parse(html);
                        	title = document.select("#gm-prd-main > div.hgroup > h1").text();
                        	String scriptInfo = document.select("head > script").toString();
                        	String price_str = scriptInfo.substring(scriptInfo.indexOf("price")+7, scriptInfo.indexOf("promoDesc")-5);
                        	double price =  Double.parseDouble(price_str);
                        	String gomePrice_str = scriptInfo.substring(scriptInfo.indexOf("gomePrice")+11, scriptInfo.indexOf("firstCategoryName")-5);
                        	double gomePrice = Double.parseDouble(gomePrice_str);
                        	//判断是price价格是否为零，如果为零使用gomePrice价格
                        	if(price == 0.0){
                        		result_price = gomePrice;
                        	}else {
        						result_price = price;
        					}
                		}else {
                			title = "";
                			result_price = 0.0;
						}
                    	String goodCommentPercent = "";
                    	try {
                    		String[] skuids = deatilurl.split("\\/");
                    		skuids = skuids[3].split("-");
                        	String comm_url = "https://ss.gome.com.cn/item/v1/d/m/store/unite/"+skuids[0]+"/"+skuids[1].replaceAll(".html", "")+"/N/11010200/110102002/1/72810012945/flag/item/allStores?callback=allStores&_=1504518369174";
                        	HttpGet httpGet3 = new HttpGet(comm_url);
                        	httpResponse = closeableHttpClient.execute(httpGet3);
                    		entity = httpResponse.getEntity();
                    		if(entity != null && httpResponse.getStatusLine().getStatusCode() == 200){
                    			html = EntityUtils.toString(entity);
                            	goodCommentPercent = html.substring(html.indexOf("goodCommentPercent")+21, html.indexOf("star")-3);
                            	goodCommentPercent = goodCommentPercent+"%";
                    		}else {
								goodCommentPercent = "";
							}
						} catch (Exception e) {
							// TODO: handle exception
							goodCommentPercent = "";
							e.printStackTrace();
						}
                    	resultJson += "{\"deatilurl\":"+"\""+deatilurl+"\""+",\"title\":"+"\""+title+"\""+",\"result_price\":"+"\""+result_price+"\""+",\"imgurl\":"+"\""+imgurl+"\""+","+"\"goodCommentPercent\":"+"\""+goodCommentPercent+"\""+"},";
                    	i ++;
                		if (i == 9){
                			break;
                		}
                	}
            	}else if(0<infos.size() && infos.size() <9) {
            		for(Element elem : infos){
            			String imgurl = elem.select("li > div > p.item-pic > a > img").attr("gome-src").toString();
            			int flag = imgurl.indexOf("http");
            			if(flag == -1){
            				imgurl = "http:"+imgurl;
            			}
                		String deatilurl = elem.select("li > div > p.item-pic > a").attr("href").toString();
						if(deatilurl.contains("http") != true){
							deatilurl = "http:"+deatilurl;
						}
                		String title = "";
                		double result_price = 0.0;
                		HttpGet httpGet2 = new HttpGet(deatilurl);
                		httpResponse = closeableHttpClient.execute(httpGet2);
                		entity = httpResponse.getEntity();
                		if(entity != null && httpResponse.getStatusLine().getStatusCode() == 200){
                			html = EntityUtils.toString(entity);
                        	document = Jsoup.parse(html);
                        	title = document.select("#gm-prd-main > div.hgroup > h1").text();                        	
                        	String scriptInfo = document.select("head > script").toString();
                        	String price_str = scriptInfo.substring(scriptInfo.indexOf("price")+7, scriptInfo.indexOf("promoDesc")-5);
                        	double price =  Double.parseDouble(price_str);
                        	String gomePrice_str = scriptInfo.substring(scriptInfo.indexOf("gomePrice")+11, scriptInfo.indexOf("firstCategoryName")-5);
                        	double gomePrice = Double.parseDouble(gomePrice_str);
                        	//判断是price价格是否为零，如果为零使用gomePrice价格
                        	if(price == 0.0){
                        		result_price = gomePrice;
                        	}else {
        						result_price = price;
        					}
                		}else {
                			title = "";
                			result_price = 0.0;
						}                		
                    	String goodCommentPercent = "";
                    	try {
                    		String[] skuids = deatilurl.split("\\/");
                    		skuids = skuids[3].split("-");
                        	String comm_url = "https://ss.gome.com.cn/item/v1/d/m/store/unite/"+skuids[0]+"/"+skuids[1].replaceAll(".html", "")+"/N/11010200/110102002/1/72810012945/flag/item/allStores?callback=allStores&_=1504518369174";
                        	HttpGet httpGet3 = new HttpGet(comm_url);
                        	httpResponse = closeableHttpClient.execute(httpGet3);
                    		entity = httpResponse.getEntity();
                    		if(entity != null && httpResponse.getStatusLine().getStatusCode() == 200){
                    			html = EntityUtils.toString(entity);
                            	goodCommentPercent = html.substring(html.indexOf("goodCommentPercent")+21, html.indexOf("star")-3);
                            	goodCommentPercent = goodCommentPercent+"%";
                    		}else {
								goodCommentPercent = "";
							}
						} catch (Exception e) {
							// TODO: handle exception
							goodCommentPercent = "";
							e.printStackTrace();
						}
                    	resultJson += "{\"deatilurl\":"+"\""+deatilurl+"\""+",\"title\":"+"\""+title+"\""+",\"result_price\":"+"\""+result_price+"\""+",\"imgurl\":"+"\""+imgurl+"\""+","+"\"goodCommentPercent\":"+"\""+goodCommentPercent+"\""+"},";                    
            		}
				}else {
					resultJson = "";
				}
				if(resultJson.length()>1){
					resultJson = "["+resultJson.subSequence(0, resultJson.length()-1)+"]";
				}
            }else {
            	resultJson = "";
			}  
        } catch (Exception e) {
        	resultJson = "";
            e.printStackTrace();  
        } finally {  
            try {  //关闭流并释放资源  
                closeableHttpClient.close();  
            } catch (Exception e) {
                e.printStackTrace();  
            }  
        }          
		return resultJson;
	}
}
