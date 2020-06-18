package com.dreamfish.restreminders.workers;

public interface IWorkerHost {
  IWorker getWorker(int name);
  IWorker addWorker(int name, IWorker worker);
  void deleteWorker(int name);
}
