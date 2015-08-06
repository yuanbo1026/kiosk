package de.nexxoo.kiosk_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.tools.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class CatalogListAdapter extends ArrayAdapter<Catalog> {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Context mContext;
	private List<Catalog> mCatalogList;
	private int mLayoutId;

	public CatalogListAdapter(Context context, int layoutId, List<Catalog> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mContext = context;
		mCatalogList = new ArrayList<Catalog>(objects);
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
			if (mLayoutId == R.layout.catalog_gridview_item) {
				// list view
				item = new Item();
				item.catalogName = (TextView) v
						.findViewById(R.id.catalog_gridview_item_name);
				item.catalogName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.catalogSize = (TextView) v.findViewById(R.id.catalog_gridview_item_size);
				item.catalogSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.catalogCover = (ImageView) v.findViewById(R.id.catalog_gridview_item_cover);

			} else {
				// grid layout

				item = new Item();
				item.catalogName = (TextView) v
						.findViewById(R.id.catalog_listview_item_name);
				item.catalogName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.catalogSize = (TextView) v.findViewById(R.id.catalog_listview_item_size);
				item.catalogSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.catalogCover = (ImageView) v.findViewById(R.id.catalog_listview_item_cover);

			}

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		if (mCatalogList != null) {
			item.catalogName.setText("Catalog");
//			Catalog catalog = mCatalogList.get(position);
//			item.catalogName.setText(catalog.getmName());
		}

		return v;
	}

	private static class Item {
		TextView catalogName;
		TextView catalogSize;
		ImageView catalogCover;
	}
}