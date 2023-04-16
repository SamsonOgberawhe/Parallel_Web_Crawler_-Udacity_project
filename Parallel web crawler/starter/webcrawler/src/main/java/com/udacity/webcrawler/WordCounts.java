package com.udacity.webcrawler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class that sorts the map of word counts.
 *
 * <p>TODO: Reimplement the sort() method using only the Stream API and lambdas and/or method
 *          references.
 */
final class WordCounts {

  /**
   * Given an unsorted map of word counts, returns a new map whose word counts are sorted according
   * to the provided {@link WordCountComparator}, and includes only the top
   * {@param popluarWordCount} words and counts.
   *
   * <p>TODO: Reimplement this method using only the Stream API and lambdas and/or method
   *          references.
   *
   * @param wordCounts       the unsorted map of word counts.
   * @param popularWordCount the number of popular words to include in the result map.
   * @return a map containing the top {@param popularWordCount} words and counts in the right order.
   */
  static Map<String, Integer> sort(Map<String, Integer> wordCounts, int popularWordCount) {

    // TODO: Reimplement this method using only the Stream API and lambdas and/or method references.

    /*PriorityQueue<Map.Entry<String, Integer>> sortedCounts =
        new PriorityQueue<>(wordCounts.size(), new WordCountComparator());
    sortedCounts.addAll(wordCounts.entrySet());
    Map<String, Integer> topCounts = new LinkedHashMap<>();
    for (int i = 0; i < Math.min(popularWordCount, wordCounts.size()); i++) {
      Map.Entry<String, Integer> entry = sortedCounts.poll();
      topCounts.put(entry.getKey(), entry.getValue());
    }
    return topCounts;*/
    WordCountComparator comparator = new WordCountComparator();
    return wordCounts.entrySet().stream()
            .filter(Objects::nonNull)
            .sorted(comparator)
            .limit(Math.min(popularWordCount, wordCounts.size()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

    //Your new sort method must return a new Map
    //The keys and values should be sorted so that the more frequent words come first
    // If multiple words have the same frequency, prefer longer words rank higher.
    // If multiple words have the same frequency and length, use alphabetical order to break ties
    // (the word that comes first in the alphabet ranks higher).
  }

  /**
   * A {@link Comparator} that sorts word count pairs correctly:
   *
   * <p>
   * <ol>
   *   <li>First sorting by word count, ranking more frequent words higher.</li>
   *   <li>Then sorting by word length, ranking longer words higher.</li>
   *   <li>Finally, breaking ties using alphabetical order.</li>
   * </ol>
   */
  private static final class WordCountComparator implements Comparator<Map.Entry<String, Integer>> {

    @Override
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
      if (!a.getValue().equals(b.getValue())) {
        return b.getValue() - a.getValue();
      }
      if (a.getKey().length() != b.getKey().length()) {
        return b.getKey().length() - a.getKey().length();
      }
      return a.getKey().compareTo(b.getKey());
    }
  }

  private WordCounts() {
    // This class cannot be instantiated
  }


}