package ru.commenthere.comment;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Application extends android.app.Application {

	private static Application instance;

	private Context context;
	private String version;
	private boolean isDebuggable;

	private AppContext appContext = null;

	private int foregroundActiviesCount = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		context = getApplicationContext();
		isDebuggable = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

		try {
			ComponentName comp = new ComponentName(context, getClass());
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(comp.getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}

		appContext = new AppContext(context);

		initImageLoader();

	}

	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(1)
				.memoryCache(new WeakMemoryCache())
				// .memoryCacheSize(2 * 1024 * 1024) // 2 Mb
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	public Context getContext() {
		if (context == null) {
			context = getApplicationContext();
		}
		return context;
	}

	public String getVersion() {
		return version;
	}

	public boolean isDebuggable() {
		return isDebuggable;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public static Application getInstance() {
		if (instance == null) {
			Log.d("Application", "Application instance is null!");
		}
		return instance;
	}

	public boolean isForeground() {
		return foregroundActiviesCount > 0;
	}

	public void incForegroundActiviesCount() {
		++foregroundActiviesCount;
	}

	public void decForegroundActiviesCount() {
		--foregroundActiviesCount;
	}

	@Override
	public void onLowMemory() {
		Log.d("Application", "Application: onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		Log.d("Application", "Application: onTerminate");
		super.onTerminate();
	}

}
