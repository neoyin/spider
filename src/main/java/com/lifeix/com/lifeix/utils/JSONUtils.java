package com.lifeix.com.lifeix.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 对于无法识别的属性，在反序列化时忽略处理
                .configure(SerializationFeature.INDENT_OUTPUT, false) // 缩进格式化输出
                .setVisibility(PropertyAccessor.FIELD, Visibility.ANY) // 序列化操作针对对象属性的可见性范围
                .setVisibility(PropertyAccessor.GETTER, Visibility.NONE) // 只能对public的getter方法过滤
                .setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE) // 对is-setter同样过滤
                .setSerializationInclusion(Include.NON_NULL); // 空值不执行序列化操作
    }

    private JSONUtils() {

    }

    public static ObjectMapper getInstance() {

        return objectMapper;
    }

    /**
     * javaBean,list,array convert to json string
     *
     * @throws JsonProcessingException
     */
    public static String obj2json(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static String obj2jsonNotNull(Object obj) throws JsonProcessingException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * json string convert to javaBean
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static <T> T json2pojo(String jsonStr, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(jsonStr, clazz);
    }

    /**
     * json string convert to map
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static <T> Map<String, Object> json2map(String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(jsonStr, Map.class);
    }

    /**
     * json string convert to map with javaBean
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static <T> Map<String, T> json2map(String jsonStr, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        Map<String, Map<String, Object>> map = objectMapper.readValue(jsonStr,
                new TypeReference<Map<String, T>>() {
                });
        Map<String, T> result = new HashMap<String, T>();
        for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), map2pojo(entry.getValue(), clazz));
        }
        return result;
    }

    /**
     * json array string convert to list with javaBean
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        if (jsonArrayStr == null || jsonArrayStr.isEmpty()) {
            return null ;
        }
        List<Map<String, Object>> list = objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {
        });
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> map : list) {
            result.add(map2pojo(map, clazz));
        }
        return result;
    }

    /**
     * map convert to javaBean
     */
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

}