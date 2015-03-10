package cs455.harvester.crawler;

/*
 *Author: Tiger Barras
 *Crawler.java
 *Crawls webpages looking for links, and slowly build a graph of a domain
 */

import cs455.harvester.threadpool.ThreadPoolManager;
import cs455.harvester.task.*;
import cs455.harvester.communication.ServerThread;
import cs455.harvester.communication.ConnectionCache;
import cs455.harvester.wireformats.Event;

import java.io.File;
import java.io.IOException;

public class Crawler{

	//Basic info on where the crawler is operating
	private final int portnum;
	private final int threadPoolSize;
	private final String rootUrl;
	private final String pathToConfigFile;
	private String rootFilePath;

	//The threadpool manager. All tasks go through this guy
	private ThreadPoolManager manager;

	//Objects that handle all the communication with other crawlers
	private ConnectionCache cache = new ConnectionCache();


	public Crawler(int _portnum, int _threadPoolSize, String _rootUrl, String _pathToConfigFile){
		portnum = _portnum;
		threadPoolSize = _threadPoolSize;
		rootUrl = _rootUrl;
		pathToConfigFile = _pathToConfigFile;
	}//End constructor

	public void beginCrawling(){
		CrawlTask firstTask = new CrawlTask(this.rootUrl, 1, this.rootFilePath);
		manager.addTask(firstTask);
	}//End beginCrawling

	//Starts thread pool and server
	public void initialize(){
		this.startServer();
		this.startThreadPoolManager();
		this.createDirectory();
		this.setupConnections();
	}//End initailize

	private void startThreadPoolManager(){
		System.out.println("Starting thread pool");
		manager = new ThreadPoolManager(this.threadPoolSize);
		Thread managerThread = new Thread(manager);
		managerThread.start();
	}//End startThreadPoolManager

	private void startServer(){
		try{
			ServerThread server = new ServerThread(this.portnum, this);
			server.start();
		}catch(IOException exception){
			System.out.println("Crawler: Error initializing ServerThread");
			System.out.println(exception);
		}

		//Sleep after everything is set up, give the other crawlers a chance to set up
		try{
			Thread.sleep(10000);
		}catch(InterruptedException exception){
			System.out.println("Crawler: Interrupted from sleeping  *yawn*");
		}
	}//End startServer

	private void createDirectory(){
		//Turn the url parameter into a absolute file path under /tmp/wbarras
		this.generateRootFilePath();

		//Make sure /tmp/wbarras exists
		this.createUserDir();

		//Make sure the directory specified by rootFilePath exists
		this.createWorkingDirectory();

		//Set up nodes and disjoint subgraphs directories
		this.createSubDirectories();

	}//End createDirectory

	//Create the /tmp/wbarras dir
	private void createUserDir(){
		File userDir = new File("/tmp/cs455-wbarras");

		boolean created = false;
		try{
			created = userDir.mkdir();
		}catch(SecurityException exception){
			System.out.println("Crawler: Error creating directory. Security Exception");
			System.out.println(exception);
		}

		if(created || userDir.exists()){
			System.out.println("Directory at " + userDir + " exists or was created");
		}else{
			System.out.println("Directory not created");
			System.out.println(userDir);
			System.out.println("Check to make sure /tmp exists");
			System.exit(-1);
		}
	}//End createUserDir

	private void createWorkingDirectory(){
		File parentDirectory = new File(this.rootFilePath);
		boolean created = false;
		try{
			created = parentDirectory.mkdir();
		}catch(SecurityException exception){
			System.out.println("Crawler: Error creating directory. Security Exception");
			System.out.println(exception);
		}

		if(created || parentDirectory.exists()){
			System.out.println("Directory at " + this.rootFilePath + " exists or was created");
		}else{
			System.out.println("Directory not created");
			System.out.println(this.rootFilePath);
			System.out.println("Check to make sure /tmp/cs455-wbarras exists");
			System.exit(-1);
		}
	}//End createWorkingDirectory

	private void createSubDirectories(){
		File nodes = new File(this.rootFilePath + "/nodes");
		File subgraphs = new File(this.rootFilePath + "/disjoint-subgraphs");

		try{
			nodes.mkdir();
			subgraphs.mkdir();
		}catch(SecurityException exception){
			System.out.println("Crawler: Error creating directory. Security Exception");
			System.out.println(exception);
		}

		if(nodes.exists() && subgraphs.exists()){
			System.out.println("Directory at " + this.rootFilePath + "/nodes exists or was created");
			System.out.println("Directory at " + this.rootFilePath + "/disjoint-subgraphs exists or was created");
		}else{
			System.out.println("Directory not created");
			System.out.println(this.rootFilePath);
			System.out.println("Check to make sure /tmp/cs455-wbarras exists");
			System.exit(-1);
		}
	}//End createSubDirectories

	private void generateRootFilePath(){
		//Clean up the rootUrl so there are no '/'s
		String cleanUrl = rootUrl;
		//Chop of leading http://
		if(cleanUrl.startsWith("http://")){
			cleanUrl = cleanUrl.substring(7,cleanUrl.length());
		}
		//Remove trailing '/'
		if(cleanUrl.substring(cleanUrl.length()-1, cleanUrl.length()).equals("/")){
			cleanUrl = cleanUrl.substring(0,cleanUrl.length()-1);
		}
		//Remove any '/'s and replace them with periods.
		//This should only do anything to the psych domain
		cleanUrl = cleanUrl.replaceAll("/", "-");

		this.rootFilePath = "/tmp/cs455-wbarras/"+cleanUrl;
	}//End generateRootFilePath

	public ConnectionCache getConnectionCache(){
		return this.cache;
	}//End getConnectionCache

	public void onEvent(Event event){
		System.out.println("crawler.onEvent()");
		System.out.println(event);
	}//End onEvent


	//Anything past here is static and only used in main()--------------->>><<<

	public static void main(String args[]){

		//Sanity check input
		if(!checkArgs(args)){
			System.exit(-1);
		}

		//pull variables
		int port = Integer.valueOf(args[0]);
		int pool = Integer.valueOf(args[1]);
		//Build Crawler
		Crawler crawler = new Crawler(port, pool, args[2], args[3]);

		//Initialize tpm and server
		crawler.initialize();



		crawler.beginCrawling();

	}//End main


	private static boolean checkArgs(String[] args){
		if(args.length != 4){
			System.out.println("Error: Incorrect number of command line arguments");
			System.out.println("Usage: Crawler <portnum> <thread pool size> <root url> <path to config file>");
			return false;
		}else{
			return true;
		}
	}//Check args
}//End class
