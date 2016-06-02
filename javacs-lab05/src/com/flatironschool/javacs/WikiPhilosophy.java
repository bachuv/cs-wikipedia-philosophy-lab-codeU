package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
    static int parens = 0;
    
    //list of strings of the visited urls
    static List<String> visitedUrls = new ArrayList<String>();
    
    
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
        System.out.println("main");
        
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String targetUrl = "https://en.wikipedia.org/wiki/Philosophy";
        //add the starting url to the visited list
        visitedUrls.add(url);
        boolean finished = false;
        boolean reachedTarget = false;
        
        while(!finished){
            
            Elements paragraphs = wf.fetchWikipedia(url);
            Element firstPara = paragraphs.get(0);
		
            Iterable<Node> iter = new WikiNodeIterable(firstPara);
        
            for (Node node: iter) {
                System.out.println(node.toString());
                if (node instanceof TextNode) {
                    if(((TextNode)node).text().contains("(")){
                        //add 1 when you see an open parenthese
                        parens++;
                    }
                    if(((TextNode)node).text().contains(")")){
                        //subtract 1 when you see an open parenthese
                        parens--;
                    }
                    //the parentheses will cancel each other out at the end
                }
                if (node instanceof Element){
                    System.out.println(node.toString());
                    String currURL = ((Element)node).attr("abs:href");
                    if(isValid((Element)node, url)){
                        System.out.println("is valid");
                        if(visitedUrls.contains(currURL)){
                            finished = true;
                        }
                        System.out.println("current url");
                        System.out.println(currURL);
                        visitedUrls.add(currURL);
                        url = currURL;
                        if(visitedUrls.contains(targetUrl)){
                            System.out.println("is target url");
                            finished = true;
                            reachedTarget = true;
                        }
                        break;
                    }
                }
            }
        }

        if(reachedTarget){
            System.out.println("SUCCESS: Page has been found!");
        }else{
            System.out.println("FAILURE: Page was not found.");
        }
    
        for(String tempURL: visitedUrls){
            System.out.println(tempURL);
        }
	}
    
    public static boolean isValid(Element e, String url){
        //get the text from the element
        String curr = e.attr("abs:href");
        
        //check the element is not an empty string
        if(curr == ""){
            return false;
        }
        
        //check if current link
        if(curr.equals(url)){
            return false;
        }
        
        //check for italics by going up the parent links
        Element parent = e;
        while(parent != null){
            if(parent.tagName().equals("i") || parent.tagName().equals("em")){
                return false;
            }
            parent = parent.parent();
        }
        
        //check for parentheses
        if(parens != 0){
            return false;
        }
        
        //visitedUrls.add(curr);
        return true;
    }
}
