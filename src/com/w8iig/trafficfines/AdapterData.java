package com.w8iig.trafficfines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.w8iig.trafficfines.data.DataAbstract;
import com.w8iig.trafficfines.data.DataAbstract.FineValues;
import com.w8iig.trafficfines.util.UtilString;

class AdapterData extends ArrayAdapter<Integer> {

	static private final String TAG = "AdapterData";

	private DataAbstract mData;
	private List<Integer> mMarked;
	private OnCheckedChangeListener mMarklistener;

	public AdapterData(Context context, DataAbstract data) {
		super(context, 0);

		mData = data;

		mMarked = new ArrayList<Integer>();
		mMarklistener = new MarkOnCheckedChangeListener();
	}

	public List<Integer> getMarkedFineIds() {
		return Collections.unmodifiableList(mMarked);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		ViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.row_fine, parent, false);
			holder = new ViewHolder();
			holder.cbMark = (CheckBox) row.findViewById(R.id.fine_mark);
			holder.txtName = (TextView) row.findViewById(R.id.fine_name);
			holder.txtDescription = (TextView) row
					.findViewById(R.id.fine_description);
			holder.txtValueHigh = (TextView) row
					.findViewById(R.id.fine_value_high);
			holder.txtValueLow = (TextView) row
					.findViewById(R.id.fine_value_low);
			row.setTag(holder);

			holder.cbMark.setOnCheckedChangeListener(mMarklistener);
		} else {
			row = convertView;
			Object tag = row.getTag();
			if (tag instanceof ViewHolder) {
				holder = (ViewHolder) tag;
			}
		}

		if (row == null || holder == null) {
			Log.e(TAG, String.format("getView: row=%s, holder=%s", row, holder));
			return null;
		}

		Integer fineId = getItem(position);
		if (mData != null) {
			int nameResId = mData.getFineNameResId(fineId);
			int descResId = mData.getFineDescriptionResId(fineId);
			FineValues value = mData.getFineValue(fineId);

			if (nameResId == 0 || descResId == 0 || value == null) {
				Log.e(TAG, String.format("getView: nameResId=%s,"
						+ "descResId=%s,value=%s", nameResId, descResId, value));
				return null;
			}

			holder.fineId = fineId;
			holder.cbMark.setChecked(mMarked.contains(Integer.valueOf(fineId)));
			holder.txtName.setText(nameResId);
			holder.txtDescription.setText(descResId);
			if (value.isRange()) {
				holder.txtValueHigh.setText(formatValue(value.getHigh()));
				holder.txtValueLow.setText(formatValue(value.getLow()));
			} else {
				holder.txtValueHigh.setText(formatValue(value.getHigh()));
				holder.txtValueLow.setText("");
			}
		} else {
			Log.e(TAG, "getView: mData=null");
		}

		return row;
	}

	private String formatValue(int value) {
		double[] thresholds = new double[] { 1000000, 1000, 1 };
		// TODO: string resources for these?
		String[] units = new String[] { "m", "k", "₫" };

		double scaled_value = 0;
		String scaled_unit = null;

		for (int i = 0; i < units.length; i++) {
			if (value >= thresholds[i]) {
				scaled_value = value / thresholds[i];
				scaled_unit = units[i];
				break;
			}
		}

		return String.format("%s%s", UtilString.formatNumber(scaled_value),
				scaled_unit);
	}

	static private class ViewHolder {
		private int fineId;
		private CheckBox cbMark;
		private TextView txtName;
		private TextView txtDescription;
		private TextView txtValueHigh;
		private TextView txtValueLow;
	}

	private class MarkOnCheckedChangeListener implements
			OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean checked) {
			ViewParent parent = view.getParent();
			if (parent instanceof View) {
				Object tag = ((View) parent).getTag();
				if (tag instanceof ViewHolder) {
					Integer fineId = Integer.valueOf(((ViewHolder) tag).fineId);

					if (checked) {
						if (!mMarked.contains(fineId)) {
							mMarked.add(fineId);
						}
					} else {
						mMarked.remove(fineId);
					}
				}
			}
		}
	}
}
