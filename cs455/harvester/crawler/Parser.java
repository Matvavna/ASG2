package cs455.harvester.crawler;

/*
 *Author: Tiger Barras
 *Parser.java
 *Uses Jericho to parse webpages and pull links
 */

import net.htmlparser.jericho.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;


public class Parser{

	///have a method to get all the a tags
	//have a method to pull links out of tags


	//This is the main from the example
	//Break it out into useful methods, and then implement in task
	public static void main(String args[]){
		//Disable verbose log statements
		Config.LoggerProvider = LoggerProvider.DISABLED;
		try{
			//Webpage that needs to be parsed
			final String pageUrl = "http://www.cs.colostate.edu/~cs455";
			Source source = new Source(new URL(pageUrl));

			//Get all 'a' tags
			List<Element> aTags = source.getAllElements(HTMLElementName.A);

			//Get the URL href attributes from each 'a' tag
			for(Element aTag : aTags){
				//print the URL
				System.out.println(aTag.getAttributeValue("href"));
			}

		}catch(IOException exception){
			System.out.println("Parser: Error parsing page");
			System.out.println(exception);
		}
	}//End main


}//End parser
