package com.dreamfish.restreminders.ui.reststate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.allen.library.SuperTextView;
import com.dreamfish.restreminders.R;
import com.dreamfish.restreminders.dialogs.CommonDialog;
import com.dreamfish.restreminders.dialogs.CommonDialogs;
import com.dreamfish.restreminders.dialogs.EditIntRangeDialog;
import com.dreamfish.restreminders.dialogs.EditWorkTimeDialog;
import com.dreamfish.restreminders.model.auto.DayRepeatType;
import com.dreamfish.restreminders.model.IDrawerOwner;
import com.dreamfish.restreminders.model.work.WorkTime;
import com.dreamfish.restreminders.utils.AlertDialogTool;
import com.dreamfish.restreminders.utils.DateUtils;
import com.dreamfish.restreminders.utils.SettingsUtils;
import com.dreamfish.restreminders.widget.MainTimerCanvases;
import com.dreamfish.restreminders.widget.MyTitleBar;
import com.dreamfish.restreminders.widget.SmallCheckItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dreamfish.restreminders.dialogs.CommonDialogs.RESULT_SETTING_ACTIVITY;

public class RestStateFragment extends Fragment {

    private final String TAG = "RestStateFragment";

    public static Fragment newInstance(){
        return new RestStateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_page_rest_status, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        handler = new RestStateHandler(this);
        resources = getResources();

        ButterKnife.bind(this, view);

        loadSettings();
        initView(view);
        loadWorkTimes();
        applySettings();
    }

    @Override
    public void onDestroyView() {
        saveSettings();
        super.onDestroyView();
    }

    private Context context;
    private Resources resources;

    private Point screenSize;

    @BindString(R.string.text_from_to)
    String text_from_to;
    @BindView(R.id.layout_root)
    ViewGroup layout_root;
    @BindView(R.id.layout_top)
    RelativeLayout layout_top;
    @BindView(R.id.layout_bottom)
    LinearLayout layout_bottom;
    @BindView(R.id.layout_control)
    ViewGroup layout_control;
    @BindView(R.id.layout_settings)
    ViewGroup layout_settings;
    @BindView(R.id.main_timer)
    MainTimerCanvases main_timer;
    @BindView(R.id.view_work_time_set)
    LinearLayout view_work_time_set;

    @BindView(R.id.btn_pause)
    Button btn_pause;
    @BindView(R.id.btn_start)
    Button btn_start;
    @BindView(R.id.text_work_urgency)
    SuperTextView text_work_urgency;
    @BindView(R.id.text_workhours_set)
    SuperTextView text_workhours_set;
    @BindView(R.id.text_reset_time_set)
    SuperTextView text_reset_time_set;
    @BindView(R.id.titlebar)
    MyTitleBar titleBar;

