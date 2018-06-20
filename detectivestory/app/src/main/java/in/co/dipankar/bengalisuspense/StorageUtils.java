package in.co.dipankar.bengalisuspense;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.co.dipankar.quickandorid.utils.DLog;

public class StorageUtils {

    public interface Callback {
        public void onSuccess(String data);
        public void onError(String data);
    }

    private final Context mContext;
    public StorageUtils(Context context){
        mContext = context;
    }
    public void loadJSONFromAsset(String fileName, Callback callback) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            if(callback != null){
                callback.onSuccess(json);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            if(callback != null){
                callback.onError("loadJSONFromAsset failed: "+ex);
            }
        }

    }


    public void writeFileToCache(String sFileName, String sBody , Callback callback){
        File file = new File(mContext.getCacheDir(),"dipankar");
        if(!file.exists()){
            file.mkdir();
        }
        try{
            File gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            DLog.e("writeFileToCache : success");
            if(callback != null){
                callback.onSuccess(sBody);
            }

        }catch (Exception e){
            e.printStackTrace();
            DLog.e("writeFileToCache: fail"+e.getMessage());
            if(callback != null){
                callback.onError("writeFileToCache: Failed"+e.getMessage());
            }
        }
    }

    public void  readFileFromCache(String sFileName  , Callback callback){
        File file = new File(mContext.getCacheDir() + "/dipankar/" + sFileName);
        if (!file.exists()) {
            if(callback != null){
                callback.onError("File not exist");
            }
            return;
        }
        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null; //not declared within while loop
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            if(callback != null){
                callback.onError("readFileFromCache Failed: "+ex.getMessage());
            }
            return;
        }
        if(contents.toString().length() == 0){
            if(callback != null){
                callback.onError("Empty data");
            }
            return;
        }
        if(callback != null){
            callback.onSuccess(contents.toString());
        }
    }
}
