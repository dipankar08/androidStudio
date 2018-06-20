package in.co.dipankar.bengalisuspense;

import android.content.Context;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.co.dipankar.bengalisuspense.models.ItemImpl;
import in.co.dipankar.quickandorid.views.QuickListView;

public class ItemManager {

    private Context mContext;
    List<ItemImpl> allData;
    private Map<String, List<QuickListView.Item>> mData;
    private List<QuickListView.Item> mMenu;
    private StorageUtils mStorageUtils;

    public ItemManager(Context c,StorageUtils storageUtils ){
        mContext = c;
        mStorageUtils = storageUtils;
        loadDataFromAssert();
    }
    private static final String FILE_NAME ="cache.json";

    private void loadDataFromAssert(){
        mStorageUtils.loadJSONFromAsset(FILE_NAME, new StorageUtils.Callback() {
            @Override
            public void onSuccess(String data) {
                if(data != null){
                    buildAllData(data);
                } else{
                    Toast.makeText(mContext, "Not able to Retrive data from internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String data) {
                Toast.makeText(mContext, "Not able to Retrive data from internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buildAllData(String jsonArray) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            allData = objectMapper.readValue(jsonArray, new TypeReference<List<ItemImpl>>(){});
        } catch (IOException e1) {
            e1.printStackTrace();
            allData = new ArrayList<>();
        }
        processData(allData);
    }

    public void processData(List<ItemImpl> list) {
        mData = new HashMap<>();
        mMenu = new ArrayList<>();
        for(ItemImpl i: list){
            if(mData.get(i.getType()) == null){
                mData.put(i.getType(), new ArrayList<QuickListView.Item>());
                mMenu.add(new ItemImpl(i.getType()));
            }
            mData.get(i.getType()).add(i);
        }
    }

    public final List<QuickListView.Item> getMenu(){
        if(mMenu != null) {
            return new ArrayList<>(mMenu);
        } else{
            return null;
        }
    }

    public  List<QuickListView.Item> getSubMenu(String key) {
        return mData.get(key);
    }
}
