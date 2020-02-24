package com.dreamfish.restreminders;

import android.content.Intent;
import android.os.Bundle;

import com.dreamfish.restreminders.pages.NotesFragment;
import com.dreamfish.restreminders.pages.RestStateFragment;
import com.dreamfish.restreminders.pages.ToListFragment;
import com.dreamfish.restreminders.pages.TrainHelpFragment;
import com.dreamfish.restreminders.utils.MyFragmentAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initControls();
        initView();
    }

    private long mExitTime;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View mRestStatusArea;

    //返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次返回到桌面", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                //System.exit(0);
                backToHome();
            }
            return true;
        } else return super.onKeyDown(keyCode, event);
    }

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_quit:
                quitAsk();
                break;
            case R.id.action_help:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return true;
    }


    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager_main);
        mTabLayout = findViewById(R.id.tab_main);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) mRestStatusArea.setVisibility(View.VISIBLE);
                else mRestStatusArea.setVisibility(View.GONE);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);


        List<String> sTitle = new ArrayList<>();
        sTitle.add(this.getString(R.string.tab_reset_status));
        sTitle.add(this.getString(R.string.tab_todo));
        sTitle.add(this.getString(R.string.tab_note));
        sTitle.add(this.getString(R.string.tab_train_help));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(RestStateFragment.newInstance());
        fragments.add(ToListFragment.newInstance());
        fragments.add(NotesFragment.newInstance());
        fragments.add(TrainHelpFragment.newInstance());

        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments, sTitle);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    //初始化控件
    private void initControls() {

        mRestStatusArea = findViewById(R.id.view_rest_status_area);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void backToHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    //退出疑问
    private void quitAsk() {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage("您确认要退出吗？退出后将不能为您进行提醒了");
        alertdialogbuilder.setPositiveButton("确定", null);
        alertdialogbuilder.setNeutralButton("取消", null);
        alertdialogbuilder.create().show();
    }


}
