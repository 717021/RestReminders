package com.dreamfish.restreminders.ui.reststate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import com.dreamfish.restreminders.workers.IWorkerHost;
import com.dreamfish.restreminders.workers.RestReminderWorker;

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
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

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
        restReminderWorker = (RestReminderWorker)((IWorkerHost)getActivity().getApplication())
                .getWorker(RestReminderWorker.NAME);

        ButterKnife.bind(this, view);

        loadSettings();
        initView(view);
        loadViewData();
    }

    @Override
    public void onDestroyView() {
        saveSettings();
        super.onDestroyView();
    }

    private Context context;
    private Resources resources;
    private RestReminderWorker restReminderWorker;

    private Point screenSize;

    @BindString(R.string.text_from_to) String text_from_to;
    @BindString(R.string.text_minute_range) String text_minute_range;
    @BindString(R.string.text_work_time_state_changed) String text_work_time_state_changed;
    @BindString(R.string.text_enable) String text_enable;
    @BindString(R.string.text_disable) String text_disable;
    @BindDrawable(R.drawable.btn_round) Drawable btn_round;

    @BindColor(R.color.color_status_active) ColorStateList color_status_active;
    @BindColor(R.color.color_status_not_active) ColorStateList color_status_not_active;
    @BindColor(R.color.color_status_not_present) ColorStateList color_status_not_present;
    @BindColor(R.color.color_status_not_enable) ColorStateList color_status_not_enable;

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
    @BindView(R.id.view_work_time_items)
    LinearLayout view_work_time_items;
    @BindView(R.id.btn_edit_worktimes)
    Button btn_edit_worktimes;
    @BindView(R.id.btn_edit_worktimes_finish)
    Button btn_edit_worktimes_finish;
    
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
            setWorkTimeItemEditMode(false);
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
                .setStartVal(restReminderWorker.getWorkOnceTimeMin())
                .setEndVal(restReminderWorker.getWorkOnceTimeMax())
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
                .setStartVal(restReminderWorker.getRestOnceTimeMin())
                .setEndVal(restReminderWorker.getRestOnceTimeMax())
                .setTitle(text_reset_time_set.getLeftString())
                .setOnEditIntRangeDialogFinishListener(new EditIntRangeDialog.OnEditIntRangeDialogFinishListener() {
                    @Override
                    public void onEdited(int start, int end) { setRestOnceTime(start, end); }
                    @Override
                    public void onCanceled() {}
                }).show();
    }
    @OnClick(R.id.btn_edit_worktimes)
    void onEditTimesClicked(View v)  {
        setWorkTimeItemEditMode(true);
    }
    @OnClick(R.id.btn_edit_worktimes_finish)
    void onEditTimesFinishClicked(View v) {
        setWorkTimeItemEditMode(false);
    }

    // ============================
    // 时钟设置控制
    // ============================

    private boolean workTimeItemEditMode = false;

    private void addWorkTimeSetItem(Date timeStart, Date timeEnd, DayRepeatType repeatType, boolean enable) {
        addWorkTimeSetItem(new WorkTime(timeStart, timeEnd, enable, repeatType), true);
    }
    private void addWorkTimeSetItem(WorkTime workTime, boolean byUser) {
        if(byUser) restReminderWorker.addWorkTime(workTime);

        //View
        SmallCheckItem newItem = new SmallCheckItem(context);
        newItem.setData(workTime);
        newItem.setText(String.format(text_from_to, DateUtils.formatTimeMinuteAuto(workTime.getStartTime()),
                DateUtils.formatTimeMinuteAuto(workTime.getEndTime())));
        newItem.setBackground(btn_round.getConstantState().newDrawable());
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
                            restReminderWorker.removeWorkTime(workTime);
                            view_work_time_items.removeView(newItem);
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
        newItem.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
            workTime.setEnabled(isChecked);
            Toasty.normal(context,
                    String.format(text_work_time_state_changed, (isChecked ? text_enable : text_disable))
            ).show();
            flushWorkTimeItemStatus(workTime, newItem);
        } );

        newItem.setOnClickListener((v) -> {
            if(workTimeItemEditMode) {
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
                                workTime.setLastEditDay(new Date());
                                newItem.setText(String.format(text_from_to, DateUtils.formatTimeMinuteAuto(start),
                                        DateUtils.formatTimeMinuteAuto(end)));
                                flushWorkTimeItemStatus(workTime, newItem);
                            }

                            @Override
                            public void onCanceled() {
                            }
                        })
                        .show();
            }
        });
        newItem.setOnLongClickListener(v -> {
            setWorkTimeItemEditMode(!workTimeItemEditMode);
            return true;
        });
        view_work_time_items.addView(newItem);
    }
    private void setWorkTimeItemEditMode(boolean isEditMode) {
        if(workTimeItemEditMode != isEditMode) {
            workTimeItemEditMode = isEditMode;
            if(workTimeItemEditMode) {
                btn_edit_worktimes.setVisibility(View.GONE);
                btn_edit_worktimes_finish.setVisibility(View.VISIBLE);
            }else {
                btn_edit_worktimes.setVisibility(View.VISIBLE);
                btn_edit_worktimes_finish.setVisibility(View.GONE);
            }
            SmallCheckItem item = null;
            for (int i = 0, c = view_work_time_items.getChildCount(); i < c; i++) {
                item = (SmallCheckItem) view_work_time_items.getChildAt(i);
                item.setDeleteMode(workTimeItemEditMode);
            }
        }
        flushWorkTimeListStatus();
    }
    private void flushWorkTimeListStatus() {
        SmallCheckItem item = null;
        WorkTime workTime = null;
        for (int i = 0, c = view_work_time_items.getChildCount(); i < c; i++) {
            item = (SmallCheckItem) view_work_time_items.getChildAt(i);
            workTime = (WorkTime)item.getData();
            flushWorkTimeItemStatus(workTime, item);
        }
    }
    private void flushWorkTimeItemStatus(WorkTime workTime, SmallCheckItem smallCheckItem) {
        if(workTime.checkMet(false, false)) {
            if(workTime.isEnabled())
                smallCheckItem.setBackgroundTintList(workTime.checkMet(true, true) ?
                    color_status_active : color_status_not_present);
            else smallCheckItem.setBackgroundTintList(color_status_not_enable);
            smallCheckItem.setVisibility(View.VISIBLE);
        } else {
            smallCheckItem.setBackgroundTintList(color_status_not_active);
            smallCheckItem.setVisibility(workTimeItemEditMode ? View.VISIBLE : View.GONE);
        }
    }

    // ============================
    // 设置加载与保存
    // ============================

    private final String[] text_work_urgency_choose = new String[] {
            "紧急","重要","认真","一般","空闲","划水"
    };

    private void setWorkUrgency(int level) {
        restReminderWorker.setWorkUrgency(level);
        text_work_urgency.setRightString(text_work_urgency_choose[level]);
    }
    private void setWorkOnceTime(int min, int max) {
        restReminderWorker.setWorkOnceTimeMax(max);
        restReminderWorker.setWorkOnceTimeMin(min);
        text_workhours_set.setRightString(String.format(text_minute_range, min, max));
    }
    private void setRestOnceTime(int min, int max) {
        restReminderWorker.setRestOnceTimeMax(max);
        restReminderWorker.setRestOnceTimeMin(min);
        text_reset_time_set.setRightString(String.format(text_minute_range, min, max));
    }

    //设置条目

    private boolean use24Hours = false;
    private SettingsUtils settingsUtils = null;

    private void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        settingsUtils = new SettingsUtils(context);

        //加载通用设置
        use24Hours = prefs.getBoolean("use24Hours", false);
        restReminderWorker.setWorkUrgency(prefs.getInt("workUrgency", 3));
        restReminderWorker.setWorkOnceTimeMax(prefs.getInt("workOnceTimeMax", 110));
        restReminderWorker.setWorkOnceTimeMin(prefs.getInt("workOnceTimeMin", 45));
        restReminderWorker.setRestOnceTimeMax(prefs.getInt("restOnceTimeMax", 25));
        restReminderWorker.setRestOnceTimeMin(prefs.getInt("restOnceTimeMin", 15));

        //读取数据
        if(restReminderWorker.getWorkTimesCount() == 0) {
            restReminderWorker.loadWorkTimes(settingsUtils.readSettings("workTimes", "[]"));
        }
    }
    private void loadViewData() {
        for(int i = 0, c = restReminderWorker.getWorkTimesCount(); i < c; i++) {
            addWorkTimeSetItem(restReminderWorker.getWorkTimeAt(i), false);
        }
        flushWorkTimeListStatus();
        DateUtils.setsIs24Hour(use24Hours);
        setWorkUrgency(restReminderWorker.getWorkUrgency());
        setWorkOnceTime(restReminderWorker.getWorkOnceTimeMin(), restReminderWorker.getWorkOnceTimeMax());
        setRestOnceTime(restReminderWorker.getRestOnceTimeMin(), restReminderWorker.getRestOnceTimeMax());
    }
    private void saveSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        //保存通用设置
        editor.putInt("workUrgency", restReminderWorker.getWorkUrgency());
        editor.putInt("workOnceTimeMax", restReminderWorker.getWorkOnceTimeMax());
        editor.putInt("workOnceTimeMin", restReminderWorker.getWorkOnceTimeMin());
        editor.putInt("restOnceTimeMax", restReminderWorker.getRestOnceTimeMax());
        editor.putInt("restOnceTimeMin", restReminderWorker.getRestOnceTimeMin());

        editor.apply();

        //保存其他设置
        //保存 workTimes
        settingsUtils.writeSettings("workTimes", restReminderWorker.saveWorkTimes());
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
