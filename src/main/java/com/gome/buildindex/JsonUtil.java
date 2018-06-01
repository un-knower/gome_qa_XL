package com.gome.buildindex; /**
 * Created by lixiang-ds3 on 2016/12/9.
 */
import java.io.IOException;

import com.gome.buildindex.Blog;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class JsonUtil {

    // Java实体对象转json对象
    public static String model2Json(Blog blog) {
        String jsonData = null;
        try {
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject().field("id", blog.getId()).field("question", blog.getQuestion())
                    .field("answer", blog.getAnswer()).field("pro",blog.getPro()).endObject();

            jsonData = jsonBuild.string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public static String model2Json_attr(Blog_attr blog) {
        String jsonData = null;
        try {
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject().field("catId", blog.getCatId())
                    .field("attr", blog.getAttr()).field("ans",blog.getAns()).endObject();
            jsonData = jsonBuild.string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public static String model2Json_pc(Blog_pc blog) {
        String jsonData = null;
        try {
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject().field("classify", blog.getClassify())
                    .field("question", blog.getQuestion()).field("answer",blog.getAnswer()).endObject();
            jsonData = jsonBuild.string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public static String model2Json_product(Blog_product blog) {
        String jsonData = null;
        try {
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject()
                    .field("skuId", blog.getSkuId()).field("attr",blog.getAttr()).endObject();
            jsonData = jsonBuild.string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public static String model2Json_final(Blog_final blog) {
        String jsonData = null;
        try {
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject()
                    .field("catId", blog.getCatId())
                    .field("skuid", blog.getSkuid())
                    .field("title", blog.getTitle())
                    .field("attr",blog.getAttr()).endObject();
            jsonData = jsonBuild.string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public static void main(String[] args){
        String words = "中国是世界四大文明古国之一，有着悠久的历史，距今约5000年前，以中原地区为中心开始出现聚落组织进而成国家和朝代，后历经多次演变和朝代更迭，持续时间较长的朝代有夏、商、周、汉、晋、唐、宋、元、明、清等。中原王朝历史上不断与北方游牧民族交往、征战，众多民族融合成为中华民族。20世纪初辛亥革命后，中国的君主政体退出历史舞台，取而代之的是共和政体。1949年中华人民共和国成立后，在中国大陆建立了人民代表大会制度的政体。中国有着多彩的民俗文化，传统艺术形式有诗词、戏曲、书法和国画等，春节、元宵、清明、端午、中秋、重阳等是中国重要的传统节日。";
//        System.out.println(ToAnalysis.parse(words));
    }
}