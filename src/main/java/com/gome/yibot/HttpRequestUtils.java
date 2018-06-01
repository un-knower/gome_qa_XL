package com.gome.yibot;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import java.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Http请求工具类
 * @author snowfigure
 * @since 2014-8-24 13:30:56
 * @version v1.0.1
 */
public class HttpRequestUtils {
    static boolean proxySet = false;
    static String proxyHost = "127.0.0.1";
    static int proxyPort = 8087;
    /**
     * 编码
     * @param source
     * @return
     */
    public static String urlEncode(String source,String encode) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source,encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return result;
    }

    public static String urlEncodeGBK(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
        return result;
    }
    /**
     * 发起http请求获取返回结果
     * @param req_url 请求地址
     * @return
     */
    public static String httpRequest(String req_url) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(req_url);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

            httpUrlConn.setDoOutput(false);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);

            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return buffer.toString();
    }

    /**
     * 发送http请求取得返回的输入流
     * @param requestUrl 请求地址
     * @return InputStream
     */
    public static InputStream httpRequestIO(String requestUrl) {
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            // 获得返回的输入流
            inputStream = httpUrlConn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     * 发送请求的URL
     * @return URL 所代表远程资源的响应结果
     *通过url 获得链接页面的内容
     */
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param isproxy
     *               是否使用代理模式
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param,boolean isproxy) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = null;
            if(isproxy){//使用代理模式
                @SuppressWarnings("static-access")
                Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                conn = (HttpURLConnection) realUrl.openConnection(proxy);
            }else{
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            // 打开和URL之间的连接

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");    // POST方法


            // 设置通用的请求属性

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.connect();

            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    //传入私钥，和map   map中已经有了不少参数了
    /*用来计算签名
    (1) 将除了sign参数以为的所有参数按照key进行字典排序，
    （2）排序后，把value按照排好的顺序 拼接起来，
    （3）最后在拼接上用户的私钥
    （4）拼接后的结果就是 计算签名的原始串 raw_str
     (5)计算签名时，PubKey和Question使用原始字符串，而不是UrlEncode后的。

    */
    private static String calSign(String privKey, Map<String, String>args){
        String rawStr = "";
        Collection<String> keySet = args.keySet();
        ArrayList<String> list = new ArrayList<String>(keySet);
        //key 排序
        Collections.sort(list);
         //value 值依次拼接
        for (int i = 0; i < list.size(); i++){
            String key = list.get(i);
            rawStr += args.get(key);
        }
        // 连接私钥
        rawStr += privKey;
        //
        try {
            // 将上述拼接后的字符串 进行base64 编码
            String b64Str = Base64.getEncoder().encodeToString(rawStr.getBytes("utf-8"));
            //将得到的结果 进行MD5哈希 ，得到的就是sign 签名
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(b64Str.getBytes("utf-8"));
            byte [] mdbytye = md5.digest();
            //BinHex 是计算机上用可以打印字符表示/传输二进制文件的一种编码方法
            return HexBin.encode(mdbytye).toLowerCase();
        }catch (Exception e){
            return "";
        }
    }

    // 把所有参数进行 拼接  组成请求的 url
    public static String generateGetUrl(String host, String pubKey, String privKey, Map<String, String>args, boolean urlEncodeFlag){
        String url = host;
        args.put("pubkey", pubKey);
        //获得签名
        String sign = calSign(privKey,args);
        Iterator iter = args.entrySet().iterator();

        boolean isFirst = true;

        while(iter.hasNext()){

            if (isFirst)
            {
                isFirst = false;
                url += "?";
            }else{
                url += "&";
                 }

            Map.Entry entry = (Map.Entry)iter.next();
            String val = (String)entry.getValue();

            if (urlEncodeFlag){

                try {
                    //拼接url过程中 对那些字段进行。不是只对  PublicKey 和question 进行urlrncoder吗
                    if(entry.getKey().equals("sessionId"))
                    {
                        val = (String)entry.getValue();
                    }else
                        {
                        val = URLEncoder.encode(val, "utf-8");
                        }
                }catch (Exception e)
                {
                    val = (String)entry.getValue();
                }
            }

            url += entry.getKey() + "=" + val;
        }

        url += "&sign=" + sign;

        return url;
    }
    //
    public static String generateGetUrl(String host, String pubKey, String privKey, HashMap<String, String>args){
        return generateGetUrl(host, pubKey, privKey, args, false);
    }

    public static String getAnswer(String query){

        Map<String,String> params = new HashMap<String, String>();
        params.put("question", query);
        params.put("ip", "demo.kfyy.wezhuiyi.com");
        //cid （坐席辅助必填字段）访问渠道，访问渠道，值位user 表示直接对接用户，
        params.put("cid","-");
        //业务入口
        params.put("eid","-");
        params.put("sessionId","默认");
        params.put("account","13344455566");

        //  追一地址
        String host = "http://demo.kfyy.wezhuiyi.com:45678/common/query";
        //  公钥
        String pubKey = "YZFXYdRHa5FuHiCSHmYSFo6p4D99G/mzVcXOehjjgqc";
        //  私钥
        String privKey = "9183367850486465a5ecea66ad58adb9";

        String url = generateGetUrl(host, pubKey, privKey, params, true);

        String sr= HttpRequestUtils.sendGet(url);
        return sr;
    }

   /* public  static void  main(String[] args)
    {
        String str = sendGet("http://www.cnblogs.com/blackiesong/p/6182038.html");
        System.out.print(str);

    }*/

}