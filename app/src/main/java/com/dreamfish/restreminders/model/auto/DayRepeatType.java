package com.dreamfish.restreminders.model.auto;

import android.content.res.Resources;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.restreminders.R;
import com.dreamfish.restreminders.model.ISaveableJsonObject;
import com.dreamfish.restreminders.workers.DayInfoProviderWorker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日重复类型数据
 */
public class DayRepeatType implements ISaveableJsonObject, IDayConditionGroup {

  /**
   * 每天重复
   */
  public final static int REPEAT_EVERY_DAY = 0;
  /**
   * 法定工作日
   */
  public final static int REPEAT_WORKDAY = 1;
  /**
   * 法定节假日
   */
  public final static int REPEAT_HOLIDAY = 2;
  /**
   * 周一至周五
   */
  public final static int REPEAT_MONDAY_TO_FRIDAY = 3;
  /**
   * 周一至周六
   */
  public final static int REPEAT_MONDAY_TO_SATURDAY = 4;
  /**
   * 周日至周五
   */
  public final static int REPEAT_SUNDAY_TO_FRIDAY = 5;
  /**
   * 周一至周六
   */
  public final static int REPEAT_SATURDAY_TO_SUNDAY = 6;
  /**
   * 自定义
   */
  public final static int REPEAT_CUSTOM = 7;
  /**
   * 不重复
   */
  public final static int REPEAT_ONCE = 8;

  private DayInfoProviderWorker dayInfoProviderWorker;

  private void getDayInfoProvider() {
    dayInfoProviderWorker = DayInfoProviderWorker.getInstance();
  }

  //数据
  //======================

  private int repeatType;
  private List<DayCondition> repeatConditions;

  /**
   * 获取重复类型
   */
  public int getRepeatType() {
    return repeatType;
  }
  /**
   * 设置重复类型
   * @param repeatType 重复类型 REPEAT_*
   */
  public void setRepeatType(int repeatType) {
    this.repeatType = repeatType;
  }
  /**
   * 获取重复类型为 REPEAT_CUSTOM 时的自定义条件数组
   */
  public List<DayCondition> getRepeatConditions() {
    return repeatConditions;
  }

  public String getRepeatTypeString(Resources resources) {
    switch (repeatType) {
      case DayRepeatType.REPEAT_CUSTOM: return resources.getString(R.string.text_customize);
      case DayRepeatType.REPEAT_EVERY_DAY: return resources.getString(R.string.text_repeat_everyday);
      case DayRepeatType.REPEAT_HOLIDAY: return resources.getString(R.string.text_repeat_holiday);
      case DayRepeatType.REPEAT_MONDAY_TO_FRIDAY:return resources.getString(R.string.text_repeat_mon_to_fir);
      case DayRepeatType.REPEAT_MONDAY_TO_SATURDAY: return resources.getString(R.string.text_repeat_mon_to_sat);
      case DayRepeatType.REPEAT_SATURDAY_TO_SUNDAY: return resources.getString(R.string.text_repeat_sat_to_sun);
      case DayRepeatType.REPEAT_SUNDAY_TO_FRIDAY: return resources.getString(R.string.text_repeat_sun_to_fir);
      case DayRepeatType.REPEAT_WORKDAY: return resources.getString(R.string.text_repeat_workday);
      case DayRepeatType.REPEAT_ONCE: return resources.getString(R.string.text_repeat_once);
    }
    return "";
  }

  public DayRepeatType(int repeatType) {
    this.repeatType = repeatType;
    this.repeatConditions = new ArrayList<>();
    getDayInfoProvider();
  }
  public DayRepeatType(String jsonString) {
    loadFromJson(jsonString);
    getDayInfoProvider();
  }

  /**
   * 检查今天是否条件符合
   */
  public boolean checkTodayShouldRepeat(Date lastEditDay) {
    Calendar cal = Calendar.getInstance();
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    switch (repeatType) {
      case REPEAT_EVERY_DAY: return true;
      case REPEAT_WORKDAY: return dayInfoProviderWorker.getTodayWorkday();
      case REPEAT_HOLIDAY: return dayInfoProviderWorker.getTodayHoliday();
      case REPEAT_MONDAY_TO_FRIDAY:
        return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY;
      case REPEAT_MONDAY_TO_SATURDAY:
        return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.SATURDAY;
      case REPEAT_SUNDAY_TO_FRIDAY:
        return dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.FRIDAY;
      case REPEAT_SATURDAY_TO_SUNDAY:
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
      case REPEAT_CUSTOM:
        for(DayCondition dayCondition : repeatConditions)
          if(dayCondition.checkMet())
            return true;
        break;
      case REPEAT_ONCE:
        Calendar lastEditDayCal = Calendar.getInstance();
        lastEditDayCal.setTime(lastEditDay);
        if(lastEditDayCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                && lastEditDayCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                && lastEditDayCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR))
          return true;
        break;
    }

    return false;
  }

  //加载与保存
  //======================

  @Override
  public void loadFromJson(String jsonString) {
    loadFromJson(JSON.parseObject(jsonString));
  }
  @Override
  public void loadFromJson(JSONObject jsonObject) {
    this.repeatConditions = new ArrayList<>();
    this.repeatType = jsonObject.getInteger("repeatType");
    JSONArray jsonArray = jsonObject.getJSONArray("repeatConditions");

    for(int i = 0; i < jsonArray.size(); i++) {
      repeatConditions.add(new DayCondition(jsonArray.getJSONObject(i)));
    }
  }
  @Override
  public JSONObject saveToJson() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("repeatType", repeatType);

    JSONArray jsonArray = new JSONArray();
    for(int i = 0; i < repeatConditions.size(); i++) {
      jsonArray.add(repeatConditions.get(i).saveToJson());
    }
    jsonObject.put("repeatConditions", jsonArray);

    return jsonObject;
  }

  @Override
  public List<DayCondition> getDayConditions() {
    return getRepeatConditions();
  }
  @Override
  public void setDayConditions(List<DayCondition> newList) {
    repeatConditions.clear();
    repeatConditions.addAll(newList);
  }
}
