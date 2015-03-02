package cs455.harvester.threadpool;

/*
 *Author: Tiger Barras
 *Worker.java
 *Assigned tasks by the ThreadPoolManager
 */

import cs455.harvester.task.Task;
import cs455.harvester.threadpool.ThreadPoolManager;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable{

	//The thread that is running this worker
	Thread wrapperThread = null;

	/*
	 *This is the manager that created this Worker
	 *Need a reference to this so we can pass back new tasks
	 */
	private final ThreadPoolManager manager;

	/*
	 *Don't ever modify this. It should only be wait/notify-ed on
	 *Wait when there's nothing to take out of it.
	 *Maybe notify if adding is implemented in this class(That might all be done in the manager)
	 */
	private final ConcurrentLinkedQueue<Task> tasksToComplete;

	public Worker(ThreadPoolManager _manager, ConcurrentLinkedQueue<Task> _tasksToComplete){
		manager = _manager;
		tasksToComplete = _tasksToComplete;
	}//End constructor


	//Runs until it is given the task to kill itself
	public void run(){
		System.out.println("Worker Starting");
		while(true){
			//Only one Worker can be looking at tasksToComplete at a time
			//Otherwise, isEmpty could end up in an inconsistent state
			synchronized(tasksToComplete){
				//The only time we should hit this statement is if we get woken up by
				  //the manager
				if(!tasksToComplete.isEmpty()){
					System.out.println("Queue not empty. Gon grab meself anotter task mon");

					Task currentTask;

					//Grab a task from the manager
					//This will be null if the list is actually empty
					currentTask = this.requestTask();

					//Make sure a task was actually returned
					//If currenTask is null, that means no task was returned from the manager
					//The continue statement puts is back at the top of the while,
					//  and we'll wait on the task list again
					if(currentTask == null) continue;

					currentTask.setWorker(this);
					currentTask.execute();

					}else{
						try{
							System.out.println("Queue empty. Adding self back to pool");
							this.manager.returnWorkerToPool(this.wrapperThread);
							tasksToComplete.wait();
						}catch(InterruptedException exception){
							System.out.println("Worker: Interrupted");
							System.out.println(exception);
						}
				}
			}//End sychronized block

		}
	}//End run


	public void setWrapperThread(Thread t){
		//Can't be final, but we don't want anyone resetting the thread
		if(this.wrapperThread == null){
			wrapperThread = t;
		}else{
			System.out.println("Worker: Error, wrapperThread already set");
		}
	}//End setWrapperThread

	private Task requestTask(){
		Task newTask = null;
		try{
			newTask = this.manager.getTask();
		}catch(NoSuchElementException exception){
			//Hopefully this branch is never ever reached
			//If it is, then a concurrency issue is causing workers to think the task
			// list has elements when it's actually empty!
			System.out.println("Worker: No task to recieve from ThreadPoolManager");
			System.out.println(exception);
		}
		return newTask;
	}//End requestTask

	public void addTask(Task task){
		synchronized(tasksToComplete){
			tasksToComplete.add(task);
			tasksToComplete.notify();
		}
	}


}//End class
