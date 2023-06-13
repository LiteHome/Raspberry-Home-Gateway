package com.rashome.gateway.commons.util;


import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.rashome.gateway.commons.exception.IotGatewayException;

public class JsonUtil {
    

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();

    // private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 序列化对象, 返回空如果序列化失败
     * @param <T>
     * @param object
     * @return
     * @throws IotGatewayException
     */
    public static <T> String toJsonString(T object) throws IotGatewayException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IotGatewayException(String.format("序列化失败, payload 是 %s", object.toString()), e);
        }
    }

    /**
     * 从字节数组反序列化, 返回空如果失败
     * @param <T>
     * @param payload
     * @param clazz
     * @return
     * @throws IotGatewayException
     */
    public static <T> T byteArrayToObject(byte[] payload, Class<T> clazz) throws IotGatewayException {
        
        try {
            return OBJECT_MAPPER.readValue(payload, clazz);
        } catch (IOException e) {
            throw new IotGatewayException(String.format("反序列化失败, payload 是 %s", payload), e);
        }
    }

    /**
     * 从字符串反序列化, 返回空如果失败
     * @param <T>
     * @param payload
     * @param clazz
     * @return
     * @throws IotGatewayException
     */
    public static <T> T stringToObject(String payload, Class<T> clazz) throws IotGatewayException {
        
        try {
            return OBJECT_MAPPER.readValue(payload, clazz);
        } catch (IOException e) {
            throw new IotGatewayException(String.format("反序列化失败, payload 是 %s", payload), e);
        }
    }

    /**
     * 从字符串反序列化集合, 返回空如果失败
     * @param <T>
     * @param payload
     * @param clazz
     * @return
     * @throws IotGatewayException
     */
    public static <T> List<T> stringToArrayObject(String payload, Class<T> clazz) throws IotGatewayException {
        
        try {
            return OBJECT_MAPPER.readValue(payload, TYPE_FACTORY.constructCollectionLikeType(List.class, clazz));
        } catch (IOException e) {
            throw new IotGatewayException(String.format("反序列化失败, payload 是 %s", payload), e);
        }
    }

    public static void writeToFile(Object source, String filePath) throws IotGatewayException {

        try {
            OBJECT_MAPPER.writeValue(new File(filePath), source);
        } catch (IOException e) {
            throw new IotGatewayException("无法保存到文件", e);
        }
    }

    public static <T> T readFromFile(Class<T> clazz, String filePath) throws IotGatewayException {

        try {
            return OBJECT_MAPPER.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            throw new IotGatewayException("无法从文件读取 json", e);
        }
    }
}
