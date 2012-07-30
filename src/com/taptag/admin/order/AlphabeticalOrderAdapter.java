package com.taptag.admin.order;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taptag.admin.R;
import com.taptag.beta.list.AbstractCustomAdapter;

public class AlphabeticalOrderAdapter extends AbstractCustomAdapter<Order> {

	public AlphabeticalOrderAdapter(Context context, int viewId, int textViewResourceId, Order[] objects) {
		super(context, viewId, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(viewId, parent, false);
		}
		
		TextView line1 = (TextView) row.findViewById(R.id.ordersListItemTitle);
		TextView line2 = (TextView) row.findViewById(R.id.ordersListItemSubtitle);
		
		Order order = data[position];
		line1.setText("Vendor: " + Integer.toString(order.getVendorId()));
		line2.setText("Completed: " + Boolean.toString(order.getCompleted()));
		
		return row;
	}

	@Override
	public void sortData() {
		Arrays.sort(data);	
	}

}
