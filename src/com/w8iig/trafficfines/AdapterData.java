package com.w8iig.trafficfines;

import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.w8iig.trafficfines.data.DataAbstract;
import com.w8iig.trafficfines.data.DataAbstract.FineValues;

class AdapterData extends ArrayAdapter<Integer> {

	private DataAbstract mData;

	public AdapterData(Context context, DataAbstract data) {
		super(context, 0);

		mData = data;
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
			holder.txtName = (TextView) row.findViewById(R.id.fine_name);
			holder.txtDescription = (TextView) row
					.findViewById(R.id.fine_description);
			holder.txtValueHigh = (TextView) row
					.findViewById(R.id.fine_value_high);
			holder.txtValueLow = (TextView) row
					.findViewById(R.id.fine_value_low);
			row.setTag(holder);
		} else {
			row = convertView;
			Object tag = row.getTag();
			if (tag instanceof ViewHolder) {
				holder = (ViewHolder) tag;
			}
		}

		if (row == null || holder == null) {
			return null;
		}

		Integer fineId = getItem(position);
		if (mData != null) {
			int nameResId = mData.getFineNameResId(fineId);
			int descResId = mData.getFineDescriptionResId(fineId);
			FineValues value = mData.getFineValue(fineId);

			if (nameResId == 0 || descResId == 0 || value == null) {
				return null;
			}

			holder.txtName.setText(nameResId);
			holder.txtDescription.setText(descResId);
			if (value.isRange()) {
				holder.txtValueHigh.setText(formatValue(value.getHigh()));
				holder.txtValueLow.setText(formatValue(value.getLow()));
			} else {
				holder.txtValueHigh.setText(formatValue(value.getHigh()));
				holder.txtValueLow.setText("");
			}
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
		
		double delta = scaled_value - Math.floor(scaled_value);
		if (delta < 0.1) {
			return String.format(Locale.US, "%.0f%s", scaled_value, scaled_unit);
		} else {
			return String.format(Locale.US, "%.1f%s", scaled_value, scaled_unit);
		}		
	}

	private static class ViewHolder {
		private TextView txtName;
		private TextView txtDescription;
		private TextView txtValueHigh;
		private TextView txtValueLow;
	}
}