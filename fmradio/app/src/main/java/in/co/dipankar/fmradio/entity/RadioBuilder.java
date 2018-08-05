package in.co.dipankar.fmradio.entity;

public class RadioBuilder {
    public RadioBuilder(){

    }
    private String mName;

    public RadioBuilder setName(String name) {
        mName = name;
        return this;
    }
    public String  getName(){
        return mName;
    }
    public Radio build(){
        return new Radio(this);
    }
}
