package in.co.dipankar.fmradio.data.configuration;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationManager {


    public enum Config {
        ENABLE_TV("enable_tv"),
        RELOAD_DATA("reload_data");
        private final String mValue;
        Config(String value) {
            mValue = value;
        }
    }

    public interface ConfigChangeListener{
        void onChange(Config config, Object newvalue);
    }


    private List<ConfigChangeListener> mConfigChangeListeners;

    private Map<Config, Object> mStoredConfig;
    public ConfigurationManager(){
        mConfigChangeListeners = new ArrayList<>();
        mStoredConfig = new HashMap<>();
    }

    public void addConfigChangeListener(ConfigChangeListener configChangeListener){
        mConfigChangeListeners.add(configChangeListener);
    }

    public void removeConfigChangeListener(ConfigChangeListener configChangeListener){
        mConfigChangeListeners.remove(configChangeListener);
    }

    public void updateConfig(Config config, Object object){
        if(mStoredConfig.get(config) == object){
            return;
        }
        mStoredConfig.put(config, object);
        for (ConfigChangeListener listener: mConfigChangeListeners){
            listener.onChange(config, object);
        }
    }
    public @NonNull  Object getConfig(Config c) {
        if(mStoredConfig.get(c) != null){
            return mStoredConfig.get(c);
        } else{
            return new Object();
        }
    }
}
