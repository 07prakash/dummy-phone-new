package com.example.dummyphoneprakash;



import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class MainPagerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Set default page (0 for Home, 1 for App Drawer)
        int defaultPage = getIntent().getIntExtra("DEFAULT_PAGE", 0);
        viewPager.setCurrentItem(defaultPage);

        // Optional: disable swipe if needed
        // viewPager.setUserInputEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) {
            // If on app drawer, go back to home
            viewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }
}