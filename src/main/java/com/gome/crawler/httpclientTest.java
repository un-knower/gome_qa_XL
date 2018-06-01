package com.gome.crawler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Iterator;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.htmlparser.util.NodeList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import org.apache.http.conn.ssl.TrustSelfSignedStrategy;

/**
 * Created by zhangheng on 2017/11/7.
 *
 */
public class httpclientTest {

    private static PoolingHttpClientConnectionManager cm = null;
    private static HttpHost proxy = null;
    private static RequestConfig reqConfig = null;
    private static CredentialsProvider credsProvider = null;

    public static String doRequest(String url) {
        String html = "";
        HttpRequestBase httpReq = new HttpGet(url);
        CloseableHttpResponse httpResp = null;

        try {
            httpReq.setConfig(reqConfig);
            CloseableHttpClient httpClient = HttpClients.custom().
                    setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                    .setConnectionManager(cm).setDefaultCredentialsProvider(credsProvider).
                            build();
            AuthCache authCache = new BasicAuthCache();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);
            httpResp = httpClient.execute(httpReq, localContext);

            int statusCode = httpResp.getStatusLine().getStatusCode();
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent()));

            System.out.print(rd.readLine());
             if (rd.readLine() != null && statusCode == 200) {
                String line = "";
                while ((line = rd.readLine()) != null) {
                    html = html+line;
                }
            }
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            try {
                if(httpResp != null) {
                    httpResp.close();
                }
            } catch (IOException var17) {
                var17.printStackTrace();
            }

        }

        return html;
    }
    public static String getLink(String url)
    {
        String ss = doRequest(url);
        System.out.println(ss);
        String  regex = "<title>.*?</title>";//正则表达式
        Pattern p = Pattern.compile(regex,Pattern.CANON_EQ);
        Matcher m = p.matcher(ss);
        if (m.find()){
            System.out.println("ccccccccc");
            String title = m.group();
            System.out.println(m.group());
            title = title.replaceAll("<title>","").trim();
            String link = "<a target=\"_blank\" href="+url+"><strong><span style=\"color: rgb(51,102,255);\">"+title+"</span></strong></a>";
            System.out.println(link);
            return link;
            //拼接
            //<a rel="nofollow" data-code="public01002" href="https://login.gome.com.cn/login">登录</a>
            //<a target="_blank" href="http://help.gome.com.cn/question/5566.html"><strong><span style="color: rgb(51,102,255);">点击这里</span></strong></a>



        }else
        {
            String link = "对不起，未找到";
            return link;
        }
    }
    public  static void main(String[] args)
    {


        String dizhi = "https://tuan.gome.com.cn/deal/Q8800683253.html?intcmp=tuanrushbuy-8000020100-8-1";
        String url = "https://www.baidu.com";
        String ss = doRequest(dizhi);
        System.out.println(ss);
        String  regex = "<title>.*?</title>";//正则表达式
        Pattern p = Pattern.compile(regex,Pattern.CANON_EQ);
        Matcher m = p.matcher(ss);
        if (m.find()){
            System.out.println("ccccccccc");
            String title = m.group();
            System.out.println(m.group());
            title = title.replaceAll("<title>","").trim();
            String link = "<a target=\"_blank\" href="+dizhi+"><strong><span style=\"color: rgb(51,102,255);\">"+title+"</span></strong></a>";
            System.out.println(link);

            //拼接
            //<a rel="nofollow" data-code="public01002" href="https://login.gome.com.cn/login">登录</a>
            //<a target="_blank" href="http://help.gome.com.cn/question/5566.html"><strong><span style="color: rgb(51,102,255);">点击这里</span></strong></a>



        }else
        {
            String link = "对不起，未找到";
        }

    }
}
