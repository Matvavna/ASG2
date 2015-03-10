package cs455.harvester.task;

/*
*Author: Tiger Barras
*Task.java
*Reads one webpage
*/

import cs455.harvester.threadpool.Worker;
import cs455.harvester.crawler.Parser;
import cs455.harvester.task.PrintMessageTask;

import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;

public class CrawlTask implements Task{

	private String url;
	private final int recursionLevel;
	private final String workingDirectory;
	private Worker worker;

	public CrawlTask(String s, int rl, String _workingDirectory){
		url = s;
		recursionLevel = rl;
		workingDirectory = _workingDirectory;
	}//End constructor


	//This is what the Worker calls
	//All the actual work kicks off right here
	public ArrayList<Task> execute(){
		//Fix URL if it's a redirect
		this.redirectBaseUrl();
		System.out.println("Crawling " + this.url);

		//Set up directory and files
		this.initDirectory(this.generateNodePath(this.url));

		Parser parser = new Parser(this.url);
		ArrayList<Task> newTasks = new ArrayList<Task>();

		//This returns a list of all the absolute links on the page from the listed domains
		ArrayList<String> urlStrings = parser.parseFully();

		//System.out.println("Printing urls");
		for(String pageUrl : urlStrings){

			//Update edges of the graph
			this.updateGraph(pageUrl);

			//System.out.println(pageUrl);
			//Check if the URL point to somewhere in this domain
			try{
				if(this.checkDomain(pageUrl)){
					newTasks.add(new CrawlTask(pageUrl, this.recursionLevel+1, this.workingDirectory));
				}else{
					//Send task to other crawler
				}
			}catch(MalformedURLException exception){
				System.out.println("CrawlTask: Error checking domain");
				System.out.println(exception);
			}
			// this.worker.addTask(new PrintMessageTask(pageUrl));
		}

		return newTasks;
	}//End execute

	private boolean checkDomain(String pageUrl)throws MalformedURLException{
		boolean sameDomain = new URL(pageUrl).getHost().equals(new URL(this.url).getHost());
		if(!sameDomain){
			String psychDomain = "http://www.colostate.edu/Depts/Psychology";
			sameDomain = (pageUrl.startsWith(psychDomain) && this.url.startsWith(psychDomain));
		}

		return sameDomain;
	}//End checkDomain

	private void redirectBaseUrl(){
		try{
			this.url = Parser.resolveRedirect(this.url);
		}catch(IOException exception){
			System.out.println("CrawlTask: Error resolving redirect for base URL");
			System.out.println(exception);
		}
	}

	//Set this before execution of any task
	//This is what allows a task to talk back to the worker
	public void setWorker(Worker _worker){
		worker = _worker;
	}//End setWorker

	public boolean equals(Object o){
		//System.out.println("equals");

		if(!(o instanceof Task)) return false;
		if(o == this) return true;

		int otherTaskHash = ((Task) o).hashCode();
		return otherTaskHash == this.hashCode();
	}//End equals

	public String getType(){
		return "CrawlTask";
	}//End getType

	//Punt hashCode to toString
	public int hashCode(){
		//System.out.println("hashCode");
		return this.toString().hashCode();
	}//End getHash

	private static String urlToPath(String s){
		//Chop of leading http://
		if(s.startsWith("http://")){
			s = s.substring(7,s.length());
		}
		//Remove trailing '/'
		if(s.substring(s.length()-1, s.length()).equals("/")){
			s = s.substring(0,s.length()-1);
		}
		//Remove any '/'s and replace them with periods.
		//This should only do anything to the psych domain
		s = s.replaceAll("/", "-");

		return s;
	}//End urlToPath

	private String generateNodePath(String nodeUrl){
		String cleanedUrl = urlToPath(nodeUrl);

		return this.workingDirectory + "/nodes/" + cleanedUrl;
	}//End GenerateNodePath

	private void initDirectory(String path){
		File dir = new File(path);
		boolean created = false;
		try{
			created = dir.mkdir();
		}catch(SecurityException exception){
			System.out.println("Crawler: Error creating directory. Security Exception");
			System.out.println(exception);
		}

		if(created || dir.exists()){
			System.out.println("Directory at " + dir + " exists or was created");
		}else{
			System.out.println("Directory not created");
			System.out.println(dir);
			System.out.println("Check to make sure /tmp/cs455-wbarras exists");
			System.exit(-1);
		}

		File in = new File(path + "/in");
		File out = new File(path + "/out");

		try{
			in.createNewFile();
			out.createNewFile();
		}catch(SecurityException exception){
			System.out.println("Crawler: Error creating file. Security Exception");
			System.out.println(exception);
		}catch(IOException exception){
			System.out.println("CrawlTask: Error creating file");
			System.out.println(exception);
		}
	}//End initDirectory

	private void updateGraph(String newEdge){
			//Add this to the current node's out file
			this.updateOut(newEdge);
			//Add this to the new edge's in file
			this.updateIn(newEdge);
	}//End updateGraphs

	private void updateOut(String newEdge){
		//Add URL to this page's out file
		String pathToOutFile = this.generateNodePath(this.url)+"/out";
		try{
			PrintWriter outFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(pathToOutFile,true)));
			outFileWriter.println(newEdge);
			outFileWriter.flush();
		}catch(FileNotFoundException exception){
			System.out.println("CrawlTask: Error creating out file");
			System.out.println(exception);
			System.exit(-1);
		}catch(SecurityException exception){
			System.out.println("CrawlTask: Error creating out file");
			System.out.println(exception);
			System.exit(-1);
		}catch(IOException exception){
			System.out.println("CrawlTask: Error creating out file");
			System.out.println(exception);
			System.exit(-1);
		}
	}//End updateOut

	private void updateIn(String newEdge){
		String pathToOutFile = this.generateNodePath(newEdge)+"/in";
		try{
			PrintWriter outFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(pathToOutFile,true)));
			outFileWriter.println(this.url);
			outFileWriter.flush();
		}catch(FileNotFoundException exception){
			System.out.println("CrawlTask: Error creating out file");
			System.out.println(exception);
			System.exit(-1);
		}catch(SecurityException exception){
			System.out.println("CrawlTask: Error creating out file");
			System.out.println(exception);
			System.exit(-1);
		}catch(IOException exception){
			System.out.println("CrawlTask: Error creating out file");
			System.out.println(exception);
			System.exit(-1);
		}
	}

	public String toString(){
		return "CrawlTask:"+url;
	}//End toString

}//End class
