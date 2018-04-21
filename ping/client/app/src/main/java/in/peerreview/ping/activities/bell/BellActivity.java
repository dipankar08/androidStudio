package in.peerreview.ping.activities.bell;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.peerreview.ping.R;
import in.peerreview.ping.activities.Utils.CommonIntent;
import in.peerreview.ping.activities.search.SearchPresenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class BellActivity extends AppCompatActivity implements IBell.View {
    IBell.Presenter  mPresenter;

    View mIncoming;
    CustomFontTextView mInComingTitle, mInComingSubTitle;
    Button mIncomingAccept, mIncomingRejcet;

    View mCreate;

    View mSent;
    CustomFontTextView mSentTitle, mSentSubTitle;
    Button mSentResult;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.peerreview.ping.R.layout.activity_bell);
        initView();
        processIntent();
        mPresenter = new BellPresenter(this);
    }

    private void processIntent() {
        String type =getIntent().getStringExtra("type");
        String bell_info = getIntent().getStringExtra("bell_info");
        BellInfo bellInfo = (new Gson()).fromJson(bell_info,BellInfo.class);
        if(type.equals("outgoing")){
            showSent(bellInfo.getmTitle(),bellInfo.getmAuthor()+" sent the bell successfully",true);
        }  else {
            showIncoming(bellInfo.getmTitle(),bellInfo.getmAuthor()+" sent a bell for you :)");
        }
    }

    private void initView() {

        //inComming
        mIncoming = findViewById(R.id.bell_incoming);
        mInComingTitle = findViewById(R.id.ic_title);
        mInComingSubTitle = findViewById(R.id.ic_subtitle);
        mIncomingAccept = findViewById(R.id.ic_accept);
        mIncomingRejcet = findViewById(R.id.ic_reject);
        mIncomingAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.accept();
                CommonIntent.stopTone();
                endThis();
            }


        });
        mIncomingRejcet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.reject();
                CommonIntent.stopTone();
                endThis();
            }

        });

        mSent = findViewById(R.id.bell_sent);
        mSentTitle = findViewById(R.id.s_title);
        mSentSubTitle = findViewById(R.id.s_subtitle);
        mSentResult = findViewById(R.id.s_result);
        mSentResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.resend();
            }
        });
        mCreate  = findViewById(R.id.bell_create);
    }

    private void endThis() {
        this.finish();
    }

    @Override
    public void showIncoming(String title, String subtitle) {
        showIncomingInternal(title, subtitle);
    }

    @Override
    public void showSent(String title, String subtitle, boolean result) {
        String resultStr = result?"Bell sent.":" Bell Sent failed! Press to resent!";
        if(result){
            mSentResult.setEnabled(false);
        } else{
            mSentResult.setEnabled(true);
        }
        showSentInternal(title, subtitle, resultStr);
    }

    private void showIncomingInternal(String title, String subtitle){
        mInComingTitle.setText(title);
        mInComingSubTitle.setText(subtitle);
        mIncoming.setVisibility(View.VISIBLE);
        mSent.setVisibility(View.GONE);
        mCreate.setVisibility(View.GONE);
        CommonIntent.playTone(this);
    }

    private void showSentInternal(String title, String subtitle, String result){
        mSentTitle.setText(title);
        mSentSubTitle.setText(subtitle);
        mSentResult.setText(result);
        mIncoming.setVisibility(View.GONE);
        mSent.setVisibility(View.VISIBLE);
        mCreate.setVisibility(View.GONE);
    }

}