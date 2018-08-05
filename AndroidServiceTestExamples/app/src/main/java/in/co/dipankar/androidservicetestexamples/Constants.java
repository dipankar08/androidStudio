package in.co.dipankar.androidservicetestexamples;

public class Constants {
    public interface ACTION
    {
        public static String MAIN_ACTION = "com.dam.foregroundservice.action.main";
        public static String PREV_ACTION = "com.dam.foregroundservice.action.prev";
        public static String PLAY_ACTION = "com.dam.foregroundservice.action.play";
        public static String NEXT_ACTION = "com.dam.foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "com.dam.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.dam.foregroundservice.action.stopforeground";
        String STOP_ACTION = "com.dam.foregroundservice.action.stop";;
        String PAUSE_ACTION = "com.dam.foregroundservice.action.pause";;
    }

    public interface NOTIFICATION_ID
    {
        public static int FOREGROUND_SERVICE = 101;
    }
}
