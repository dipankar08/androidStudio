package in.co.dipankar.ping.common.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by dip on 3/26/18.
 */

public class AudioManagerUtils {
    public interface Callback  {
        void onSpeakerOn();
        void onSpeakerOff();
    }

    private AudioManager mAudioManager;
    private Callback mCallback;
    private Context mContext;
    public AudioManagerUtils(Context context, Callback callback){
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mCallback = callback;
        mContext = context;
    }

    public void setEnableSpeaker(boolean enable){
        if(enable){
            if(mAudioManager.isWiredHeadsetOn()) {
                mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setWiredHeadsetOn(false);
                mAudioManager.setSpeakerphoneOn(true);
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                mCallback.onSpeakerOn();
            }
        } else{
            if(mAudioManager.isSpeakerphoneOn()) {
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                mAudioManager.setSpeakerphoneOn(false);
                mAudioManager.setWiredHeadsetOn(true);
                mCallback.onSpeakerOff();
            }
        }
    }
}
