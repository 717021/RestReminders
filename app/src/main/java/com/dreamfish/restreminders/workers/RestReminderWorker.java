package com.dreamfish.restreminders.workers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dreamfish.restreminders.model.work.WorkTime;

import java.util.ArrayList;
import java.util.List;

public class RestReminderWorker implements IWorker {

  public static final int NAME = 1;

  private int workUrgency = 3;
  private int workOnceTimeMax = 110;
  private int workOnceTimeMin = 45;
  private int restOnceTimeMax = 25;
  private int restOnceTimeMin = 15;
  private List<WorkTime> workTimes = new ArrayList<>();

  public int getWorkTimesCount() {
    return workTimes.size();
  }
  public List<WorkTime> getWorkTimes() {
    return workTimes;
  }
  public WorkTime getWorkTimeAt(int index) {
    return this.workTimes.get(index);
  }
  public void addWorkTime(WorkTime workTime) {
    this.workTimes.add(workTime);
  }
  public void removeAllWorkTimes() {
    this.workTimes.clear();
  }
  public void removeWorkTime(WorkTime workTime) {
    this.workTimes.remove(workTime);
  }

  public int getWorkUrgency() {
    return workUrgency;
  }
  public void setWorkUrgency(int workUrgency) {
    this.workUrgency = workUrgency;
  }
  public int getWorkOnceTimeMax() {
    return workOnceTimeMax;
  }
  public void setWorkOnceTimeMax(int workOnceTimeMax) {
    this.workOnceTimeMax = workOnceTimeMax;
  }
  public int getWorkOnceTimeMin() {
    return workOnceTimeMin;
  }
  public void setWorkOnceTimeMin(int workOnceTimeMin) {
    this.workOnceTimeMin = workOnceTimeMin;
  }
  public int getRestOnceTimeMax() {
    return restOnceTimeMax;
  }
  public void setRestOnceTimeMax(int restOnceTimeMax) {
    this.restOnceTimeMax = restOnceTimeMax;
  }
  public int getRestOnceTimeMin() {
    return restOnceTimeMin;
  }
  public void setRestOnceTimeMin(int restOnceTimeMin) {
    this.restOnceTimeMin = restOnceTimeMin;
  }

  //加载保存工作时间段
  public void loadWorkTimes(String json) {
    JSONArray jsonArray = JSON.parseArray(json);
    for(int i = 0, c = jsonArray.size(); i < c; i++) {
      WorkTime workTime = new WorkTime(jsonArray.getJSONObject(i));
      workTimes.add(workTime);
    }
  }
  public String saveWorkTimes() {
    JSONArray jsonArray = new JSONArray();
    for(int i = 0, c = workTimes.size(); i < c; i++)
      jsonArray.add(workTimes.get(i).saveToJson());
    return jsonArray.toJSONString();

  }

}
