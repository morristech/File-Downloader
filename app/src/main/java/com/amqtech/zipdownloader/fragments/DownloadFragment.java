package com.amqtech.zipdownloader.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amqtech.zipdownloader.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andrew on 8/17/15.
 */
public class DownloadFragment extends Fragment {

    private Button startBtn;
    private ProgressDialog mProgressDialog;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //noinspection ConstantConditions
        startBtn = (Button) getView().findViewById(R.id.startDL);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("Downloading file...");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);

                final DownloadTask downloadTask = new DownloadTask(getActivity());
                //Make sure you match local file name with this one here
                downloadTask.execute("http://www.amqtech.com/planets_walls/david/david_wall_1.jpg");

                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                        Toast.makeText(getActivity().getBaseContext(), "Download Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                //Make sure file name here is the same as the one on the server
                //Also, make sure to keep '/' wherever it is since this is a directory
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                        //Downloads folder
                        + "/Download"
                        //File name
                        + "/david_wall_1.jpg");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                new MaterialDialog.Builder(getActivity())
                        .title("File Downloaded!")
                        .content("Your file has been downloaded. You may need to use a file manager to view it.")
                        .neutralText("OK")
                        .neutralColor(Color.parseColor("#ff1963be"))
                        .positiveText("Download Now")
                        .positiveColor(Color.parseColor("#ff1963be"))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                String cabinet = "market://details?id=com.afollestad.cabinet";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(cabinet));
                                startActivity(i);
                            }

                            @Override
                            public void onNeutral(MaterialDialog dialog) {
                                super.onNeutral(dialog);
                                dialog.dismiss();
                            }
                        })
                        .show();
        }
    }

}
