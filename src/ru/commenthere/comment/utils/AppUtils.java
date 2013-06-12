package ru.commenthere.comment.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public class AppUtils {

	public static boolean isOnline(Context ctx) {
		NetworkInfo netInfo = ((ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static String getCurrentTimeMiliss() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	public static boolean canMakeCall(Context ctx) {
		TelephonyManager manager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getSimState() == TelephonyManager.SIM_STATE_READY
				&& (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM || manager
						.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA);
	}

	public static void showAlert(Context ctx, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("Alert");
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();
	}

	public static void showAlert(Context cnt, int title, int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cnt);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public static void showAlert(Context ctx, int resId) {
		String message = ctx.getString(resId);
		showAlert(ctx, message);
	}

	public static void showToast(Context ctx, String message) {
		Toast tost = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		tost.setGravity(Gravity.CENTER, 0, 0);
		tost.show();
	}

	public static void showToast(Context ctx, int resId) {
		String message = ctx.getString(resId);
		showToast(ctx, message);
	}

	// public static float distanceBetween(GeoPoint first, GeoPoint second){
	// Location firstLocation = new Location("");
	// firstLocation.setLatitude(first.getLatitudeE6()/1E6);
	// firstLocation.setLongitude(first.getLongitudeE6()/1E6);
	//
	// Location secondLocation = new Location("");
	// secondLocation.setLatitude(second.getLatitudeE6()/1E6);
	// secondLocation.setLongitude(second.getLatitudeE6()/1E6);
	//
	// //distance between them
	// float distanceBetweenPoints = firstLocation.distanceTo(secondLocation);
	// return distanceBetweenPoints;
	// }

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width,
				v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.measure(MeasureSpec.makeMeasureSpec(v.getLayoutParams().width,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
				v.getLayoutParams().height, MeasureSpec.EXACTLY));
		v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
		v.draw(c);
		return b;
	}

	public static String doubleToString(double value) {
		NumberFormat formatter = new DecimalFormat("0.##");
		return formatter.format(value);
	}

	public static float roundTwoDecimals(float f) {
		return (float) Math.round(f * 100) / 100;
	}

	public static void clearCookies(Context context) {
		// Edge case: an illegal state exception is thrown if an instance of
		// CookieSyncManager has not be created. CookieSyncManager is normally
		// created by a WebKit view, but this might happen if you start the
		// app, restore saved state, and click logout before running a UI
		// dialog in a WebView -- in which case the app crashes
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	public static String getFirstChar(String value) {
		if (value != null) {
			for (int i = 0; i < value.length(); i++) {
				char ch = value.charAt(i);
				if (Character.isLetter(ch)) {
					return String.valueOf(ch).toUpperCase();
				}
			}
		}
		return String.valueOf(" ");
	}

	public static int getPixels(Context context, int dipValue) {
		Resources r = context.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue, r.getDisplayMetrics());
		return px;
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}

	/**
	 * Given either a Spannable String or a regular String and a token, apply
	 * the given CharacterStyle to the span between the tokens, and also remove
	 * tokens.
	 * <p>
	 * For example, {@code setSpanBetweenTokens("Hello ##world##!", "##",
	 * new ForegroundColorSpan(0xFFFF0000));} will return a CharSequence
	 * {@code "Hello world!"} with {@code world} in red.
	 * 
	 * @param text
	 *            The text, with the tokens, to adjust.
	 * @param token
	 *            The token string; there should be at least two instances of
	 *            token in text.
	 * @param cs
	 *            The style to apply to the CharSequence. WARNING: You cannot
	 *            send the same two instances of this parameter, otherwise the
	 *            second call will remove the original span.
	 * @return A Spannable CharSequence with the new style applied.
	 * 
	 * @see http 
	 *      ://developer.android.com/reference/android/text/style/CharacterStyle
	 *      .html
	 */

	public static CharSequence setSpanBetweenTokens(CharSequence text,
			String token, CharacterStyle... cs) {
		// Start and end refer to the points where the span will apply
		int tokenLen = token.length();
		int start = text.toString().indexOf(token) + tokenLen;
		int end = text.toString().indexOf(token, start);

		if (start > -1 && end > -1) {
			// Copy the spannable string to a mutable spannable string
			SpannableStringBuilder ssb = new SpannableStringBuilder(text);
			for (CharacterStyle c : cs)
				ssb.setSpan(c, start, end, 0);

			// Delete the tokens before and after the span
			ssb.delete(end, end + tokenLen);
			ssb.delete(start - tokenLen, start);

			text = ssb;
		}

		return text;
	}

	public static String encodeFileToBase64(Context context, String fileName) {
		File file = new File(fileName);
		String encoded = null;
		FileInputStream fis;
		BufferedInputStream bis;
		byte[] buffer = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			// byte[] buffer = new byte[(int) file.length()];
			bis.read(buffer);
			fis.close();
			// encoded = Base64.encodeToString(buffer, 0);
			encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fis = null;
			bis = null;
			buffer = null;
		}
		return encoded;
	}

	public static String encodeFileToBase64(Context context, File file) {
		String encoded = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buffer = new byte[(int) file.length()];
			bis.read(buffer);
			fis.close();
			encoded = Base64.encodeToString(buffer, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encoded;
	}

	public static boolean loadFile(final String url, final File f)
			throws IOException {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				URL imageUrl;
				try {
					imageUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) imageUrl
							.openConnection();
					conn.setConnectTimeout(30000);
					conn.setReadTimeout(30000);
					conn.setInstanceFollowRedirects(true);
					InputStream is = conn.getInputStream();
					OutputStream os = new FileOutputStream(f);
					copyStream(is, os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void saveEncodedFile(String encoded, File file)
			throws Exception {
		byte[] decodedAsBytes = Base64.decode(encoded, Base64.DEFAULT);

		FileOutputStream os = new FileOutputStream(file, true);
		os.write(decodedAsBytes);
		os.flush();
		os.close();
	}

	public static void closeAllBelowActivities(Activity current) {
		boolean flag = true;
		Activity below = current.getParent();
		if (below == null)
			flag = false;
		// System.out.println("Below Parent: " + below.getClass());
		while (flag) {
			Activity temp = below;
			try {
				below = temp.getParent();
				temp.finish();
			} catch (Exception e) {
				flag = false;
			}
		}
		current.finish();
	}

	public static String join(Iterator iterator, String separator) {
		// handle null, zero and one elements before building a buffer
		Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first.toString();
		}
		// two or more elements
		StringBuffer buf = new StringBuffer(256); // Java default is 16,
													// probably too small
		if (first != null) {
			buf.append(first);
		}
		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	public static String readFile(String filePath) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";

	}
	
	public static String convertStreamToString(InputStream is) {
		   java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
	}

}
