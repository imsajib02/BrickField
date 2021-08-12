package com.b2gsoft.mrb.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.b2gsoft.mrb.R;

public class GenericVollyError {

    public static Response.ErrorListener errorListener(final Context context, final ProgressDialog pDialog) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                try {
                    if (error instanceof NetworkError) {
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    } else if (error instanceof VolleyError) {
                        Toast.makeText(context, context.getString(R.string.volley_error), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e("ex", e.getMessage());
                }

            }
        };
    }
}
