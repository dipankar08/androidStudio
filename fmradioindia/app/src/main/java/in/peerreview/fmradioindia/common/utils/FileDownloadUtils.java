package in.peerreview.fmradioindia.common.utils;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class FileDownloadUtils{
    //Interface
    public interface IFIleDownloadCallback{
        void onComplete(String result);
        void onProgress(float percentage);
        void onError(String msg);
    }
    
    //public API

    public void download(final String url,final IFIleDownloadCallback diskSearchCallback) {
        Thread backgroudThread = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadInternal(url, diskSearchCallback);
            }
        });
        backgroudThread.start();
    }

    //private
    private void downloadInternal(String surl, final IFIleDownloadCallback diskSearchCallback){
        int count = 0;
        try {
            URL url = new URL(surl);
            String songname = url.getFile();
            songname = songname.substring(songname.lastIndexOf("/")+1);

            File cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"Offline");
            if(!cacheDir.exists())
                cacheDir.mkdirs();
            final File f =new File(cacheDir,songname+".mp3");

            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            if (!f.exists()) {
                f.createNewFile();
            }
            else{
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "File already exist skipping downlaods");
                        diskSearchCallback.onComplete(f.getAbsolutePath());
                    }
                });
                return;
            }

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(f);
            final String filepath = f.getAbsolutePath();

            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();



            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                final float rate = (total*100)/lenghtOfFile;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        diskSearchCallback.onProgress(rate);
                    }
                });
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    diskSearchCallback.onComplete(filepath);
                }
            });

        } catch (final Exception e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    diskSearchCallback.onError(e.getMessage());
                }
            });
        }
    }

    //singleton
    private static class FileDownloadUtilsLoader {
        private static final FileDownloadUtils INSTANCE = new FileDownloadUtils();
    }
    private FileDownloadUtils() {
        if (FileDownloadUtilsLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }
    public static FileDownloadUtils getInstance() {
        return FileDownloadUtilsLoader.INSTANCE;
    }
    private final  String TAG ="DIPANKAR";
}