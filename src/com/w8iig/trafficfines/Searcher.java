package com.w8iig.trafficfines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;

import com.w8iig.trafficfines.data.DataAbstract;
import com.w8iig.trafficfines.util.UtilString;

class Searcher {

	static private final String TAG = "Searcher";

	private Context mContext;
	private DataAbstract mData;
	private Map<String, String> mNormalizationMapping = new HashMap<String, String>();
	private String[] mSearchTexts;

	Searcher(Context context, DataAbstract data) {
		mContext = context;
		mData = data;

		prepareData();
	}

	List<Integer> search(String query) {
		SortedSet<SearchResult> results = new TreeSet<SearchResult>();
		List<String> words = normalizeQuery(query);

		if (mSearchTexts != null) {
			Log.v(TAG, String.format("search(%s): words=%d, records=%d", query,
					words.size(), mSearchTexts.length));

			for (int i = 0; i < mSearchTexts.length; i++) {
				int score = 0;

				// do single word matching
				for (String word : words) {
					score += getMaxScore(mSearchTexts[i], word, 1);
				}

				// do word pair matching
				if (words.size() > 1) {
					for (int j = 0; j < words.size() - 1; j++) {
						String wordPair = String.format("%s %s", words.get(j), words.get(j + 1));
						score += getMaxScore(mSearchTexts[i], wordPair, 2);
					}
				}

				if (score > 0) {
					results.add(new SearchResult(i, score));
				}
			}

			Log.v(TAG, String.format("search(%s) -> results.size=%d", query,
					results.size()));
		} else {
			Log.e(TAG, "search: mSearchTexts=null");
		}

		List<Integer> resultFineIds = new ArrayList<Integer>();
		int scoreMax = 0;
		for (SearchResult result : results) {
			Log.v(TAG, String.format("search(%s) -> result #%d, score=%d",
					query, result.fineId, result.score));

			if (scoreMax == 0) {
				// this is the first result, it has the highest score
				scoreMax = result.score;
			} else {
				// check to make sure this result's score is not too low
				if (result.score * 2 < scoreMax) {
					continue;
				}
			}

			resultFineIds.add(result.fineId);
		}
		Log.v(TAG, String.format("search(%s) -> resultFineIds.size=%d", query,
				resultFineIds.size()));

		return resultFineIds;
	}

	private List<String> normalizeQuery(String query) {
		List<String> words = new ArrayList<String>();
		String normalized;

		if (null == query) {
			normalized = "";
		} else {
			normalized = UtilString.toLowerCaseAndRemoveAccents(query);
			normalized = normalized.replaceAll("[^a-z0=]", " ");
		}

		StringTokenizer tokenizer = new StringTokenizer(normalized, " ");

		while (tokenizer.hasMoreTokens()) {
			String word = tokenizer.nextToken();

			if (word.isEmpty()) {
				// ignore empty word
				continue;
			}

			if (mNormalizationMapping.containsKey(word)) {
				words.add(mNormalizationMapping.get(word));
			} else {
				words.add(word);
			}
		}

		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(word);
			sb.append(" ");
		}
		Log.v(TAG,
				String.format("normalizeQuery(%s) -> %s", query, sb.toString()));

		return words;
	}

	private int getMaxScore(String text, String word, int factor) {
		int offset = 0;
		int maxScore = 0;

		while (true) {
			Score score = getScore(text, word, factor, offset);
			if (score == null) {
				break;
			}

			maxScore = Math.max(maxScore, score.score);
			offset = score.indexOf + 1;
		}

		return maxScore;
	}

	private Score getScore(String text, String word, int factor, int offset) {
		int indexOf = text.indexOf(word, offset);
		// int textLength = text.length(); // not used for now
		int wordLength = word.length();

		if (indexOf == -1) {
			// not found, no score
			return null;
		}

		// check for previous character, should be non alphabet
		if (indexOf == 0) {
			// good, first character of text
		} else {
			char c = text.charAt(indexOf - 1);
			if (c >= 'a' && c <= 'z') {
				return null;
			}
		}

		// intentionally not check for the next character right after the word
		// this is done to support quick searching by the first character
		// TODO: revisit this decision?

		// TODO: choose a better score calculation
		return new Score(indexOf, (int) Math.pow(wordLength, factor));
	}

	private void prepareData() {
		if (mData != null) {
			mSearchTexts = mData.getSearchTexts(mContext);
		} else {
			Log.e(TAG, "prepareData: mData=null");
		}

		// mapping for normalization
		mNormalizationMapping.put("ko", "khong");
		mNormalizationMapping.put("k", "khong");
		mNormalizationMapping.put("o", "khong");
		mNormalizationMapping.put("0", "khong");
		mNormalizationMapping.put("=", "bang");
		mNormalizationMapping.put("ki", "ky");
	}

	static private class SearchResult implements Comparable<SearchResult> {
		private int fineId;
		private int score;

		private SearchResult(int fineId, int score) {
			this.fineId = fineId;
			this.score = score;
		}

		@Override
		public int compareTo(SearchResult another) {
			if (score == another.score) {
				if (fineId == another.fineId) {
					// this should not happen...
					return 0;
				} else {
					// the lower the id, the higher the result
					return fineId < another.fineId ? -1 : 1;
				}
			} else {
				// the higher the score, the higher the result
				return score < another.score ? 1 : -1;
			}
		}
	}

	static private class Score {
		private int indexOf;
		private int score;

		private Score(int indexOf, int score) {
			this.indexOf = indexOf;
			this.score = score;
		}
	}
}
