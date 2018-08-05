package in.co.dipankar.fmradio.data.repositary;

import android.arch.lifecycle.LiveData;
import java.util.List;

import in.co.dipankar.fmradio.data.model.CoinModel;

/**
 * Created by omrierez on 28.12.17.
 */

public interface CryptoRepository {

    LiveData<List<CoinModel>> getCryptoCoinsData();
    LiveData<String> getErrorStream();
    LiveData<Double> getTotalMarketCapStream();
    void fetchData();
}