    private void initView(View view) {
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        defaultDisplay.getSize(screenSize);

        main_timer.setRadius(screenSize.x / 2 - 100);
        main_timer.postInvalidate();

        setBottomViewVisible(false);
        setSettingsViewVisible(false);

        //菜单
        titleBar.setLeftIconOnClickListener((v) -> {
            Activity activity = getActivity();
            if(activity instanceof IDrawerOwner)
                ((IDrawerOwner)activity).openDrawer();
        });
    }
    private void setBottomViewVisible(boolean visible) {
        int height_top = (int)((double)screenSize.y * (visible ? 0.3 : 0.7));
        int height_bottom = (int)((double)screenSize.y * (visible ? 0.7 : 0.3));

        ViewGroup.LayoutParams lp = layout_top.getLayoutParams();
        lp.height = height_top;
        layout_top.setLayoutParams(lp);

        //lp = layout_bottom.getLayoutParams();
        //lp.height = height_bottom;
        //layout_bottom.setLayoutParams(lp);

        main_timer.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
    private void setSettingsViewVisible(boolean visible) {
        layout_settings.setVisibility(visible ? View.VISIBLE : View.GONE);
        layout_control.setVisibility(!visible ? View.VISIBLE : View.GONE);
        if(!visible) {
            setWorkTimeItemDelMode(false);
        }
    }

    // ============================
    // 控件事件
    // ============================

    @OnClick(R.id.btn_start)
    void onBtnStartClicked(View v) {
        task = new TimerTask() {
            @Override
            public void run() {
                Message m = new Message();
                m.what = MSG_MAIN_CLOCK_UPDATE;
                handler.dispatchMessage(m);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }
    @OnClick(R.id.btn_pause)
    void onBtnPauseClicked(View v) {
        if(timer!=null)
            timer.cancel();
    }
    @OnClick(R.id.btn_goto_setting)
    void onGotoSettingsClicked(View v) {
        CommonDialogs.showSettings(getActivity());
    }
    @OnClick(R.id.btn_settings_back)
    void onSettingsBackClicked(View v) {
        setBottomViewVisible(false);
        setSettingsViewVisible(false);
        saveSettings();
    }
    @OnClick(R.id.btn_settings)
    void onSettingsClicked(View v) {
        setBottomViewVisible(true);
        setSettingsViewVisible(true);
    }
    @OnClick(R.id.btn_add_worktime)
    void onAddWorkTimeClicked(View v) {
        //添加工作时间
        new EditWorkTimeDialog(context)
                .setUse24Hours(use24Hours)
                .setStartTime(new Date())
                .setRepeat(new DayRepeatType(DayRepeatType.REPEAT_EVERY_DAY))
                .setEndTime(new Date())
                .setOnEditWorkTimeDialogFinishListener(new EditWorkTimeDialog.OnEditWorkTimeDialogFinishListener() {
                    @Override
                    public void onEdited(Date start, Date end, DayRepeatType repeatType, boolean enabled) {
                        addWorkTimeSetItem(start, end, repeatType, enabled);
                    }
                    @Override
                    public void onCanceled() {}
                }).show();
    }
    @OnClick(R.id.text_work_urgency)
    void onSetWorkUrgencyClicked(View v) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.WhiteRoundDialog)
                        .setTitle(resources.getString(R.string.text_select_work_urgency))
                        .setItems(text_work_urgency_choose, (dialog, which) -> setWorkUrgency(which))
                        .setNegativeButton(R.string.text_cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = AlertDialogTool.buildBottomPopupDialog(builder);
        AlertDialogTool.setDialogButtonBetterStyle(context, dialog);
        AlertDialogTool.setDialogPadding(dialog, 20, 50, 20, 45);
        dialog.show();
    }
    @OnClick(R.id.text_workhours_set)
    void onSetWorkHoursClicked(View v) {
        new EditIntRangeDialog(context)
                .setTextStart(resources.getString(R.string.text_min_time))
                .setTextEnd(resources.getString(R.string.text_max_time))
                .setStartVal(workOnceTimeMin)
                .setEndVal(workOnceTimeMax)
                .setTitle(text_workhours_set.getLeftString())
                .setOnEditIntRangeDialogFinishListener(new EditIntRangeDialog.OnEditIntRangeDialogFinishListener() {
                    @Override
                    public void onEdited(int start, int end) { setWorkOnceTime(start, end);}
                    @Override
                    public void onCanceled() {}
                })
                .show();
    }
    @OnClick(R.id.text_reset_time_set)
    void onSetResetTimeClicked(View v) {
        new EditIntRangeDialog(context)
                .setTextStart(resources.getString(R.string.text_min_time))
                .setTextEnd(resources.getString(R.string.text_max_time))
                .setStartVal(restOnceTimeMin)
                .setEndVal(restOnceTimeMax)
                .setTitle(text_reset_time_set.getLeftString())
                .setOnEditIntRangeDialogFinishListener(new EditIntRangeDialog.OnEditIntRangeDialogFinishListener() {
                    @Override
                    public void onEdited(int start, int end) { setRestOnceTime(start, end); }
                    @Override
                    public void onCanceled() {}
                }).show();
    }

    // ============================
    // 时钟设置控制
    // ============================

    private List<WorkTime> workTimes = new ArrayList<>();
    private boolean workTimeItemDelMode = false;

    private void addWorkTimeSetItem(Date timeStart, Date timeEnd, DayRepeatType repeatType, boolean enable) {
        addWorkTimeSetItem(new WorkTime(timeStart, timeEnd, enable, repeatType));
    }
    private void addWorkTimeSetItem(WorkTime workTime) {
        workTimes.add(workTime);

        //View
        SmallCheckItem newItem = new SmallCheckItem(context);
        newItem.setData(workTime);
        newItem.setText(String.format(text_from_to, DateUtils.formatTimeMinuteAuto(workTime.getStartTime()),
                DateUtils.formatTimeMinuteAuto(workTime.getEndTime())));
        newItem.setChecked(workTime.isEnabled());

        newItem.getDeleteButton().setOnClickListener((v) -> {

            new CommonDialog(context)
                    .setTitle(resources.getString(R.string.text_delete_ask))
                    .setMessage(resources.getString(R.string.text_sure_delete_worktime))
                    .setPositive(resources.getString(R.string.text_cancel))
                    .setNegtive(resources.getString(R.string.text_delete))
                    .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick(Dialog dialog) { dialog.dismiss(); }
                        @Override
                        public void onNegtiveClick(Dialog dialog) {
                            workTimes.remove(workTime);
                            view_work_time_set.removeView(newItem);
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
        newItem.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> workTime.setEnabled(isChecked) );

        newItem.setOnClickListener((v) -> {
            //编辑工作时间
           new EditWorkTimeDialog(context)
                .setUse24Hours(use24Hours)
                .setStartTime(workTime.getStartTime())
                .setEndTime(workTime.getEndTime())
                .setRepeat(workTime.getRepeat())
                .setWorkTimeEnabled(workTime.isEnabled())
                .setOnEditWorkTimeDialogFinishListener(new EditWorkTimeDialog.OnEditWorkTimeDialogFinishListener() {
                    @Override
                    public void onEdited(Date start, Date end, DayRepeatType repeatType, boolean enabled) {
                        workTime.setStartTime(start);
                        workTime.setEndTime(end);
                        workTime.setEnabled(enabled);
                        newItem.setText(String.format(text_from_to, DateUtils.formatTimeMinuteAuto(start),
                                DateUtils.formatTimeMinuteAuto(end)));
                    }
                    @Override
                    public void onCanceled() {}
                })
                .show();
        });
        newItem.setOnLongClickListener(v -> {
            setWorkTimeItemDelMode(!workTimeItemDelMode);
            return true;
        });
        view_work_time_set.addView(newItem, 1);

    }
    private void setWorkTimeItemDelMode(boolean isDelMode) {
        if(workTimeItemDelMode != isDelMode) {
            workTimeItemDelMode = isDelMode;
            SmallCheckItem item = null;
            for (int i = 1, c = view_work_time_set.getChildCount() - 1; i < c; i++) {
                item = (SmallCheckItem) view_work_time_set.getChildAt(i);
                item.setDeleteMode(workTimeItemDelMode);
            }
        }
    }

    //加载保存工作时间段
    private void loadWorkTimes() {
        Log.d(TAG, "workTimes : " + settingsUtils.readSettings("workTimes", "[]"));
        JSONArray jsonArray = JSON.parseArray(settingsUtils.readSettings("workTimes", "[]"));
        for(int i = 0, c = jsonArray.size(); i < c; i++) {
            WorkTime workTime = new WorkTime(jsonArray.getJSONObject(i));
            addWorkTimeSetItem(workTime);
        }
    }
    private void saveWorkTimes() {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0, c = workTimes.size(); i < c; i++)
            jsonArray.add(workTimes.get(i).saveToJson());
        settingsUtils.writeSettings("workTimes", jsonArray.toJSONString());
    }



    // ============================
    // 设置加载与保存
    // ============================

    private final String[] text_work_urgency_choose = new String[] {
            "紧急","重要","认真","一般","空闲","划水"
    };

    private void setWorkUrgency(int level) {
        workUrgency = level;
        text_work_urgency.setRightString(text_work_urgency_choose[level]);
    }
    private void setWorkOnceTime(int min, int max) {
        workOnceTimeMin = min;
        workOnceTimeMax = max;
        text_workhours_set.setRightString(String.format(resources.getString(R.string.text_minute_range), min, max));
    }
    private void setRestOnceTime(int min, int max) {
        restOnceTimeMin = min;
        restOnceTimeMax = max;
        text_reset_time_set.setRightString(String.format(resources.getString(R.string.text_minute_range), min, max));
    }

    //设置条目

    private boolean use24Hours = false;
    private int workUrgency = 3;
    private int workOnceTimeMax = 110;
    private int workOnceTimeMin = 45;
    private int restOnceTimeMax = 25;
    private int restOnceTimeMin = 15;

    private SettingsUtils settingsUtils = null;

    private void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        settingsUtils = new SettingsUtils(context);

        //加载通用设置
        use24Hours = prefs.getBoolean("use24Hours", false);
        workUrgency = prefs.getInt("workUrgency", 3);
        workOnceTimeMax = prefs.getInt("workOnceTimeMax", 110);
        workOnceTimeMin = prefs.getInt("workOnceTimeMin", 45);
        restOnceTimeMax = prefs.getInt("restOnceTimeMax", 25);
        restOnceTimeMin = prefs.getInt("restOnceTimeMin", 15);
    }
    private void applySettings() {
        DateUtils.setsIs24Hour(use24Hours);
        setWorkUrgency(workUrgency);
        setWorkOnceTime(workOnceTimeMin, workOnceTimeMax);
        setRestOnceTime(restOnceTimeMin, restOnceTimeMax);
    }
    private void saveSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        //保存通用设置
        editor.putInt("workUrgency", workUrgency);
        editor.putInt("workOnceTimeMax", workOnceTimeMax);
        editor.putInt("workOnceTimeMin", workOnceTimeMin);
        editor.putInt("restOnceTimeMax", restOnceTimeMax);
        editor.putInt("restOnceTimeMin", restOnceTimeMin);

        editor.apply();

        //保存其他设置
        saveWorkTimes();
    }

    // ============================
    // 事件
    // ============================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SETTING_ACTIVITY) {
            loadSettings();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // ============================
    // Handler 与时钟更新
    // ============================

    private static final int MSG_MAIN_CLOCK_UPDATE = 111;

    private static class RestStateHandler extends Handler {
        private  RestStateFragment restStateFragment;

        RestStateHandler(RestStateFragment fragment) {
            restStateFragment = fragment;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == MSG_MAIN_CLOCK_UPDATE) {
                restStateFragment.main_timer.setValue(restStateFragment.main_timer.getValue() + 1);
                restStateFragment.main_timer.postInvalidate();
            }
        }
    }

    private static Handler handler = null;
    private Timer timer = null;
    private TimerTask task = null;

    /**
     * 上班时间
     * 中午结束时间
     * 下午开始时间
     * 下班时间
     *
     * 一次工作时长
     * 一次休息时长
     *
     * 暂停
     */
}
