package de.nexxoo.kiosk_app.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.nexxoo.kiosk_app.R;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.tools.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 10.08.2015.
 */
public class OptimizeGridAdapter extends ArrayAdapter<Manual> {
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Context mContext;
	private List<Manual> mManualList;

	public OptimizeGridAdapter(Context context, int layoutId, List<Manual> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mContext = context;
		mManualList = new ArrayList<Manual>(objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = null;
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.manual_gridview_item, parent, false);

			item = new Item();
			item.information = (TextView) v
					.findViewById(R.id.manual_gridview_item_size);
			item.information.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_BOLD));
//			item.manualSize = (TextView) v.findViewById(R.id.manual_gridview_item_size);
//			item.manualSize.setTypeface(Misc.getCustomFont(mContext,
//					Misc.FONT_NORMAL));
			item.manualCover = (ImageView) v.findViewById(R.id.manual_gridview_item_cover);

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}
		return v;
	}

	@Override
	public int getCount() {
		return mManualList.size();
	}

	@Override
	public Manual getItem(int position) {
		return mManualList.get(position);
	}

	private static class Item {
		TextView information;
		ImageView manualCover;
	}

	public static Item NULL_ITEM = new Item();

	public List<Manual> getItems() {
		return mManualList;
	}

	public void setItems(List<Manual> items) {
		mManualList = items;
		notifyDataSetChanged();
	}

	public Item getNullItem() {
		return NULL_ITEM;
	}

	public boolean isNullItem(Item item) {
		return item == NULL_ITEM;
	}


}
