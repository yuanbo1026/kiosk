package de.nexxoo.kiosk_app.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import com.astuetz.TypefaceSpan;
import de.nexxoo.kiosk_app.db.DatabaseHandler;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.*;

public class Nexxoo {

	public static final String TAG = "NexxooKioskApp";

	public static final String REPLACE_1 = "###x###";
	public static final String REPLACE_2 = "###y###";
	public static String PAGES = " Pages / ";
	public static String DURATION_DIVIDER = " / ";
	public static String TIME_DIVIDER = ":";

	public static String getDeviceId() {
		String id = Build.SERIAL;

		if (id == null)
			id = Build.FINGERPRINT;

		return id;
	}

	/**
	 * Calculates the dp from a given pixel-value depending on device configuration.
	 *
	 * @param ctx Context
	 * @param px  Pixels to calculate dp from
	 * @return The dp value.
	 */
	public static int pxToDp(Context ctx, int px) {
		DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	/**
	 * Calculates the pixel from a given dp-value depending on device configuration.
	 *
	 * @param ctx Context
	 * @param dp  DP to calculate pixels from
	 * @return The pixel value.
	 */
	public static int dpToPx(Context ctx, int dp) {
		DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public static String getLocalIps() {
		String result = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						result += inetAddress.getHostAddress() + "\n";
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static boolean addContentToDB(Context cxt, int id) {
		DatabaseHandler helper = new DatabaseHandler(cxt);
		helper.addContent(id);
		helper.close();
		return false;
	}

	public static int dp2px(Context mContext, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				mContext.getResources().getDisplayMetrics());
	}

	public static SpannableString getStyledText(Context context, String title) {
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(context, "OpenSans-Regular.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return s;
	}

	public static void saveContentId(Context context, int id) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("kiosk",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		long time = System.currentTimeMillis();
		editor.putLong("" + id, time);
		Log.d(Nexxoo.TAG, "save content id into SharedPreference :" + id);
		editor.commit();
	}

	public static Integer[] getContentIds(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("kiosk",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		Map<String, Long> keVaules = (Map<String, Long>) sharedPreferences.getAll();
		List<Map.Entry<String, Long>> contentIds =
				new ArrayList<Map.Entry<String, Long>>(keVaules.entrySet());

		Collections.sort(contentIds, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				int tmp = (o1.getValue()).compareTo(o2.getValue());
				return tmp;
			}
		});
		Collections.reverse(contentIds);

		Integer[] ids = new Integer[contentIds.size()];
		int i = 0;
		for (Map.Entry<String, Long> map : contentIds) {
			ids[i] = Integer.parseInt(map.getKey());
			Log.d(Nexxoo.TAG, "getContentIds :" + ids[i]);
			i++;
		}
		return ids;
	}

	public static List<Integer> getContentIdList(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("kiosk",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		Map<String, Long> keVaules = (Map<String, Long>) sharedPreferences.getAll();
		List<Map.Entry<String, Long>> contentIds =
				new ArrayList<Map.Entry<String, Long>>(keVaules.entrySet());
		Collections.sort(contentIds, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				int tmp = (o1.getValue()).compareTo(o2.getValue());
				return tmp;
			}
		});
		Collections.reverse(contentIds);

		List<Integer> ids = new ArrayList<Integer>();
		for (Map.Entry<String, Long> map : contentIds) {
			ids.add(Integer.parseInt(map.getKey()));
			Log.d(Nexxoo.TAG, "getContentIdList :" + Integer.parseInt(map.getKey()));
		}

		return ids;
	}

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String splitToComponentTimes(long longVal) {
		if ((longVal / 3600) > 0)
			return String.format("%02d:%02d:%02d", longVal / 3600, longVal / 60, longVal % 60);
		else
			return String.format("%02d:%02d", longVal / 60, longVal % 60);
	}
}
