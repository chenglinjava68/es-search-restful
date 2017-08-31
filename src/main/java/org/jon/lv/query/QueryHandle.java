package org.jon.lv.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.client.RestClient;
import org.jon.lv.base.BaseHandle;
import org.jon.lv.constant.Constant;
import org.jon.lv.utils.BuildPath;
import org.jon.lv.utils.ConvertUtils;

import java.io.IOException;
import java.util.Set;

/**
 * @Package org.jon.lv.query.QueryHandle
 * @Description: 查询
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/8/30 9:38
 * version V1.0.0
 */
public class QueryHandle extends BaseHandle {

    public QueryHandle(RestClient restClient, String index, String type) {
        super(restClient, index, type);
    }

    /**
     * 自定义组装后的json请求字符串
     * @param queryJson
     * @return
     * @throws IOException
     */
    public String query(String queryJson) throws IOException{
        String url = BuildPath.build(index, type, Constant._SEARCH);

        System.out.println("-------------------" + queryJson);

        JSONObject object = JSON.parseObject(requestPretty(RequestMethod.POST, url, queryJson));
        return object.getJSONObject(Constant.HITS).toJSONString();
    }


    /**
     * 必须在开启使用_all整合所有字段值的情况下才可以使用
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String matchAll(String content, int from , int size, Set<String> rtnFields) throws IOException {
        String query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":[\"_all\"]}}," +
                "\"from\":" + from + ",\"size\":"+size+"}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":[\"_all\"]}}," +
                    "\"from\":"+from+",\"size\":"+size+",\"_source\": "+ ConvertUtils.collection2String(rtnFields)+"}";
        }
        return query(query);
    }

    /**
     * 自定义多字段查询
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param docFields - 从文档中那些字段匹配 ---字段可以可以加权eg:["title", "summary^3"] ^3 Boosting summary field的权重调成3，这样就提高了其在结果中的权重
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String multiField(String content, int from , int size, Set<String> docFields ,Set<String> rtnFields) throws IOException {

        if(docFields == null || docFields.size() == 0) return null;

        String query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":"+ ConvertUtils.collection2String(docFields) +"}}," +
                "\"from\":" + from + ",\"size\":"+size+"}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":"+ConvertUtils.collection2String(docFields)+"}}," +
                    "\"from\":"+from+",\"size\":"+size+",\"_source\": "+ ConvertUtils.collection2String(rtnFields)+"}";
        }

        return query(query);
    }

    /**
     * 模糊查询-----fuzziness的值指定为AUTO，其在term的长度大于5的时候相当于指定值为2
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param docFields - 从文档中那些字段匹配
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String fuzzyQuery(String content, int from , int size, Set<String> docFields, Set<String> rtnFields) throws IOException {

        String fields = (docFields == null || docFields.size() == 0) ? "[\"_all\"]" : ConvertUtils.collection2String(docFields);

        String query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":"+ fields +",\"fuzziness\": \"AUTO\"}}," +
                "\"from\":" + from + ",\"size\":"+size+"}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":"+ fields +",\"fuzziness\": \"AUTO\"}}," +
                    "\"from\":"+from+",\"size\":"+size+",\"_source\": "+ ConvertUtils.collection2String(rtnFields)+"}";
        }

        return query(query);
    }

    /**
     * 匹配短语查询-----匹配短语查询要求查询字符串中的trems要么都出现Document中、要么trems按照输入顺序依次出现在结果中.
     * 在默认情况下，查询输入的trems必须在搜索字符串紧挨着出现，否则将查询不到。通过指定slop间隔多少个单词仍然能够搜索到。
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param slop - 指定slop参数，来控制输入的trems之间有多少个单词仍然能够搜索到
     * @param docFields - 从文档中那些字段匹配
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String phraseQuery(String content, int from , int size, int slop,  Set<String> docFields, Set<String> rtnFields) throws IOException {

        String fields = (docFields == null || docFields.size() == 0) ? "[\"_all\"]" : ConvertUtils.collection2String(docFields);

        String query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":"+ fields +",\"type\": \"phrase\", \"slop\": "+slop+"}}," +
                "\"from\":" + from + ",\"size\":"+size+"}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"multi_match\":{\"query\":\"" + content+"\",\"fields\":"+ fields +",\"type\": \"phrase\", \"slop\": "+slop+"}}," +
                    "\"from\":"+from+",\"size\":"+size+",\"_source\": "+ ConvertUtils.collection2String(rtnFields)+"}";
        }

        return query(query);
    }

    /**
     * 匹配短语前缀查询-----匹配短语前缀查询可以指定单词的一部分字符前缀即可查询到该单词
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param docField - 文档中的字段（文本字段text类型）
     * @param slop - 指定slop参数，来控制输入的trems之间有多少个单词仍然能够搜索到
     * @param max_expansions - max_expansions参数限制被匹配到的terms数量来减少资源的使用
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String phrasePrefixQuery(String content, int from , int size,
                                    String docField, int slop, int max_expansions, Set<String> rtnFields) throws IOException {

        if((docField == null || "".equals(docField))) return null;

        String query = "{\"query\":{\"match_phrase_prefix\":{\"" + docField + "\":" +
                            "{\"query\":\""+content+"\",\"slop\":"+ slop +",\"max_expansions\": " + max_expansions +"}}}," +
                            "\"from\":" + from + ",\"size\": " + size+ "}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"match_phrase_prefix\":{\"" + docField + "\":" +
                    "{\"query\":\""+content+"\",\"slop\":"+ slop +",\"max_expansions\": " + max_expansions +"}}}," +
                    "\"from\":" + from + ",\"size\": " + size+ ",\"_source\": "+ ConvertUtils.collection2String(rtnFields)+"}";
        }

        return query(query);
    }

    /**
     * 通配符查询 -------eg: t* : *将会匹配零个或者多个字符
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param docField - 从文档中那个字段匹配
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String wildcardQuery(String content, int from , int size, String docField, Set<String> rtnFields) throws IOException {
        if(docField == null || "".equals(docField)) return null;

        String query = "{\"query\":{\"wildcard\":{\""+ docField +"\":\""+content+"\"}}," +
                "\"from\":"+from+",\"size\":"+size+"}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"wildcard\":{\""+ docField +"\":\""+content+"\"}}," +
                    "\"from\":"+from+",\"size\":"+size+",\"_source\":"+ ConvertUtils.collection2String(rtnFields)+"}";
        }

        return query(query);
    }

    /**
     * 正则表达式查询 -------eg: t[a-z]*y
     * @param content - 待查询字符串内容
     * @param from - 起始拉取位置
     * @param size - 拉取条数
     * @param docField - 从文档中那个字段匹配
     * @param rtnFields - 返回字段
     * @return
     * @throws IOException
     */
    public String regexpQuery(String content, int from , int size, String docField, Set<String> rtnFields) throws IOException {
        if(docField == null || "".equals(docField)) return null;

        String query = "{\"query\":{\"regexp\":{\""+ docField +"\":\""+content+"\"}}," +
                "\"from\":"+from+",\"size\":"+size+"}";

        if(rtnFields != null && rtnFields.size() > 0){
            query = "{\"query\":{\"regexp\":{\""+ docField +"\":\""+content+"\"}}," +
                    "\"from\":"+from+",\"size\":"+size+",\"_source\":"+ ConvertUtils.collection2String(rtnFields)+"}";
        }

        return query(query);
    }
}
