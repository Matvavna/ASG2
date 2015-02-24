package cs455.harvester.task;

/*
 *Author: Tiger Barras
 *ExitTask.java
 *This is the task sent when the manager wants to kill a thread
 */

public class ExitTask{

	//This is what the Worker calls
	//All the actual work kicks off right here
	public void execute(){
		System.out.println("Worker: Exiting");
		System.exit(1);
	}//End execute

}//End class
