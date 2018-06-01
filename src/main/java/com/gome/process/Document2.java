package com.gome.process;//package ir;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;


/**
 * This class represents one document.
 * It will keep track of the term frequencies.
 * @author swapneel
 *
 */
public class Document2 implements Comparable<Document2>{

	private static JiebaSegmenter segmenter = new JiebaSegmenter();
	/**
	 * A hashmap for term frequencies.
	 * Maps a term to the number of times this terms appears in this document.
	 */
	private HashMap<String, Integer> termFrequency;

	/**
	 * The name of the file to read.
	 */
	private String str;

	/**
	 * The constructor.
	 * It takes in the name of a file to read.
	 * It will read the file and pre-process it.
	 * @param str the name of the title
	 */
	public Document2(String str) {
		this.str = str;
		termFrequency = new HashMap<String, Integer>();
		readFileAndPreProcess();
	}

	/**
	 * This method will read in the file and do some pre-processing.
	 * The following things are done in pre-processing:
	 * Every word is converted to lower case.
	 * Every character that is not a letter or a digit is removed.
	 * We don't do any stemming.
	 * Once the pre-processing is done, we create and update the
	 */
	private void readFileAndPreProcess() {
		try {
//			List<SegToken> seg = segmenter.process(str, JiebaSegmenter.SegMode.SEARCH);
//			String in = "";
//			for(SegToken segToken : seg){
//				in += segToken.word.getToken() + " ";
//			}
//			in = in.trim();
			String[] word = str.split(" ");

			for(String st : word){
				if (!(st.equalsIgnoreCase(""))) {
					if (termFrequency.containsKey(st)) {
						int oldCount = termFrequency.get(st);
						termFrequency.put(st, ++oldCount);
					} else {
						termFrequency.put(st, 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will return the term frequency for a given word.
	 * If this document doesn't contain the word, it will return 0
	 * @param word The word to look for
	 * @return the term frequency for this word in this document
	 */
	public double getTermFrequency(String word) {
		if (termFrequency.containsKey(word)) {
			return termFrequency.get(word);
		} else {
			return 0;
		}
	}

	/**
	 * This method will return a set of all the terms which occur in this document.
	 * @return a set of all terms in this document
	 */
	public Set<String> getTermList() {
		return termFrequency.keySet();
	}


	/**
	 * @return the filename
	 */
	private String getFileName() {
		return str;
	}


	/**
	 * The overriden method from the Comparable interface.
	 */
	public int compareTo(Document2 other) {
		return str.compareTo(other.getFileName());
	}

	/**
	 * This method is used for pretty-printing a Document2 object.
	 * @return the filename
	 */
	public String toString() {
		return str;
	}


}