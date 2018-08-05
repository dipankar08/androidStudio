package in.co.dipankar.fmradio.entity;

public class Radio {
    private String mName;
    public Radio(RadioBuilder radioBuilder) {
        mName = radioBuilder.getName();
    }
    public String getName(){
        return mName;
    }
}
