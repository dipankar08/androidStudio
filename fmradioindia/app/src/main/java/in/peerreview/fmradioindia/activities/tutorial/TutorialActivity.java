package in.peerreview.fmradioindia.activities.tutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.co.dipankar.quickandorid.views.SliderView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.common.CommonIntent;

public class TutorialActivity  extends AppCompatActivity {
    private SliderView mSliderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);
        initViews();
    }

    private void initViews() {
        mSliderView = (SliderView) findViewById(R.id.slider);
        List<SliderView.Item> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#33691E"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#1B5E20"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#827717"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#311B92"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#1A237E"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#0D47A1"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#1565C0"
        ));
        sliderItems.add(new SliderItem(
                "২জি বাঙ্গালী আপে আপনাকে স্বাগত!",
                "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
                R.drawable.close,
                "#006064"
        ));
        mSliderView.init(sliderItems, new SliderView.Callback() {
            @Override
            public void onSkip() {
                gotoHome();
                SharedPrefsUtil.getInstance().setBoolean("FIRST_BOOT", false);
            }

            @Override
            public void onNext() {

            }

            @Override
            public void onClose() {
                gotoHome();
                SharedPrefsUtil.getInstance().setBoolean("FIRST_BOOT", false);
            }
        });
    }

    private void gotoHome(){
        CommonIntent.startRadioActivity(this, getIntent().getStringExtra("START_WITH"));
    }

    private class SliderItem implements SliderView.Item{

        String title;
        String subtitle;
        int imageID;
        String color;

        public SliderItem(String title, String subtitle, int imageID, String color) {
            this.title = title;
            this.subtitle = subtitle;
            this.imageID = imageID;
            this.color = color;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSubTitle() {
            return subtitle;
        }

        @Override
        public int getImageId() {
            return imageID;
        }

        @Override
        public String getBackgroundColor() {
            return color;
        }
    }

}
