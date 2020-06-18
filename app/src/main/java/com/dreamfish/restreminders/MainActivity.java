package com.dreamfish.restreminders;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamfish.restreminders.dialogs.CommonDialog;
import com.dreamfish.restreminders.dialogs.CommonDialogs;
import com.dreamfish.restreminders.model.IDrawerOwner;
import com.dreamfish.restreminders.ui.mine.MineFragment;
import com.dreamfish.restreminders.ui.notes.NotesFragment;
import com.dreamfish.restreminders.ui.reststate.RestStateFragment;
import com.dreamfish.restreminders.ui.todo.ToListFragment;
import com.dreamfish.restreminders.utils.IconAndTextFragmentAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.dreamfish.restreminders.dialogs.CommonDialogs.RESULT_SETTING_ACTIVITY;

public class MainActivity extends AppCompatActivity implements IDrawerOwner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        loadSettings();
        initControls();
        initView();
    }

    private long mExitTime;
    private List<Fragment> fragments;

    // ============================
    // 事件
    // ============================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SETTING_ACTIVITY) {
            for(Fragment f : fragments)
                f.onActivityResult(RESULT_SETTING_ACTIVITY, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        saveSettings();
        super.onDestroy();
    }
    //返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toasty.normal(this, text_press_again_to_quit).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
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
            case R.id.action_settings: CommonDialogs.showSettings(this); break;
            case R.id.action_quit: quitAsk(); break;
            case R.id.action_help: CommonDialogs.showHelp(this); break;
            case R.id.action_about: CommonDialogs.showAbout(this); break;
        }
        return true;
    }

    // ============================
    // 资源与界面初始化
    // ============================

    @BindString(R.string.text_press_again_to_quit)
    String text_press_again_to_quit;
    @BindString(R.string.text_sure_quit)
    String text_sure_quit;
    @BindString(R.string.action_quit)
    String action_quit;
    @BindString(R.string.text_cancel)
    String text_cancel;
    @BindString(R.string.app_name)
    String app_name;
    @BindColor(R.color.colorPrimary)
    int colorPrimary;
    @BindColor(android.R.color.black)
    int colorText;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.view_pager_main)
    ViewPager mViewPager;
    @BindView(R.id.tab_main)
    TabLayout mTabLayout;
    @BindView(R.id.nav)
    NavigationView nav;

    private boolean isScrolled = false;

    private void initView() {
        mTabLayout.setupWithViewPager(mViewPager);

        //标签数据

        List<String> sTitle = new ArrayList<>();
        sTitle.add(this.getString(R.string.tab_reset_status));
        sTitle.add(this.getString(R.string.tab_todo));
        sTitle.add(this.getString(R.string.tab_note));
        sTitle.add(this.getString(R.string.tab_center));

        List<Drawable> sIcons = new ArrayList<>();
        sIcons.add(getDrawable(R.drawable.ic_code));
        sIcons.add(getDrawable(R.drawable.ic_todo));
        sIcons.add(getDrawable(R.drawable.ic_book));
        sIcons.add(getDrawable(R.drawable.ic_balloon));

        fragments = new ArrayList<>();
        fragments.add(RestStateFragment.newInstance());
        fragments.add(ToListFragment.newInstance());
        fragments.add(NotesFragment.newInstance());
        fragments.add(MineFragment.newInstance());

        IconAndTextFragmentAdapter adapter =
                new IconAndTextFragmentAdapter(getSupportFragmentManager(),
                        fragments, sTitle, sIcons);

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
               switch (state) {
                   case ViewPager.SCROLL_STATE_DRAGGING:
                       isScrolled = false;
                       break;
                   case ViewPager.SCROLL_STATE_SETTLING:
                       isScrolled = true;
                       break;
                   case ViewPager.SCROLL_STATE_IDLE:
                       if (mViewPager.getCurrentItem() == 0 && !isScrolled)
                           openDrawer();
                       isScrolled = true;
                       break;
               }
           }
       });

        //tab设置图标和字体
        for (int i = 0; i < mTabLayout.getTabCount(); i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab!=null) tab.setCustomView(getTabView(adapter, i));
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { setTabViewColor(tab, true); }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { setTabViewColor(tab, false); }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab!=null) setTabViewColor(tab, true);
    }
    private void initControls()  {

        DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // 得到contentView 实现侧滑界面出现后主界面向右平移避免侧滑界面遮住主界面
                View content = drawerLayout.getChildAt(0);
                int offset = (int) (drawerView.getWidth() * slideOffset);
                content.setTranslationX(offset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //打开侧滑界面触发
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //关闭侧滑界面触发
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //状态改变时触发
            }
        };

        nav.setNavigationItemSelectedListener(MainActivity.this::onOptionsItemSelected);
        drawerLayout.addDrawerListener(drawerListener);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    private void setTabViewColor(TabLayout.Tab tab, boolean active) {
        View view = tab.getCustomView();
        if(view != null) {
            TextView text_title = tab.getCustomView().findViewById(R.id.text_title);
            ImageView image_icon = view.findViewById(R.id.image_icon);
            if (text_title != null) text_title.setTextColor(active ? colorPrimary : colorText);
            if(image_icon != null) image_icon.getDrawable().setTint(active ? colorPrimary : colorText);
        }
    }
    private View getTabView(IconAndTextFragmentAdapter adapter, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab_main, null);
        TextView text_title = view.findViewById(R.id.text_title);
        ImageView image_icon = view.findViewById(R.id.image_icon);
        image_icon.setImageDrawable(adapter.getPageIcon(position));
        text_title.setText(adapter.getPageTitle(position));
        return view;
    }

    private void backToHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    private void quit() {
        finish();
    }
    //退出疑问
    private void quitAsk() {
        new CommonDialog(this)
                .setTitle(app_name)
                .setMessage(text_sure_quit)
                .setNegtive(action_quit)
                .setPositive(text_cancel)
                .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick(Dialog dialog) { dialog.dismiss(); }
                    @Override
                    public void onNegtiveClick(Dialog dialog) { quit(); }
                })
                .show();
    }


    // ============================
    // 设置加载与保存
    // ============================

    private void loadSettings() {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    private void saveSettings() {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences.Editor editor = prefs.edit();

        //editor.apply();
    }

    @Override
    public void openDrawer() { drawerLayout.openDrawer(Gravity.LEFT); }
}
