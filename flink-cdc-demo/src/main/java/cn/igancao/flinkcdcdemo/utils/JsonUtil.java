package cn.igancao.flinkcdcdemo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class JsonUtil {

    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        return parseJsonObject(jsonObject, clazz);
    }

    private static <T> T parseJsonObject(JSONObject jsonObject, Class<T> clazz) {
        T obj = JSON.parseObject(jsonObject.toJSONString(), clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            if (fieldType.equals(Date.class)) {
                // 如果是 Date 类型，转换为指定格式字符串
                String dateStr = jsonObject.getString(field.getName());
                if (dateStr != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(dateStr);
                        field.set(obj, date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (fieldType.equals(LocalDateTime.class)) {
                // 如果是 LocalDateTime 类型，转换为指定格式字符串
                String dateTimeStr = jsonObject.getString(field.getName());
                if (dateTimeStr != null) {
                    try {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dtf);
                        field.set(obj, dateTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return obj;
    }

    public static void main(String[] args) {
        // 示例用法：将 JSON 字符串转换为对象
        String jsonString = "{\"name\":\"John\",\"birthDate\":\"2023-11-15 12:30:45\",\"lastUpdated\":\"2023-11-15 12:30:45\"}";
        MyObject myObject = JsonUtil.parseObject(jsonString, MyObject.class);

        System.out.println(myObject);



    }

    @Data
    public static class MyObject {
        private String name;
        private Date birthDate;
        private LocalDateTime lastUpdated;
    }
}

