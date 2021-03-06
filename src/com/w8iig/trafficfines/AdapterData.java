package com.w8iig.trafficfines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
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
	public long getItemId(int position) {
		return getItem(position);
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
			holder.cbMark = (CheckBox) row.findViewById(R.id.cb_fine_mark);
			holder.txtName = (TextView) row.findViewById(R.id.txt_fine_name);
			holder.txtValue = (TextView) row.findViewById(R.id.txt_fine_value);
			holder.txtLicenseDays = (TextView) row
					.findViewById(R.id.txt_fine_license_days);
			holder.txtVehicleDays = (TextView) row
					.findViewById(R.id.txt_fine_vehicle_days);
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

		Integer fineId = Integer.valueOf((int) getItemId(position));
		if (mData != null) {
			int nameResId = mData.getFineNameResId(fineId);
			FineValues value = mData.getFineValue(fineId);
			int licenseDays = mData.getLicenseDays(fineId);
			int vehicleDays = mData.getVehicleDay(fineId);

			if (nameResId == 0 || value == null) {
				Log.e(TAG, String.format("getView: nameResId=%s," + "value=%s",
						nameResId, value));
				return null;
			}

			holder.fineId = fineId;
			holder.cbMark.setChecked(mMarked.contains(Integer.valueOf(fineId)));
			holder.txtName.setText(nameResId);
			holder.txtValue.setText(formatValue(value.getHigh()));

			if (licenseDays >= 9999) {
				holder.txtLicenseDays.setVisibility(View.VISIBLE);
				holder.txtLicenseDays.setText(R.string.fine_9999_days);
			} else if (licenseDays > 0) {
				holder.txtLicenseDays.setVisibility(View.VISIBLE);
				holder.txtLicenseDays.setText(UtilString
						.formatNumber(licenseDays));
			} else {
				holder.txtLicenseDays.setVisibility(View.GONE);
			}

			if (vehicleDays >= 9999) {
				holder.txtVehicleDays.setVisibility(View.VISIBLE);
				holder.txtVehicleDays.setText(R.string.fine_9999_days);
			} else if (vehicleDays > 0) {
				holder.txtVehicleDays.setVisibility(View.VISIBLE);
				holder.txtVehicleDays.setText(UtilString
						.formatNumber(vehicleDays));
			} else {
				holder.txtVehicleDays.setVisibility(View.GONE);
			}

			// expand the hit target of the mark checkbox
			// it's the little thing...
			expandHitTarget(row, holder.cbMark);
		} else {
			Log.e(TAG, "getView: mData=null");
		}

		return row;
	}

	static private void expandHitTarget(final View row, final View checkBox) {
		row.post(new Runnable() {
			@Override
			public void run() {
				Rect rect = new Rect();
				checkBox.getHitRect(rect);

				// the rect is set with the assumption that the checkbox is on
				// the top left position and there is nothing below it:
				// +---+---------------
				// | X | Text..
				// +---+ More text..
				// | |
				// . .
				// . .
				// +---+---------------
				rect.top = 0;
				// try to make a square hit target but do not exceed a quarter
				// of the row width
				rect.right = Math.min(row.getHeight(), row.getWidth() / 4);
				rect.bottom = row.getHeight();
				rect.left = 0;
				row.setTouchDelegate(new TouchDelegate(rect, checkBox));
			}
		});
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
		private TextView txtValue;
		private TextView txtLicenseDays;
		private TextView txtVehicleDays;
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
