package cs455.harvester.threadpool;

/*
 *Author: Tiger Barras
 *ThreadPoolManager.java
 *Maintains a queue of worker threads and a queue of tasks, and assigns the tasks to the workers
 */

import cs455.harvester.threadpool.Worker;
import cs455.harvester.task.Task;
import cs455.harvester.task.ExitTask;
import cs455.harvester.task.AddTaskTask;
import cs455.harvester.task.PrintMessageTask;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;



public class ThreadPoolManager implements Runnable{

  private final int numberOfThreads;

  //This holds references to the Threads running available workers
  //When there is a task to be done, the worker grabs it, and removes itself from the queue
  //When it completes that task, it places itself back at the end of the queue
  private final ArrayList<Thread> availableWorkers = new ArrayList<Thread>();

  //Hold references to tasks waiting to be completed
  //When there is a task to be done, it is grabbed by the next available worker
  private final ConcurrentLinkedQueue<Task> tasksToComplete = new ConcurrentLinkedQueue<Task>();


  public ThreadPoolManager(int _numberOfThreads){
    numberOfThreads = _numberOfThreads;
  }//End constructor


  public void run(){
    //Fill availableWorkers with workerThreads
    this.initWorkers(this.numberOfThreads);
    //Start all the workers
    this.startWorkers();

    //Want to just sit in here until the thread gets killed
    while(true){
      //System.out.println("Loop");
    //  synchronized(tasksToComplete){
    //
    //  }
    }
  }//End run


  //Initializes the proper amount of Workers, and assigns each one to a Thread
  //The Threads are placed in the availableWorkers Queue
  private void initWorkers(int threadCount){
    //Add the appropriate number of Workers to the Queue
    for(int i = 0; i < threadCount; i++){
      String workerName = "Worker:" + i;//Generate a unique name for each thread
      System.out.println("Generating Worker");
      Worker worker = new Worker(this, tasksToComplete);
      System.out.println("Generating thread");
      Thread workerThread = workerThread = new Thread(worker, workerName);
      worker.setWrapperThread(workerThread);
      System.out.println("Adding workerThread " + workerThread.getName());
      this.availableWorkers.add(workerThread);
    }

    //Queue should now contain the proper numbers of threads, but they have not yet been started
  }//End initWorkers

  //Starts each of the Workers in the queue
  private void startWorkers(){
    synchronized(availableWorkers){
      for(Thread workerThreadToStart : this.availableWorkers){
        workerThreadToStart.start();
      }
    }
  }//End startWorkers

  //I don't think this will even need to be used
  private void addTask(Task task){
    synchronized(tasksToComplete){
      this.tasksToComplete.add(task);
      tasksToComplete.notify();
    }
  }//End addTask

  public Task getTask()throws NoSuchElementException{
    //This needs to be synchronized
    //If not, then two Workers could both recieve the last Task in the Queue
    synchronized(tasksToComplete){
      return tasksToComplete.poll();
    }
  }//End getTask

  public void returnWorkerToPool(Thread workerThread){
    synchronized(availableWorkers){
      System.out.println("Returning thread " + workerThread.getName() + " to pool");
      this.availableWorkers.add(workerThread);
    }
  }//End returnToQueue


  //This main is for the purposes of testing this class only
  //ThreadPoolManager should always be used as an object inside something else
  public static void main(String args[]){
    ThreadPoolManager tpm = new ThreadPoolManager(4);
    for(int i = 0; i < 4; i++){
      tpm.addTask(new AddTaskTask(new PrintMessageTask("Hello" + i)));
    }
    Thread managerThread = new Thread(tpm);
    managerThread.start();
  }

}//End class
