package cs455.harvester.threadpool;

/*
 *Author: Tiger Barras
 *ThreadPoolManager.java
 *Maintains a queue of worker threads and a queue of tasks, and assigns the tasks to the workers
 */

 import cs455.harvester.threadpool.Worker;

 import java.util.concurrent.ConcurrentLinkedQueue;


public class ThreadPoolManager{

  private final int numberOfThreads;
  private ConcurrentLinkedQueue<Worker> availableWorkers;


  public ThreadPoolManager(int _numberOfThreads){
    numberOfThreads = _numberOfThreads;

    this.initWorkers(this.numberOfThreads);

    this.startWorkers();
  }//End constructor


  //Initializes the availableWorkers queue with the specified amount of Workers
  private void initWorkers(int threadCount){
    this.availableWorkers = new ConcurrentLinkedQueue<Worker>();

    //Add the appropriate number of Workers to the Queue
    for(int i = 0; i < threadCount; i++){
      this.availableWorkers.add(new Worker());
    }

    //Queue should now contain the proper numbers of workers, but they have not yet been started
  }//End initWorkers

  //Starts each of the Workers in the queue
  private void startWorkers(){
    for(Worker workerToStart : this.availableWorkers){
      workerToStart.run();
    }

  }//End startWorkers


  //This main is for the purposes of testing this class only
  //ThreadPoolManager should always be used as an object inside something else
  public static void main(String args[]){
    ThreadPoolManager tpm = new ThreadPoolManager(4);
  }

}//End class
