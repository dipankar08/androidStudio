package in.peerreview.fmradioindia.activities.player.presenter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.peerreview.fmradioindia.activities.player.IPlayerContract;
import in.peerreview.fmradioindia.activities.radio.model.RadioNode;
import in.peerreview.fmradioindia.common.models.MusicNode;
import in.peerreview.fmradioindia.common.utils.Configuration;
import in.peerreview.fmradioindia.common.utils.IPlayer;
import in.peerreview.fmradioindia.common.utils.Network;
import in.peerreview.fmradioindia.common.utils.Player;


/** Created by dip on 2/14/18. */
public class PlayerPresenter implements IPlayerContract.Presenter {
    static final String TAG = "WelcomePresenter";
    private static final String url = Configuration.DB_ENDPOINT + "?limit=300&state=Active";
    private IPlayerContract.View mView;

    private Player mPlayer;

    private List<MusicNode> mMusicNodeList;
    int curIndex = 0;

    private boolean mIsRepeat = false;

    public PlayerPresenter(IPlayerContract.View loginView) {
        mView = loginView;
        mPlayer = new Player(new Player.IPlayerCallback() {
            @Override
            public void onTryPlaying(String id) {

            }

            @Override
            public void onSuccess(String id) {
                MusicNode m = getMusicNodeForID(id);
                mView.showPauseUI(m);
            }

            @Override
            public void onResume(String id) {
                MusicNode m = getMusicNodeForID(id);
                mView.showPauseUI(m);
            }

            @Override
            public void onPause(String id) {
                MusicNode m = getMusicNodeForID(id);
                mView.showPlayUI(m);
            }

            @Override
            public void onMusicInfo(HashMap<String, Object> info) {
               mView.updateSeekBarInfo((int)info.get("Duration"),(int)info.get("CurrentPosition"));
            }

            @Override
            public void onSeekBarPossionUpdate(int total, int cur) {
                mView.updateSeekBarInfo(total,cur);
            }

            @Override
            public void onError(String id) {

            }

            @Override
            public void onComplete(String id) {
                if(mIsRepeat){
                    restart();
                } else{
                    playNext();
                }
            }
        });
    }

    @Override
    public void loadAlbum(String name) {
        String url = Configuration.MUSIC_ENDPOINT+ "?limit=30&album_name="+name;
        Network.getInstance().retrive(url, Network.CacheControl.GET_CACHE_ELSE_LIVE, new Network.INetworkCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try{
                    List<MusicNode> list = new ArrayList<>();
                    JSONArray output = jsonObject.getJSONArray("out");
                    for( int i=0; i< output.length();i++){
                        JSONObject js = output.getJSONObject(i);
                        list.add(new MusicNode(js.getString("uid"),
                                js.getString("title"),
                                js.getString("subtitle"),
                                js.getString("media_url"),
                                js.getString("image_url")));
                    }
                    if(list.size() > 0) {
                        mMusicNodeList = list;
                        curIndex = 0;
                    }
                    mView.hideLoadUI();
                } catch (Exception e){

                }
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    @Override
    public void playNext() {
        if(curIndex < mMusicNodeList.size() -1){
            curIndex ++;
            play();
        }
    }

    @Override
    public void playPrevious() {
            if(curIndex > 0){
                curIndex --;
                play();
            }
    }

    @Override
    public void play() {
        if(curIndex >= 0) {
            mPlayer.play(mMusicNodeList.get(curIndex).getId(),mMusicNodeList.get(curIndex).getMedia_url());
        }
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void restart() {
        mPlayer.seekTo(0);
    }

    @Override
    public void resume() {
        mPlayer.resume();
    }

    @Override
    public void mute() {
        mPlayer.mute();
    }

    @Override
    public void unMute() {
        mPlayer.unmute();
    }

    @Override
    public void download() {

    }

    @Override
    public void SetFavorite(boolean isFev) {

    }

    @Override
    public void setSeekLocation(int time) {
        mPlayer.seekTo(time);
    }

    @Override
    public void suffle() {
        long seed = System.nanoTime();
        Collections.shuffle(mMusicNodeList, new Random(seed));
        Collections.shuffle(mMusicNodeList, new Random(seed));
    }

    @Override
    public void setRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    private MusicNode getMusicNodeForID(String id){
        for ( MusicNode mn : mMusicNodeList){
            if(id.equals(mn.getId())){
                return mn;
            }
        }
        return null;
    }

}
