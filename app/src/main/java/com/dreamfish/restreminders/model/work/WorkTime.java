package com.dreamfish.restreminders.model.work;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.restreminders.model.ISaveableJsonObject;
import com.dreamfish.restreminders.model.auto.DayRepeatType;

import java.util.Calendar;
import java.util.Date;

/**
 * 工作时间段数据
 */
public class WorkTime implements ISaveableJsonObject {

  private Date lastEditDay;
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
  public Date getLastEditDay() {
    return lastEditDay;
  }
  public void setLastEditDay(Date lastEditDay) {
    this.lastEditDay = lastEditDay;
  }

  public WorkTime(String jsonString) {
    loadFromJson(jsonString);
  }
  public WorkTime(JSONObject jsonObject) {
    loadFromJson(jsonObject);
  }
  public WorkTime(Date startTime, Date endTime, boolean enable, DayRepeatType repeatType) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.enabled = enable;
    this.repeat = repeatType;
    this.lastEditDay = new Date();
  }

  //加载与保存

  @Override
  public void loadFromJson(String jsonString) {
    loadFromJson(JSON.parseObject(jsonString));
  }
  @Override
  public void loadFromJson(JSONObject jsonObject) {
    this.startTime = jsonObject.getDate("startTime");
    this.endTime = jsonObject.getDate("endTime");
    this.enabled = jsonObject.getBoolean("enabled");
    this.lastEditDay = jsonObject.getDate("lastEditDay");
    repeat = new DayRepeatType(jsonObject.getString("repeat"));
  }
  @Override
  public JSONObject saveToJson() {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("startTime", startTime);
    jsonObject.put("endTime", endTime);
    jsonObject.put("enabled", enabled);
    jsonObject.put("lastEditDay", lastEditDay);
    jsonObject.put("repeat", repeat.saveToJson());

    return jsonObject;
  }

  //判断测试


  /**
   * 检查条件是否成立
   * @param checkTime 是否检查现在时间是否成立，否则只查询今天时间是否成立
   */
  public boolean checkMet(boolean checkTime, boolean checkEnable) {
    if(!enabled && checkEnable) return false;
    if(!repeat.checkTodayShouldRepeat(lastEditDay)) return false;
    if(checkTime) {

      Calendar calStart = Calendar.getInstance();
      Calendar calNow = Calendar.getInstance();
      Calendar calEnd = Calendar.getInstance();

      calStart.setTime(startTime);
      calStart.set(Calendar.YEAR, 2000);
      calStart.set(Calendar.MONTH, 0);
      calStart.set(Calendar.DAY_OF_YEAR, 1);
      calStart.set(Calendar.SECOND, 0);

      calEnd.setTime(endTime);
      calEnd.set(Calendar.YEAR, 2000);
      calEnd.set(Calendar.MONTH, 0);
      calEnd.set(Calendar.DAY_OF_YEAR, 1);
      calEnd.set(Calendar.SECOND, 0);

      calNow.set(Calendar.YEAR, 2000);
      calNow.set(Calendar.MONTH, 0);
      calNow.set(Calendar.DAY_OF_YEAR, 1);
      calNow.set(Calendar.SECOND, 0);

      return (calStart.compareTo(calNow) <= 0 && calNow.compareTo(calEnd) <= 0);
    }
    return true;
  }

}
