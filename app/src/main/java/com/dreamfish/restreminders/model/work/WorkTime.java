package com.dreamfish.restreminders.model.work;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.restreminders.model.ISaveableJsonObject;
import com.dreamfish.restreminders.model.auto.DayRepeatType;

import java.util.Date;

/**
 * 工作时间段数据
 */
public class WorkTime implements ISaveableJsonObject {


  private Date startTime;
  private Date endTime;
  private boolean enabled;
  private DayRepeatType repeat;

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
  public boolean isEnabled() {
    return enabled;
  }
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  public DayRepeatType getRepeat() {
    return repeat;
  }
  public void setRepeat(DayRepeatType repeat) {
    this.repeat = repeat;
  }
  public Date getStartTime() {
    return startTime;
  }
  public Date getEndTime() {
    return endTime;
  }

  public WorkTime(String jsonString) {
    loadFromoJson(jsonString);
  }
  public WorkTime(JSONObject jsonObject) {
    loadFromoJson(jsonObject);
  }
  public WorkTime(Date startTime, Date endTime, boolean enable, DayRepeatType repeatType) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.enabled = enable;
    this.repeat = repeatType;
  }

  //加载与保存

  @Override
  public void loadFromoJson(String jsonString) {
    loadFromoJson(JSON.parseObject(jsonString));
  }
  @Override
  public void loadFromoJson(JSONObject jsonObject) {
    this.startTime = jsonObject.getDate("startTime");
    this.endTime = jsonObject.getDate("endTime");
    this.enabled = jsonObject.getBoolean("enabled");
    repeat = new DayRepeatType(jsonObject.getString("repeat"));
  }
  @Override
  public JSONObject saveToJson() {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("startTime", startTime);
    jsonObject.put("endTime", endTime);
    jsonObject.put("enabled", enabled);
    jsonObject.put("repeat", repeat.saveToJson());

    return jsonObject;
  }



}
