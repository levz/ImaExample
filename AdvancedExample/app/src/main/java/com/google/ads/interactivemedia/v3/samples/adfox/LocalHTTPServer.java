package com.google.ads.interactivemedia.v3.samples.adfox;

import android.content.Context;
import android.util.Log;

import com.google.ads.interactivemedia.v3.samples.videoplayerapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalHTTPServer {
    private static LocalHTTPServer mInstance;

    private ImaWebServer mLocalHttpServer;
    private Context mContext;

    private static String vastFolder;

    private LocalHTTPServer() {}

    public static LocalHTTPServer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalHTTPServer();
            //vastFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vast";
            vastFolder = context.getCacheDir().getAbsolutePath() + "/vast";
        }
        mInstance.mContext = context;
        return mInstance;
    }

    public void start() {
        if (mLocalHttpServer != null) {
            mLocalHttpServer.stop();
            mLocalHttpServer = null;
        }

        // Creating cache dir (if not exists)
        File vastDir;
        vastDir = new File(vastFolder);
        if (!vastDir.exists())
            vastDir.mkdir();
        copyResources(R.raw.res_master);
        copyResources(R.raw.res_645361_1945937);
        copyResources(R.raw.res_606766_1939315_1);
        copyResources(R.raw.res_606766_1939315_2);
        copyResources(R.raw.res_606766_1939315_3);
        copyResources(R.raw.res_606766_1939315_4);

        try {
            Log.d("ImaExample", "Starting LocalHTTPServer at " + vastDir.getAbsolutePath());
            mLocalHttpServer = new ImaWebServer("127.0.0.1", 8787, vastDir, false, "*" /*"http://imasdk.googleapis.com"*/);
            mLocalHttpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ImaExample", "Could not start LocalHTTPServer: " + e.getMessage());
        }
    }

    public void stop() {
        if (mLocalHttpServer != null) {
            mLocalHttpServer.stop();
            mLocalHttpServer = null;
        }

        //clearFolder(new File(vastFolder));
    }

    private void clearFolder(File dir){
        if (dir.exists()) {
            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
    }

    private void copyResources(int resId){
        InputStream in = mContext.getResources().openRawResource(resId);
        String filename = mContext.getResources().getResourceEntryName(resId).replace("_", "-").split("res-")[1] + ".xml";

        File dest = new File(vastFolder, filename);
        if(!dest.exists()){
            try {
                Log.d("ImaExample", "Copying " + filename + " to " + dest.getAbsolutePath());
                OutputStream out = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int len;
                while((len = in.read(buffer, 0, buffer.length)) != -1){
                    out.write(buffer, 0, len);
                    Log.d("ImaExample", len + " bytes " + "written");
                }

                out.close();
                in.close();
            }
            catch (FileNotFoundException e) {
                Log.d("ImaExample", "Setup::copyResources - " + e.getMessage());
            }
            catch (IOException e) {
                Log.d("ImaExample", "Setup::copyResources - " + e.getMessage());
            }
        }
        else {
            Log.d("ImaExample", dest.getAbsolutePath() + " already exists");
        }
    }

}