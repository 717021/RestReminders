package com.dreamfish.restreminders.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

import com.dreamfish.restreminders.R;
import com.dreamfish.restreminders.model.auto.DayRepeatType;
import com.dreamfish.restreminders.utils.AlertDialogTool;
import com.dreamfish.restreminders.utils.PixelTool;
import com.zyyoona7.picker.OptionsPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditWorkTimeDialog extends Dialog {

  public EditWorkTimeDialog(@NonNull Context context) {
    super(context, R.style.WhiteRoundDialog);
    this.context = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dialog_edit_worktime);
    resources = context.getResources();

    initData();
    initView();
    refreshView();

    AlertDialogTool.setDialogWindowAnimations(this, R.style.DialogBottomPopup);
    AlertDialogTool.setDialogGravity(this, Gravity.BOTTOM);
    AlertDialogTool.setDialogSize(this, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
  }

  @Override
  public void show() {
    super.show();
    refreshView();
  }

  private Context context;
  private Resources resources;

  private Calendar startTime = Calendar.getInstance();
  private Calendar endTime = Calendar.getInstance();
  private boolean workTimeEnabled = true;
  private DayRepeatType repeat;
  private boolean use24Hours = false;
  private OnEditWorkTimeDialogFinishListener onEditWorkTimeDialogFinishListener;

  public interface OnEditWorkTimeDialogFinishListener{
    void onEdited(Date start, Date end, DayRepeatType repeat, boolean enabled);
    void onCanceled();
  }

  @BindView(R.id.date_picker_start)
  OptionsPickerView<String> date_picker_start = null;
  @BindView(R.id.date_picker_end)
  OptionsPickerView<String> date_picker_end = null;
  @BindView(R.id.text_repeat_value)
  Button text_repeat_value;
  @BindView(R.id.switch_enable)
  Switch switch_enable;

  public EditWorkTimeDialog setOnEditWorkTimeDialogFinishListener(OnEditWorkTimeDialogFinishListener onEditWorkTimeDialogFinishListener) {
    this.onEditWorkTimeDialogFinishListener = onEditWorkTimeDialogFinishListener;
    return this;
  }
  public EditWorkTimeDialog setStartTime(Date startTime) {
    this.startTime.setTime(startTime);
    return this;
  }
  public EditWorkTimeDialog setEndTime(Date endTime) {
    this.endTime.setTime(endTime);
    return this;
  }
  public EditWorkTimeDialog setUse24Hours(boolean use24Hours) {
    this.use24Hours = use24Hours;
    return this;
  }
  public EditWorkTimeDialog setRepeat(DayRepeatType repeat) {
    this.repeat = repeat;
    return this;
  }
  public EditWorkTimeDialog setWorkTimeEnabled(boolean workTimeEnabled) {
    this.workTimeEnabled = workTimeEnabled;
    return this;
  }
  public boolean isWorkTimeEnabled() {
    return workTimeEnabled;
  }
  public Date getStartTime() {
    return startTime.getTime();
  }
  public Date getEndTime() {
    return endTime.getTime();
  }
  public boolean isUse24Hours() {
    return use24Hours;
  }
  public DayRepeatType getRepeat() {
    return repeat;
  }


  /**
   * 初始化界面控件的显示数据
   */
  private void refreshView() {
    setValueToSelected();
  }

  private List<String> timeListDataAP;
  private List<String> timeListDataHours12;
  private List<String> timeListDataHours24;
  private List<String> timeListDataMinutes;

  private void initData() {
    timeListDataAP = new ArrayList<>();
    timeListDataAP.add(resources.getString(R.string.text_am));
    timeListDataAP.add(resources.getString(R.string.text_pm));
    timeListDataHours12 = new ArrayList<>();
    timeListDataHours24 = new ArrayList<>();
    timeListDataMinutes = new ArrayList<>();

    for(int i = 1; i <= 12; i++)
      timeListDataHours12.add(String.valueOf(i));
    for(int i = 0; i < 24; i++)
      timeListDataHours24.add(String.valueOf(i));
    for(int i = 0; i <= 59; i++)
      timeListDataMinutes.add(String.valueOf(i));

  }

  private void initView() {
    ButterKnife.bind(this);

    date_picker_start.setTextSize(PixelTool.spToPx(context, 30));
    date_picker_end.setTextSize(PixelTool.spToPx(context, 30));

    if(use24Hours) {
      date_picker_start.setData(timeListDataHours24, timeListDataMinutes);
      date_picker_end.setData(timeListDataHours24, timeListDataMinutes);
    }else {
      date_picker_start.setData(timeListDataAP, timeListDataHours12, timeListDataMinutes);
      date_picker_end.setData(timeListDataAP, timeListDataHours12, timeListDataMinutes);
    }
  }

  @OnClick(R.id.btn_ok)
  void onOkClicked(View v) {
    if (onEditWorkTimeDialogFinishListener!= null) {
      getSelectedValue();

      onEditWorkTimeDialogFinishListener.onEdited(startTime.getTime(), endTime.getTime(), repeat, workTimeEnabled);
      dismiss();

    }
  }
  @OnClick(R.id.btn_cancel)
  void onCancelClicked(View v) {
    if (onEditWorkTimeDialogFinishListener!= null) { onEditWorkTimeDialogFinishListener.onCanceled(); dismiss(); }

  }
  @OnClick(R.id.text_repeat_value)
  void onRepeatClicked(View v) {
    new EditRepeatDialog (context)
            .setValue(repeat)
            .setOnEditRepeatDialogFinishListener(new EditRepeatDialog.OnEditRepeatDialogFinishListener() {
              @Override
              public void onEdited(DayRepeatType val) { repeat = val; text_repeat_value.setText(repeat.getRepeatTypeString(resources)); }
              @Override
              public void onCanceled() {}
            })
            .show();
  }

  private void setValueToSelected() {

    if(use24Hours) {

      date_picker_start.setOpt1SelectedPosition(startTime.get(Calendar.HOUR_OF_DAY));
      date_picker_start.setOpt2SelectedPosition(startTime.get(Calendar.MINUTE));

      date_picker_end.setOpt1SelectedPosition(endTime.get(Calendar.HOUR_OF_DAY));
      date_picker_end.setOpt2SelectedPosition(endTime.get(Calendar.MINUTE));

    }else {
      date_picker_start.setOpt1SelectedPosition(startTime.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
      date_picker_start.setOpt2SelectedPosition(startTime.get(Calendar.HOUR) - 1);
      date_picker_start.setOpt3SelectedPosition(startTime.get(Calendar.MINUTE));

      date_picker_end.setOpt1SelectedPosition(endTime.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
      date_picker_end.setOpt2SelectedPosition(endTime.get(Calendar.HOUR) - 1);
      date_picker_end.setOpt3SelectedPosition(endTime.get(Calendar.MINUTE));
    }

    switch_enable.setChecked(workTimeEnabled);
    text_repeat_value.setText(repeat.getRepeatTypeString(resources));
  }
  private void getSelectedValue() {

    if(use24Hours) {
      startTime.set(Calendar.HOUR_OF_DAY, date_picker_start.getOpt1SelectedPosition());
      startTime.set(Calendar.MINUTE, date_picker_start.getOpt2SelectedPosition());

      endTime.set(Calendar.HOUR_OF_DAY, date_picker_end.getOpt1SelectedPosition());
      endTime.set(Calendar.MINUTE, date_picker_end.getOpt2SelectedPosition());
    }else {

      startTime.set(Calendar.HOUR_OF_DAY, date_picker_start.getOpt2SelectedPosition() +
              (date_picker_start.getOpt1SelectedPosition() == 1 ? 12 : 0) + 1);
      startTime.set(Calendar.MINUTE, date_picker_start.getOpt3SelectedPosition());

      endTime.set(Calendar.HOUR_OF_DAY, date_picker_end.getOpt2SelectedPosition() +
              (date_picker_end.getOpt1SelectedPosition() == 1 ? 12 : 0) + 1);
      endTime.set(Calendar.MINUTE, date_picker_end.getOpt3SelectedPosition());
    }

    workTimeEnabled = switch_enable.isChecked();
  }
}
