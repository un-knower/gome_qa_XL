package com.gome.process;//package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a corpus of document2s.
 * It will create an inverted index for these document2s.
 * @author swapneel
 *
 */
public class Corpus {

	/**
	 * An arraylist of all document2s in the corpus.
	 */
	private ArrayList<Document2> documents;

	/**
	 * The inverted index.
	 * It will map a term to a set of document2s that contain that term.
	 */
	private HashMap<String, Set<Document2>> invertedIndex;

	/**
	 * The constructor - it takes in an arraylist of document2s.
	 * It will generate the inverted index based on the document2s.
	 * @param documents the list of document2s
	 */
	public Corpus(ArrayList<Document2> documents) {
		this.documents = documents;
		invertedIndex = new HashMap<String, Set<Document2>>();

		createInvertedIndex();
	}

	/**
	 * This method will create an inverted index.
	 */
	private void createInvertedIndex() {

		for (Document2 document2 : documents) {
			Set<String> terms = document2.getTermList();

			for (String term : terms) {
				if (invertedIndex.containsKey(term)) {
					Set<Document2> list = invertedIndex.get(term);
					list.add(document2);
				} else {
					Set<Document2> list = new TreeSet<Document2>();
					list.add(document2);
					invertedIndex.put(term, list);
				}
			}
		}
	}

	/**
	 * This method returns the idf for a given term.
	 * @param term a term in a document
	 * @return the idf for the term
	 */
	public double getInverseDocumentFrequency(String term) {
		if (invertedIndex.containsKey(term)) {
			Set<Document2> list = invertedIndex.get(term);
			double size = documents.size();
			double documentFrequency = list.size();

			return Math.log10(size / documentFrequency);
		} else {
			return 0;
		}
	}

	/**
	 * @return the document2s
	 */
	public ArrayList<Document2> getDocuments() {
		return documents;
	}

	/**
	 * @return the invertedIndex
	 */
	public HashMap<String, Set<Document2>> getInvertedIndex() {
		return invertedIndex;
	}
}