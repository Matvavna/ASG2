package cs455.harvester.task;

/*
 *Author: Tiger Barras
 *Task.java
 *A single unit of work to be done by the Worker
 *Implemented to package different tasks
 */

import cs455.harvester.threadpool.Worker;


public interface Task{

  //This is what the Worker calls
  //All the actual work kicks off right here
  public void execute();

  //Set this before execution of any task
  //This is what allows a task to talk back to the worker
  public void setWorker(Worker worker);

}//End interface
