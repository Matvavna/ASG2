package cs455.harvester.task;

/*
 *Author: Tiger Barras
 *Task.java
 *A single unit of work to be done by the Worker
 *Implemented to package different tasks
 */

public interface Task{

  //This is what the Worker calls
  //All the actual work kicks off right here
  public void execute();

}//End interface
