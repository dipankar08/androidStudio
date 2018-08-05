package in.co.dipankar.fmradio.data;


import android.arch.lifecycle.LiveData;

public interface DataSource<T> {

    LiveData<T> getDataStream();
    LiveData<String> getErrorStream();
}
