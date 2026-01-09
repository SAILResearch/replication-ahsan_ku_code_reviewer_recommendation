package com.sail.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TextUtil {

	public static String COMMA_SEPARATOR = ",";
	public static String DASH_SEPARATOR = "-";
	public static String UNDERSCORE_SEPARATOR = "_";
	public static String DOT_SEPERATOR = ".";
	
	public static List<String> convertStringToList(String text, String separator){
		List<String> separatedStringList = new ArrayList<String>();
		
		text = text.replace("[", "").replace("]", "").replace("\'","").trim();
		String splitText[] = text.split(separator);
		
		for(String w : splitText) {
			if(w.trim().length() <= 0) {
				continue;
			}
			separatedStringList.add(w.trim());
		}
		
		return separatedStringList;
	}
	
	public static List<String> convertStringToListPatch(String text, String separator){
		List<String> separatedStringList = new ArrayList<String>();
		
		text = text.replace("[", "").replace("]", "").replace("\'","").trim();
		String splitText[] = text.split(separator);
		
		/*
		 * ['modules/elasticsearch/src/main/java/org/elasticsearch/index/query/json/JsonFilterBuilders.java/JsonFilterBuilders.java', 
		 * 'modules/elasticsearch/src/main/java/org/elasticsearch/index/query/json/JsonQueryParserRegistry.java/JsonQueryParserRegistry.java',
		 * --- we need to remove the file name it is repeated
		 */
		for(String w : splitText) {
			if (w.contains("/")) {
				w = w.substring(0, w.lastIndexOf("/"));
			}
			if(w.trim().length() <= 0) {
				continue;
			}
			if (w.contains(".")) {
				w = w.substring(0, w.lastIndexOf("."));
				w = w.replace("/", ".");
				
			}
			separatedStringList.add(w.trim());
		}
		
		return separatedStringList;
	}
	
	public static String convertListToString(List<String> textList, String seperator) {
		String textString = "[";
		for(int i = 0 ; i < textList.size() ; i ++ ) {
			textString += textList.get(i);
			if (i < textList.size() - 1) {
				textString += seperator;
			}
			
		}
		textString += "]";
		return textString;
	}
	
	public static String convertSetToString(Set<String> textSet, String seperator) {
		String textString = "[";
		ArrayList<String> textList = new ArrayList<String>();
		textList.addAll(textSet);
		for(int i = 0 ; i < textList.size() ; i ++ ) {
			textString += textList.get(i);
			if (i < textList.size() - 1) {
				textString += seperator;
			}
			
		}
		textString += "]";
		return textString;
	}
	
	public static Set<String> convertStringToSet(String text, String separator){
		Set<String> separatedStringList = new HashSet<String>();
		
		text = text.replace("[", "").replace("]", "").replace("\'","").trim();
		String splitText[] = text.split(separator);
		
		for(String w : splitText) {
			if(w.trim().length() <= 0) {
				continue;
			}
			separatedStringList.add(w.trim());
		}
		
		return separatedStringList;
	}
	
	
}
