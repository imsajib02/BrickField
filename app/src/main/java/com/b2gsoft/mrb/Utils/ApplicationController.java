package com.b2gsoft.mrb.Utils;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


// TODO: Auto-generated Javadoc

/**
 * The Class ApplicationController.
 */
public class ApplicationController extends MultiDexApplication {
	
	/** The m request queue. */
	private RequestQueue mRequestQueue;
	
	/** The m instance. */
	private static ApplicationController mInstance;
	
	/** The Constant TAG. */
	public static final String TAG = ApplicationController.class.getSimpleName();
	
	/** The Constant MY_SOCKET_TIMEOUT_MS. 120 sec */
	private static final int MY_SOCKET_TIMEOUT_MS = 120000;
	
	/** The m context. */
	private static Context mContext;
	
	/** The socket timeout. 40 sec */
	public  static int socketTimeout = 40000;

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override

	public void onCreate() {
		super.onCreate();

		// The following line triggers the initialization of ACRA
		mInstance = this;
		mContext = getApplicationContext();


	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public static Context getContext() {
		return mContext;
	}

	/**
	 * Gets the single instance of ApplicationController.
	 *
	 * @return single instance of ApplicationController
	 */
	public static synchronized ApplicationController getInstance() {
		return mInstance;
	}

	/**
	 * Gets the request queue.
	 *
	 * @return the request queue
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());

		}

		return mRequestQueue;
	}

	/**
	 * Adds the to request queue.
	 *
	 * @param <T> the generic type
	 * @param req the req
	 * @param tag the tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	/**
	 * Adds the to request queue.
	 *
	 * @param <T> the generic type
	 * @param req the req
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		
		req.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	/**
	 * Cancel pending requests.
	 *
	 * @param tag the tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}
