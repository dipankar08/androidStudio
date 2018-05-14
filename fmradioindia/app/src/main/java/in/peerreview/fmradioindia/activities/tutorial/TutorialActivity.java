package in.peerreview.fmradioindia.activities.tutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.co.dipankar.quickandorid.views.SliderView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.common.CommonIntent;
import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {
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
    sliderItems.add(
        new SliderItem(
            "২জি বাঙ্গালী FM আপে আপনাকে স্বাগত !",
            "আপনি এই আপে ভারত, বাংলাদেশ এর বিভিন্ন লাইভ FM  চ্যানেল এর গান শুনতে পারবেন খুব সহজে এবং বিনামূল্যে! নুতুন নুতুন ফীচার জানার জন্য \"নেক্সট\" বাটন টিপুন.",
            R.drawable.ic_fm_white_50,
            "#33691E"));
    sliderItems.add(
        new SliderItem(
            "এখন আপনার ঘুম ভাগুক FM এলার্ম-এ",
            "আপনি এই আপের সাহায্যে সকালের এলার্ম সেট করতে পারেন! কোনো চ্যানেল এ লং প্রেস করুন আর \"অটো স্টার্ট\" প্রেস করুন.",
            R.drawable.ic_alarm_white_50,
            "#1B5E20"));
    sliderItems.add(
        new SliderItem(
            "উন্নত ইন্টেলিজেন্ট সার্চ!",
            "আপনি এখন খুব সহজেই  নিজের পছন্দের চ্যানেল সার্চ করতে পারেন! মেনু বারের সার্চ বাটন ক্লিক করুন এবং টাইপ করুন!",
            R.drawable.ic_bulg_white_50,
            "#827717"));
    sliderItems.add(
        new SliderItem(
            "আমরা জানি কোন চ্যানেল চলছে না!",
            "আপনি কি অনেক বার ট্রাই করে বিরক্ত ? আমরা আপনাকে বলে দেব কোনো চ্যানেল চলছে আর কোনো চ্যানেল চলছে না ",
            R.drawable.ic_social_white_50,
            "#311B92"));
    sliderItems.add(
        new SliderItem(
            "এই আপটি অটোমেটিক বন্ধ করা যাই!",
            "রাত্রে যখন আপনি ঘুমোতে যান, আপনি চান যে FM টি ৩০ মিনিটে পরে অটোমেটিক বন্ধ হয়ে যাক - সেটা এখন খুব সহজেই করতে পারেন. কোনো চ্যানেল এ লং প্রেস করুন আর \"অটো স্টপ\" ট্যাপ করুন. ",
            R.drawable.ic_switch_off,
            "#1A237E"));
    sliderItems.add(
        new SliderItem(
            "আপনি যে কোনো FM রেকর্ড করতে পারেন",
            "এই আপ এ খুব সহজে রেকডিং করা যায়! প্লেয়ার এ রাইট সাইড এ \"Circle \" এ \" ট্যাপ করুন. ",
            R.drawable.ic_recording_white_50,
            "#0D47A1"));
    sliderItems.add(
        new SliderItem(
            "আপএ আছে নুতুন সাজেশন এন্ড রিসেন্ট লিস্ট.",
            "আমরা সার্চ লিস্ট ছড়ায়, নুতুন একটা কুইক সাজেশন লিস্ট অ্যাড করেছি. এই লিস্ট এ আপনার ভালোবাসার চ্যানেল, ফেবারিট চ্যানেল এবং রিসেন্টলি চালানো চ্যানেল গুলো অটোমেটিক দেখা যাবে. ",
            R.drawable.ic_search_white_50,
            "#1565C0"));
    sliderItems.add(
        new SliderItem(
            "অটো লক স্ক্রিন সাপোর্ট!",
            "আমরা জানি, হটাৎ কোনো কারণে ভুল বসত টাচ হয়ে গেলে চ্যানেল চেঞ্জ হয়ে যায় বা বন্ধ হয়ে যায়. এখন FM চালিয়ে আপনি সেটা পকোটে রাখতে পারেন - আপনাকে শুধু লক স্ক্রিন তা অন করতে হবে.",
            R.drawable.ic_lock_white_50,
            "#006064"));
    mSliderView.init(
        sliderItems,
        new SliderView.Callback() {
          @Override
          public void onSkip() {
            gotoHome();
            SharedPrefsUtil.getInstance().setBoolean("FIRST_BOOT", false);
          }

          @Override
          public void onNext() {}

          @Override
          public void onClose() {
            gotoHome();
            SharedPrefsUtil.getInstance().setBoolean("FIRST_BOOT", false);
          }
        });
  }

  private void gotoHome() {
    CommonIntent.startRadioActivity(this, getIntent().getStringExtra("START_WITH"));
  }

  private class SliderItem implements SliderView.Item {

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
