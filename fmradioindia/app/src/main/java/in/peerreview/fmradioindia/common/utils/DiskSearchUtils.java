package in.peerreview.fmradioindia.common.utils;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dip on 2/16/18.
 */

public class DiskSearchUtils{
    //Interface
    public interface IDiskSearchCallback{
        void onComplete(List<HashMap<String, String>> result);
        void onError(String msg);
    }
    // public APIs
    public void search(final String ext, IDiskSearchCallback diskSearchCallback) {
        final String rootPath = Environment.getExternalStorageDirectory()
                .getPath() + "/";
        search(rootPath, ext,diskSearchCallback);
    }

    public void search(final String rootPath, final String ext, IDiskSearchCallback diskSearchCallback) {
        searchInternal(rootPath,ext,diskSearchCallback);
    }

    //private
    private void searchInternal(final String rootPath, final String ext, final IDiskSearchCallback diskSearchCallback){
        Thread backgroudThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<HashMap<String,String>>  result = getPlayList(rootPath, ext);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(result == null){
                                diskSearchCallback.onError("Error Not able to serach file..");
                            } else{
                                diskSearchCallback.onComplete(result);
                            }
                        }
                    });
                } catch (final IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            diskSearchCallback.onError(e.getMessage());
                        }
                    });
                }
            }
        });
        backgroudThread.start();
    }

    private ArrayList<HashMap<String,String>> getPlayList(String rootPath,String ext) throws IOException{
        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            if(files == null){
                return fileList;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath(),ext) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath(),ext));
                    } else {
                        break;
                    }
                } else if (file.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("path", file.getAbsolutePath());
                    song.put("name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
    }

    //singleton
    private static class DiskSearchUtilsLoader {
        private static final DiskSearchUtils INSTANCE = new DiskSearchUtils();
    }

    private DiskSearchUtils() {
        if (DiskSearchUtils.DiskSearchUtilsLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }
    public static DiskSearchUtils getInstance() {
        return DiskSearchUtils.DiskSearchUtilsLoader.INSTANCE;
    }
}