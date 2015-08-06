package de.nexxoo.kiosk_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.tools.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 28.07.2015.
 */
public class ManualListAdapter extends ArrayAdapter<Manual> {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Context mContext;
	private List<Manual> mManualList;
	private int mLayoutId;

	public ManualListAdapter(Context context, int layoutId, List<Manual> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mContext = context;
		mManualList = new ArrayList<Manual>(objects);
		mLayoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Item item = null;
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(mLayoutId, parent, false);
			v.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));
			if (mLayoutId == R.layout.manual_gridview_item) {
				// list view
				item = new Item();
				item.manualName = (TextView) v
						.findViewById(R.id.manual_gridview_item_name);
				item.manualName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.manualSize = (TextView) v.findViewById(R.id.manual_gridview_item_size);
				item.manualSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.manualCover = (ImageView) v.findViewById(R.id.manual_gridview_item_cover);

			} else {
				// grid layout

				item = new Item();
				item.manualName = (TextView) v
						.findViewById(R.id.manual_listview_item_name);
				item.manualName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.manualSize = (TextView) v.findViewById(R.id.manual_listview_item_size);
				item.manualSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.manualCover = (ImageView) v.findViewById(R.id.manual_listview_item_cover);

			}

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		if(mManualList!=null){
			item.manualName.setText("Manual");
//			Manual manual = mManualList.get(position);
//			item.manualName.setText(manual.getmName());
		}

		return v;
	}

	private static class Item {
		TextView manualName;
		TextView manualSize;
		ImageView manualCover;
	}
}
