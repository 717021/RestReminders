package com.dreamfish.restreminders;

import android.app.Application;

import com.dreamfish.restreminders.workers.DayInfoProviderWorker;
import com.dreamfish.restreminders.workers.IWorker;
import com.dreamfish.restreminders.workers.IWorkerHost;
import com.dreamfish.restreminders.workers.RestReminderWorker;

import java.util.HashMap;
import java.util.Map;

public class RestRemindersApplication extends Application implements IWorkerHost {

  @Override
  public void onCreate() {
    super.onCreate();

    //初始化几个基本管理器
    addWorker(RestReminderWorker.NAME, new RestReminderWorker());
    addWorker(DayInfoProviderWorker.NAME, new DayInfoProviderWorker());
  }



  // ============================
  // 全局组件管理
  // ============================

  private Map<Integer, IWorker> workers = new HashMap<>();

  @Override
  public IWorker getWorker(int name) {
    return workers.get(name);
  }
  @Override
  public IWorker addWorker(int name, IWorker worker) {
    return workers.put(name, worker);
  }
  @Override
  public void deleteWorker(int name) {
    workers.remove(name);
  }
}
