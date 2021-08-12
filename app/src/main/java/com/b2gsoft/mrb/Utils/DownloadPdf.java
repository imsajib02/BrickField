package com.b2gsoft.mrb.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import com.b2gsoft.mrb.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class DownloadPdf extends AsyncTask<String, Void, Void> {

    private Context context;
    private ProgressDialog pDialog;
    private int type;

    public DownloadPdf(Context context, int type) {
        this.context = context;
        this.type = type;
        pDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        try {
            pDialog.setMessage(context.getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Exception e) {
        }
    }

    @Override
    protected Void doInBackground(String... strings) {

        try {

            URL generateUrl = null;

            if(type == StaticValue.SummeryReport) {

                generateUrl = new URL(Url.SummeryReportGenerate);
            }
            else if(type == StaticValue.Report) {

                generateUrl = new URL(Url.ReportGenerate);
            }

            Log.e("ReportURL ", generateUrl.toString());

            HttpURLConnection connection = (HttpURLConnection) generateUrl.openConnection();

            Log.e("Code ", connection.getResponseMessage());

            if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK){

                SimpleDateFormat pdfFormat = new SimpleDateFormat("dd-MM-yyyy");

                Date todayDate = new Date();
                String pdfDate =  pdfFormat.format(todayDate);

                String fileName = "";
                String fileExtension=".pdf";

                URL url = null;

                if(type == StaticValue.SummeryReport) {

                    fileName = context.getString(R.string.summery_report) + " - (" + pdfDate + ")";
                    url = new URL(Url.SummeryReportDownload);
                }
                else if(type == StaticValue.Report) {

                    fileName = context.getString(R.string.report) + " - (" + pdfDate + ")";
                    url = new URL(Url.ReportDownload);
                }

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String folderName = context.getString(R.string.app_name) + " - " + context.getString(R.string.company_name);
                File folder = new File(Environment.getExternalStorageDirectory(), folderName);

                if(!folder.exists())
                {
                    folder.mkdirs();
                }

                File outputFile = new File(folder, fileName + fileExtension);
                outputFile.createNewFile();

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;

                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }

                fos.close();
                is.close();

                PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                PrintAdapter printAdapter = new PrintAdapter(context, outputFile.getAbsolutePath());

                PrintAttributes.Builder builder = new PrintAttributes.Builder();
                builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
                builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);

                printManager.print(outputFile.getName(), printAdapter, builder.build());
            }
            else {

                ((Activity) context).runOnUiThread(new Runnable() {

                    public void run() {

                        Toast.makeText(((Activity) context), context.getString(R.string.general_error_message), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch(Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if(pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
