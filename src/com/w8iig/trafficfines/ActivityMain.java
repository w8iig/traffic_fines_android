package com.w8iig.trafficfines;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.w8iig.trafficfines.data.DataAbstract;
import com.w8iig.trafficfines.data.DataBike;

public class ActivityMain extends SherlockListActivity implements
		OnItemClickListener {

	static private final String TAG = "ActivityMain";

	private Handler mHandler = new Handler();
	private SearchView mSearchView;

	private DataAbstract mData;
	private AdapterData mListAdapter;
	private Searcher mSearcher;
	private String mSearchQueryPending;
	private TaskSearch mSearchTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// TODO: replace with data specified via preference
		mData = new DataBike();
		mSearcher = new Searcher(this, mData);

		ListView listView = getListView();
		mListAdapter = new AdapterData(this, mData);
		listView.setAdapter(mListAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		mSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		if (null != mSearchView) {
			mSearchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));

			SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
				public boolean onQueryTextChange(String newText) {
					performSearch(newText);
					return true;
				}

				public boolean onQueryTextSubmit(String query) {
					performSearch(query);
					return true;
				}
			};
			mSearchView.setOnQueryTextListener(queryTextListener);
		} else {
			Log.e(TAG, "onCreateOptionsMenu: searchView=null");
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mListAdapter.getCount() == 0) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mSearchView != null) {
						// automatically expand the search view when there is
						// nothing to show (on a cold start or similar)
						// we can use setIconifiedByDefault upon initialization
						// but the widget looks ugly that way...
						mSearchView.setIconified(false);
					}
				}
			}, 100);
		}

		mSearchQueryPending = null;
		mSearchTask = null;
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position,
			long fineId) {
		if (listView == getListView()) {
			listView.requestFocus();
			
			if (mData == null) {
				Log.w(TAG, "onItemClick: mData=null");
			}

			String fineUniqueId = mData.getFineUniqueId((int) fineId);

			Intent intentDoc = new Intent(this, ActivityDoc.class);
			intentDoc.putExtra(ActivityDoc.EXTRA_FINE_UNIQUE_ID, fineUniqueId);
			startActivity(intentDoc);
		}
	}

	private synchronized void performSearch(String query) {
		if (mSearchTask == null) {
			// no task is running, execute a new task
			mSearchTask = new TaskSearch(mSearcher, query);
			mSearchTask.execute();
		} else {
			// put the query in queue so it can be processed later
			Log.v(TAG, String.format("performSearch -> "
					+ "mSearchQueryPending=%s", query));
			mSearchQueryPending = query;
		}
	}

	private synchronized void processSearchResult(Searcher searcher,
			List<Integer> result) {
		mSearchTask = null;

		if (searcher == mSearcher && result != null) {
			mListAdapter.clear();
			for (Integer fineId : result) {
				mListAdapter.add(fineId);
			}
			mListAdapter.notifyDataSetChanged();
		}

		if (mSearchQueryPending != null) {
			String query = mSearchQueryPending;
			mSearchQueryPending = null;

			Log.v(TAG, String.format("processSearchResult -> "
					+ "performSearch(%s)", query));
			performSearch(query);
		}
	}

	private class TaskSearch extends AsyncTask<Void, Void, List<Integer>> {

		private String mQuery;
		private Searcher mSearcher;

		private TaskSearch(Searcher searcher, String query) {
			mSearcher = searcher;
			mQuery = query;
		}

		@Override
		protected List<Integer> doInBackground(Void... arg0) {
			if (mQuery != null && mQuery.isEmpty()) {
				// special case for empty query
				// simply return the marked list if possible
				if (mListAdapter != null) {
					return mListAdapter.getMarkedFineIds();
				} else {
					return new ArrayList<Integer>();
				}
			}

			if (mSearcher != null) {
				return mSearcher.search(mQuery);
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Integer> result) {
			processSearchResult(mSearcher, result);
		}
	}
}
