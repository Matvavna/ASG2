package cs455.harvester.task;

/*
 *Author: Tiger Barras
 *AddTaskTask.java
 *A single unit of work to be done by the Worker
 *Redundant test task
 */

import cs455.harvester.threadpool.Worker;


public class AddTaskTask implements Task{

	private final Task task;
	private Worker worker;

	public AddTaskTask(Task t){
		task = t;
	}//End constructor

	//This is what the Worker calls
	//All the actual work kicks off right here
	@Override
	public void execute(){
		System.out.println("Task adding task");
		this.worker.addTask(this.task);
	}

	public void setWorker(Worker w){
		this.worker = w;
	}//End setWorker

}//End class
