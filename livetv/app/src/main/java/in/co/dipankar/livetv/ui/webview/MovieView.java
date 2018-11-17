package in.co.dipankar.livetv.ui.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.base.BaseView;
import in.co.dipankar.quickandorid.utils.DLog;

public class MovieView extends BaseView {

    private WebView mWebView;

    public MovieView(Context context) {
        super(context);
        init(context);
    }

    public MovieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MovieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        String url = getArgs().getString("URL");
        if(url!= null && url.length() > 0){
            openURL(url);
        } else{
            openURL("https://oload.icu/embed/yukLQtDK-AI/fdind.MP4.mp4");
        }
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_movie, this);
        mWebView  = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
    }

    public void openURL(String url){
        mWebView.loadUrl(url);
    }
}
