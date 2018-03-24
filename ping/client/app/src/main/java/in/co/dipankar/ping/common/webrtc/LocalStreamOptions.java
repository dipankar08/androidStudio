package in.co.dipankar.ping.common.webrtc;

/**
 * Created by dip on 3/23/18.
 */

public class LocalStreamOptions {
    private boolean isAudioEnabled = true;
    private boolean isVideoEnabled = true;

    public int videoFps = 20;
    private Dimension dimension = Dimension.Dimension_240P;


    public int getVideoFps() {
        return videoFps;
    }

    public void setVideoFps(int videoFps) {
        this.videoFps = videoFps;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public enum Dimension {
        Dimension_240P(240, 320),
        Dimension_720P(720, 1280),
        Dimension_1080P(1080, 1920);
        int width;
        int height;
        Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public boolean isAudioEnabled(){
        return isAudioEnabled;
    }

    public boolean isVideoEnabled(){
        return isVideoEnabled;
    }
    public void setAudioEnabled(boolean s){
         isAudioEnabled = s;
    }

    public void setVideoEnabled( boolean s){
         isVideoEnabled=s;
    }
}
