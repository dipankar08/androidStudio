package in.co.dipankar.coolui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import in.co.dipankar.coolui.R;
import in.co.dipankar.coolui.views.RemoteButtonView;
import in.co.dipankar.coolui.views.ZoomButtomView;

public class MainActivity extends AppCompatActivity {
    private RemoteButtonView remoteButtonView;
    private ZoomButtomView zoomButtomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_shape_view);

        remoteButtonView = findViewById(R.id.touchshapeview);
        zoomButtomView = findViewById(R.id.zoom_btn);

        remoteButtonView.setTouchShapeViewListener(new RemoteButtonView.TouchShapeViewListener() {
            @Override
            public void onClick(RemoteButtonView.Direction direction) {
                Log.d("DIPANKAR","onclick:" + direction.name());
            }

            @Override
            public void onHoverIn(RemoteButtonView.Direction direction) {
                Log.d("DIPANKAR","onHoverIn:" + direction.name());
            }

            @Override
            public void onHoverOut(RemoteButtonView.Direction direction) {
                Log.d("DIPANKAR","onHoverOut:" + direction.name());
            }
        });

        zoomButtomView.setZoomButtomViewListener(new ZoomButtomView.ZoomButtomViewListener() {
            @Override
            public void onClick(ZoomButtomView.Direction direction) {
                Log.d("DIPANKAR","Zoom: onclick:" + direction.name());
            }

            @Override
            public void onHoverIn(ZoomButtomView.Direction direction) {
                Log.d("DIPANKAR","Zoom onHoverIn:" + direction.name());
            }

            @Override
            public void onHoverOut(ZoomButtomView.Direction direction) {
                Log.d("DIPANKAR"," Zoom onHoverOut:" + direction.name());
            }
        });
    }
}
