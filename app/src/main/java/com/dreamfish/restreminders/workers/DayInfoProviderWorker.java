package com.dreamfish.restreminders.workers;

public class DayInfoProviderWorker implements IWorker {
  public static final int NAME = 2;

  private static DayInfoProviderWorker instance;

  public static DayInfoProviderWorker getInstance() { return instance; }

  public DayInfoProviderWorker() {
    instance = this;
  }

  public boolean getTodayWorkday() {
    return false;
  }
  public boolean getTodayHoliday() {
    return false;
  }
}
