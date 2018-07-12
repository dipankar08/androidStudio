package in.peerreview.fmradioindia.common;

public class StatManager {
    private static StatManager instance;
    private StatManager(){}
    public static StatManager getInstance(){
        if(instance == null){
            instance = new StatManager();
        }
        return instance;
    }
}
