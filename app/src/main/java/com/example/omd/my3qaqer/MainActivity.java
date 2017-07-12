package com.example.omd.my3qaqer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTab;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_View();
    }

    private void init_View() {
        mTab = (TabLayout) findViewById(R.id.mTab);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mTab.addTab(mTab.newTab().setText("بحث عن دواء"));
        mTab.addTab(mTab.newTab().setText("تسجيل الدخول"));
        mTab.addTab(mTab.newTab().setText("انشاء حساب جديد"));

        //////////////////////////////////////////////////////
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTab.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTab));
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}
