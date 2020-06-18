package com.dreamfish.restreminders.model.auto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.restreminders.model.ISaveableJsonObject;

import java.util.Calendar;
import java.util.Date;

/**
 * 日条件数据
 */
public class DayCondition implements ISaveableJsonObject {

  public final static int TYPE_DATE = 1;
  public final static int TYPE_DAY_OF_WEEK = 0;

  //数据
  //======================

  private int type = TYPE_DAY_OF_WEEK;
  private Date date;
  private int dayOfWeek;

  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public int getDayOfWeek() {
    return dayOfWeek;
  }
  public void setDayOfWeek(int dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public DayCondition(Date date) {
    this.date = date;
    this.type = TYPE_DATE;
  }
  public DayCondition(int dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }
  public DayCondition(String jsonString) {
    loadFromoJson(jsonString);
  }
  public DayCondition(JSONObject jsonObject) {
    loadFromoJson(jsonObject);
  }

  //加载与保存
  //======================

  @Override
  public void loadFromoJson(String jsonString) {
    loadFromoJson(JSON.parseObject(jsonString));
  }
  @Override
  public void loadFromoJson(JSONObject jsonObject) {
    type = jsonObject.getInteger("type");
    date = jsonObject.getDate("date");
    dayOfWeek = jsonObject.getInteger("dayOfWeek");
  }
  @Override
  public JSONObject saveToJson() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("type", type);
    jsonObject.put("date", date);
    jsonObject.put("dayOfWeek", dayOfWeek);
    return jsonObject;
  }


  /**
   * 检查今天是否符合条件
   */
  public boolean checkMet() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());

    if(type == TYPE_DAY_OF_WEEK) {
      return dayOfWeek == cal.get(Calendar.DAY_OF_WEEK);
    }else if(type == TYPE_DATE) {

      Calendar calendar2 = Calendar.getInstance();
      calendar2.setTime(date);

      int day1 = cal.get(Calendar.DAY_OF_YEAR);
      int day2 = calendar2.get(Calendar.DAY_OF_YEAR);
      int year1 = cal.get(Calendar.YEAR);
      int year2 = calendar2.get(Calendar.YEAR);

      return year1 == year2 && day1 == day2;
    }
    return false;
  }
}
