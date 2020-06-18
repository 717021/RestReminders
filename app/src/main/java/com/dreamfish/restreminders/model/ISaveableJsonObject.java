package com.dreamfish.restreminders.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 保存读取JSON 接口
 */
public interface ISaveableJsonObject {

  /**
   * 从 JSON 字符串读取当前数据
   * @param jsonString JSON 字符串
   */
  void loadFromJson(String jsonString);

  /**
   * 从 JSON 对象读取当前数据
   * @param jsonObject JSON 对象
   */
  void loadFromJson(JSONObject jsonObject);

  /**
   * 保存对象为 JSON
   */
  JSONObject saveToJson();
}
