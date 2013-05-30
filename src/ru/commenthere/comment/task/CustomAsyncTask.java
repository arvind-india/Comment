package ru.commenthere.comment.task;

import ru.commenthere.comment.view.CustomProgressDialog;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public abstract class CustomAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {
	private final static String TAG = "CustomAsyncTask";

	protected Context mContext = null;
	protected Activity mActivity = null;

	private boolean showProgress = true;
	private String progressMessage;
	private Dialog pleaseWaitDialog = null;

	private AsyncTaskListener asyncTaskListener = null;
	private Result result;

	protected int errorCode;
	protected String errorMessage = "";

	public static interface AsyncTaskListener {
		void onBeforeTaskStarted(CustomAsyncTask<?, ?, ?> task);

		void onTaskFinished(CustomAsyncTask<?, ?, ?> task);
	}

	public CustomAsyncTask(Context mContext) {
		this.mContext = mContext;
		if (mContext instanceof Activity) {
			this.mActivity = (Activity) mContext;
		}
	}

	public CustomAsyncTask(Context mContext, boolean showProgress) {
		this(mContext);
		this.showProgress = showProgress;
	}

	public CustomAsyncTask(Context mContext, boolean showProgress,
			String progressMessage) {
		this(mContext);
		this.showProgress = showProgress;
		this.progressMessage = progressMessage;
	}

	public void setAsyncTaskListener(AsyncTaskListener asyncTaskListener) {
		this.asyncTaskListener = asyncTaskListener;
	}

	public void removeAsyncTaskListener(AsyncTaskListener asyncTaskListener) {
		if (this.asyncTaskListener == asyncTaskListener)
			this.asyncTaskListener = null;
	}

	@Override
	protected void onCancelled() {
		if (showProgress) {
			if (mActivity != null)
				mActivity.setProgressBarIndeterminateVisibility(false);
			try {
				pleaseWaitDialog.dismiss();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		if (mActivity != null)
			mActivity.finish();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (showProgress) {
			if (mActivity != null)
				mActivity.setProgressBarIndeterminateVisibility(true);
			if (progressMessage == null) {
				pleaseWaitDialog = CustomProgressDialog.show(mContext, null,
						null, true, true);
			} else {
				ProgressDialog progressDialog = new ProgressDialog(mContext);
				progressDialog.setMessage(progressMessage);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				try {
					progressDialog.show();
				} catch (Throwable t) {
					t.printStackTrace();
					progressDialog = null;
				}
				pleaseWaitDialog = progressDialog;
			}
			if (pleaseWaitDialog != null) {
				pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
					}
				});
			}
		}
		doBeforeTaskStarted();
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		if (showProgress && pleaseWaitDialog != null) {
			if (mActivity != null)
				mActivity.setProgressBarIndeterminateVisibility(false);
			try {
				pleaseWaitDialog.dismiss();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			pleaseWaitDialog = null;
		}
		this.result = result;
		doTaskFinished();
	}

	private void doBeforeTaskStarted() {
		if (asyncTaskListener != null) {
			asyncTaskListener.onBeforeTaskStarted(this);
		}
	}

	private void doTaskFinished() {
		if (asyncTaskListener != null) {
			asyncTaskListener.onTaskFinished(this);
		}
	}

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}

	public Dialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public Result getResult() {
		return result;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getProgressMessage() {
		return progressMessage;
	}

	public void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}

}
