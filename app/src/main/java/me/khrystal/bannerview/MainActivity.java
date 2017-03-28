package me.khrystal.bannerview;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import me.khrystal.widget.BannerView;
import me.khrystal.widget.BannerViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout bannerContainer;
    BannerView<String> bannerView;
    List<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerContainer = (LinearLayout) findViewById(R.id.llBannerWidget);
        bannerView = (BannerView<String>) findViewById(R.id.bannerWidgetView);
        bannerView.setPageIndicatorPadding(10);
        for (int i = 0; i < 5; i++) {
            datas.add("" + i + "asasasasa");
        }
        ViewGroup container = (ViewGroup) findViewById(R.id.indicatorContainer);
        bannerView.setIndicatorViewGroup(container);
        bannerView.setPageIndicator(new int[]{R.drawable.bg_circle_white, R.drawable.bg_circle_white_60});
        bannerView.setPages(new BannerView.BannerHolder<String>() {
            private TextView textView;

            @Override
            public View createView(Context context) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.item, null);
                textView = (TextView) layout.findViewById(R.id.itemTitle);
                return layout;
            }

            @Override
            public void UpdateUI(Context context, int position, String data) {
                textView.setText(data);
            }

        }, datas);
        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, datas.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        bannerContainer.setOnClickListener(this);

        bannerView.setCanLoop(true);
        bannerView.setClipParent(false);
        bannerView.setPagerPadding(100, 0, 100, 0).setPagerMargin(20);
        BannerViewPager viewPager = bannerView.getViewPager();
//        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerView != null)
            bannerView.startTurning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bannerView != null)
            bannerView.stopTurning();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBannerWidget:
                Toast.makeText(MainActivity.this, "bannerView Click", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
