package com.example.musicbrowser;

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	
	private ArrayList<String> filesName;
	private Activity mActivity;
	private int screenHeight;
	
	public ListViewAdapter(Activity mActivity, ArrayList<String> filesName, int screenHeight) {
		super();
		this.mActivity = mActivity;
		this.filesName = filesName;
		this.screenHeight = screenHeight;
	}
	
	@Override
	public int getCount() {
		return filesName.size();
	}

	@Override
	public String getItem(int position) {
		return filesName.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    public View getView(final int pos, View convertView, ViewGroup parent) {
		ViewHolder view;
		LayoutInflater inflator = mActivity.getLayoutInflater();
        
		if(convertView == null) {
			view = new ViewHolder();
			convertView = inflator.inflate(R.layout.listview_row, parent, false);
			view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
			view.txtViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.035f);
			view.txtViewTitle.setTextColor(Color.WHITE);
			convertView.setTag(view);
		}
		else {
			view = (ViewHolder) convertView.getTag();
		}
		
		view.txtViewTitle.setText(filesName.get(pos));
		

        return convertView;
    }
	
	public static class ViewHolder {
		public TextView txtViewTitle;
	}
}
