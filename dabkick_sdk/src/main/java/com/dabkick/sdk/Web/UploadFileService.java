package com.dabkick.sdk.Web;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.dabkick.sdk.DabKickVideoAgent.DabKickGlobalData;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class UploadFileService {

    ProgressBar mProgressBar;
    String mFilename;
    UploadTask task;
    boolean isRunning = true;

    public UploadFileService uploadFile(String filePath, String serverURL, String key) {
        task = new UploadTask();
        task.execute(filePath, serverURL, key);


        return this;
    }

    public UploadFileService setProgressBar(ProgressBar bar) {
        mProgressBar = bar;
        return this;
    }

    public UploadFileService setFileName(String fileName) {
        mFilename = fileName;
        return this;
    }

    public void cancel() {
        task.onCancelled();
    }

    private class UploadTask extends AsyncTask<String, Void, Integer> {

        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
        }

        @Override
        protected Integer doInBackground(String... objects) {

            Boolean success = true;

            int totalFileSize = (int) DabKickGlobalData.getFileSize(objects[0]);

            if (mProgressBar != null) {
                mProgressBar.setMax(totalFileSize);
                mProgressBar.setProgress(0);
            }

            return upload2Server(objects[0], objects[1], objects[2], this);

        }

        @Override
        protected void onPostExecute(Integer response) {
            // do stuff after posting data
            if (mFinishedDownloadListener != null) {
                mFinishedDownloadListener.onFinishedDownload(isRunning, response);
                mFinishedDownloadListener = null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        protected void onCancelled() {
            isRunning = false;

            if (mFinishedDownloadListener != null) {
                mFinishedDownloadListener.onFinishedDownload(false, -1);
                mFinishedDownloadListener = null;
            }
        }
    }

    int upload2Server(String filePath, String serverURL, String key, UploadTask task) {

        if (mFilename == null)
            mFilename = DabKickGlobalData.getFileNameFromPath(filePath);

        int maxBufferSize = 1 * 1024 * 1024;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));

            URL url = new URL(serverURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");

            if (key == null || key.isEmpty())
                connection.setRequestProperty("uploaded_file", mFilename);
            else
                connection.setRequestProperty(key, mFilename);

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            if (key == null || key.isEmpty())
                outputStream.writeBytes("Content-Disposition:form-data;name=\"uploaded_file\";filename=\"" + mFilename + "\"" + lineEnd);
            else
                outputStream.writeBytes("Content-Disposition:form-data;name=\"" + key + "\";filename=\"" + mFilename + "\"" + lineEnd);


            outputStream.writeBytes(lineEnd);

            int fileTotalBytes = fileInputStream.available();

            int bufferSize = Math.min(fileTotalBytes, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // Read file
            Log.d("yuan_upload result:", "reading file");
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                Log.d("yuan_upload result:", "writing file bytesRead:" + bytesRead);
                if (!isRunning) {
                    Log.d("yuan_upload result:", "cancelled");
                    return -1;
                }

                outputStream.write(buffer, 0, bufferSize);

                final int bytesAvailable = fileInputStream.available();
                if (mProgressBar != null) {
                    DabKickGlobalData.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(mProgressBar.getMax() - bytesAvailable);
                        }
                    });
                }
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            //int serverResponseCode = connection.getResponseCode();
            //String serverResponseMessage = connection.getResponseMessage();
            //System.out.println(serverResponseMessage);

            String response = "";
            try {
                Log.d("yuan_upload result:", "reading result");
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;

                while ((line = rd.readLine()) != null) {
                    response += line;
                }
                Log.d("yuan_upload result:", response);
//                LogReporter.add("Photo_upload: response=>"+response);

                rd.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

//            LogReporter.add("Upload file: url =>"+serverURL+" response:"+response);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            return 1;

        } catch (Exception e) {
            //Exception handling
            e.printStackTrace();
            return 0;
        }
    }

    OnFinishedDownloadListener mFinishedDownloadListener = null;

    //Listener
    public void setOnFinishedDownload(OnFinishedDownloadListener listener) {
        mFinishedDownloadListener = listener;
    }

    public interface OnFinishedDownloadListener {
        void onFinishedDownload(boolean success, int response);
    }

}