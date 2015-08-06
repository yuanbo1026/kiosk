package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.nexxoo.kiosk_app.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public class SearchListAdapter extends BaseExpandableListAdapter {

	public List<String> groupList;
	public List<BaseEntity> tempChild;
	public List<List<BaseEntity>> childList = new ArrayList<List<BaseEntity>>();
	public LayoutInflater mInflater;
	public Activity activity;
	private Context context;

	public SearchListAdapter(Context context,List<String> groupList, List<List<BaseEntity>> childList) {
		this.groupList = groupList;
		this.childList = childList;
		this.context = context;
		this.mInflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		final int gPosition = groupPosition;
		final int cPosition = childPosition;
		TextView text = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.serach_list_child_row, null);

		}
		text = (TextView) convertView.findViewById(R.id.search_result_child_listview_item_name);
		text.setText(childList.get(groupPosition).get(childPosition).getName());
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, childList.get(gPosition).get(cPosition).getName(),
						Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_list_group_row, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.search_list_group_row_text);
		tv.setText(groupList.get(groupPosition));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	}
