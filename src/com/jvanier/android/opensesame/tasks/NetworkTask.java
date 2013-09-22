package com.jvanier.android.opensesame.tasks;

import java.io.IOException;

import org.json.JSONException;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;

import com.jvanier.android.opensesame.controllers.OpenSesameApp;
import com.jvanier.android.opensesame.utils.NetworkUtil;

abstract public class NetworkTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	
	public static interface OnCompleteListener<Result> {
		public void onComplete(Result result);
	}
	
	public static interface OnIOExceptionListener {
		public void onIOException(IOException exception);
	}
	
	public static interface OnExceptionListener {
		public void onException(Exception exception);
	}
	
	public static interface OnNetworkUnavailableListener {
		public void onNetworkException(NetworkErrorException exception);
	}
	
	private Exception exception;
	private IOException ioException;
	
	private boolean isComplete = false;
	public boolean isComplete() {
		return isComplete;
	}
	
	private boolean isAborted = false;
	public boolean isAborted() { return isAborted; }
	
	private OnCompleteListener<Result> completeListener;
	public void setOnCompleteListener(OnCompleteListener<Result> l) {
		this.completeListener = l;
	}

	private OnExceptionListener exceptionListener;
	public void setOnExceptionListener(OnExceptionListener l) {
		this.exceptionListener = l;
	}
	
	private OnExceptionListener genericExceptionListener;
	/**
	 * This listener gets called if any error happens. It's a convenience method to 
	 * catch all the errors in 1 shot.
	 * @param l
	 */
	public void setOnGenericExceptionListener(OnExceptionListener l) {
		this.genericExceptionListener = l;
	}
	
	private OnIOExceptionListener ioExceptionListener;
	public void setOnIOExceptionListener(OnIOExceptionListener l) {
		this.ioExceptionListener = l;
	}
	
	private OnNetworkUnavailableListener networkUnavailableListener;
	public void setOnNetworkUnavailableListener(
			OnNetworkUnavailableListener l) {
		this.networkUnavailableListener = l;
	}
	
	public NetworkTask() {
		super();
	}
	
	/**
	 * A convenience method used to hide the poor API of the internal execute method that can't be overridden.
	 */
	@SuppressWarnings("unchecked")
	public void execute() {
		execute(null, null);
	}
	
	/**
	 * Silly AsynTask has the cancel marked as final. Use abort instead;
	 */
	public void abort() {
		isAborted = true;
		cancel(true);
	}
	
	/**
	 * This is where we make the network call. We're not passing object here, so this method must get the params
	 * it needs from the class properties. Since this is thread be sure to make as volatile if needed.
	 * 
	 * @return 
	 * @throws Exception
	 */
	abstract protected Result doNetworkAction() throws IOException, JSONException;
	
	/**
	 * This method runs on the UI Thread.
	 * Use this hook for what happens when the doNetworkAction method returns successfully.
	 * 
	 * @param result The result from doNetworkAction
	 */
	 protected void onPostSuccess(Result result) { }
	 protected void onPostFault(Exception e) { }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isComplete = false;
		isAborted = false;
		boolean hasNetworkConnection = NetworkUtil.hasInternetAccess(OpenSesameApp.getContext());
		if (!hasNetworkConnection) {
			if (networkUnavailableListener != null) {
				networkUnavailableListener.onNetworkException(new NetworkErrorException("Internet connection unavailable"));
			}
			abort();
		}
	}
	
	/**
	 * Mostly likely you should not override this. It's not marked as final, but treat it like that.
	 */
	@Override
	protected Result doInBackground(Params... params) {
		if (isCancelled()) {
			return null;
		}
		try {
			return doNetworkAction();
		} catch (IOException e) {
			ioException = e;
			return null;
		} catch (Exception e) {
			exception = e;
			return null;
		}
	}
	
	/**
	 * Out logic to figure what kind of result we got.
	 */
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		isComplete = true;
		if (isCancelled() || isAborted()) {
			return;
		}
		
		if (ioException != null) {
			onPostFault(ioException);
			if (ioExceptionListener != null) ioExceptionListener.onIOException(ioException);
			if (genericExceptionListener != null) genericExceptionListener.onException(ioException);
		} else if (exception != null) {
			onPostFault(exception);
			if (exceptionListener != null) exceptionListener.onException(exception);
			if (genericExceptionListener != null) genericExceptionListener.onException(exception);
		} 
		
		// SUCCESS!
		else {
			onPostSuccess(result);
			if (completeListener != null) {
				completeListener.onComplete(result);
			}
		}
	}
}

