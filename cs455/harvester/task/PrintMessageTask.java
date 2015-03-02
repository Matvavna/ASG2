package cs455.harvester.task;

/*
 *Author: Tiger Barras
 *Task.java
 *A single unit of work to be done by the Worker
 *Print a message
 */

import cs455.harvester.threadpool.Worker;


public class PrintMessageTask implements Task{

	private final String message;
	private Worker worker;

	public PrintMessageTask(String s){
		message = s;
	}//End constructor


	//This is what the Worker calls
	//All the actual work kicks off right here
	public void execute(){
		System.out.println("PrintMessageTask: " + this.message);
	}//End execute


	public void setWorker(Worker w){
		this.worker = w;
	}//End setWorker

}//End class
