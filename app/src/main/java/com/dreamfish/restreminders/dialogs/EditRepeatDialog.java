package com.dreamfish.restreminders.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.allen.library.SuperTextView;
import com.dreamfish.restreminders.R;
import com.dreamfish.restreminders.model.auto.DayRepeatType;
import com.dreamfish.restreminders.utils.AlertDialogTool;

import androidx.annotation.NonNull;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditRepeatDialog extends Dialog {

  public EditRepeatDialog(@NonNull Context context) {
    super(context, R.style.WhiteRoundDialog);
    this.context = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dialog_edit_repeat);

    ButterKnife.bind(this);

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
  private DayRepeatType value;
  private OnEditRepeatDialogFinishListener onEditRepeatDialogFinishListener;

  public interface OnEditRepeatDialogFinishListener{
    void onEdited(DayRepeatType val);
    void onCanceled();
  }

  //bind Control and res

  @BindColor(R.color.colorPrimary)
  int colorPrimary;
  @BindString(R.string.text_set_custom_condition)
  String text_set_custom_condition;

  @BindView(R.id.text_repeat_once)
  SuperTextView text_repeat_once;
  @BindView(R.id.text_repeat_everyday)
  SuperTextView text_repeat_everyday;
  @BindView(R.id.text_repeat_workday)
  SuperTextView text_repeat_workday;
  @BindView(R.id.text_repeat_holiday)
  SuperTextView text_repeat_holiday;
  @BindView(R.id.text_repeat_mon_to_fir)
  SuperTextView text_repeat_mon_to_fir;
  @BindView(R.id.text_repeat_mon_to_sat)
  SuperTextView text_repeat_mon_to_sat;
  @BindView(R.id.text_repeat_sun_to_fir)
  SuperTextView text_repeat_sun_to_fir;
  @BindView(R.id.text_repeat_sat_to_sun)
  SuperTextView text_repeat_sat_to_sun;
  @BindView(R.id.text_repeat_customize)
  SuperTextView text_repeat_customize;

  public EditRepeatDialog setOnEditRepeatDialogFinishListener(OnEditRepeatDialogFinishListener onEditRepeatDialogFinishListener) {
    this.onEditRepeatDialogFinishListener = onEditRepeatDialogFinishListener;
    return this;
  }
  public DayRepeatType getValue() {
    return value;
  }
  public EditRepeatDialog setValue(DayRepeatType value) {
    this.value = value;
    return this;
  }

  /**
   * 初始化界面控件的显示数据
   */
  private void refreshView() {
    switch (value.getRepeatType()) {
      case DayRepeatType.REPEAT_CUSTOM: text_repeat_customize.setLeftTextColor(colorPrimary); break;
      case DayRepeatType.REPEAT_EVERY_DAY: setItemChecked(text_repeat_everyday); break;
      case DayRepeatType.REPEAT_HOLIDAY: setItemChecked(text_repeat_holiday);break;
      case DayRepeatType.REPEAT_MONDAY_TO_FRIDAY: setItemChecked(text_repeat_mon_to_fir); break;
      case DayRepeatType.REPEAT_MONDAY_TO_SATURDAY: setItemChecked(text_repeat_mon_to_sat); break;
      case DayRepeatType.REPEAT_SATURDAY_TO_SUNDAY: setItemChecked(text_repeat_sat_to_sun); break;
      case DayRepeatType.REPEAT_SUNDAY_TO_FRIDAY: setItemChecked(text_repeat_sun_to_fir); break;
      case DayRepeatType.REPEAT_WORKDAY: setItemChecked(text_repeat_workday); break;
      case DayRepeatType.REPEAT_ONCE: setItemChecked(text_repeat_once); break;
    }
  }
  private void setItemChecked(SuperTextView text) {
    text.setLeftTextColor(colorPrimary);
    text.getCheckBox().setChecked(true);
  }
  private void onEdited(int repeatValue) {
    value.setRepeatType(repeatValue);

    if (onEditRepeatDialogFinishListener!= null) onEditRepeatDialogFinishListener.onEdited(value);
    dismiss();
  }

  //Events

  @OnClick(R.id.text_repeat_once)
  void onOnceClicked(View v) { onEdited(DayRepeatType.REPEAT_ONCE); }
  @OnClick(R.id.text_repeat_everyday)
  void onEverydayClicked(View v) { onEdited(DayRepeatType.REPEAT_EVERY_DAY); }
  @OnClick(R.id.text_repeat_workday)
  void onWorkdayClicked(View v) { onEdited(DayRepeatType.REPEAT_WORKDAY); }
  @OnClick(R.id.text_repeat_holiday)
  void onHolidayClicked(View v) { onEdited(DayRepeatType.REPEAT_HOLIDAY); }
  @OnClick(R.id.text_repeat_mon_to_fir)
  void onMonToFirClicked(View v) { onEdited(DayRepeatType.REPEAT_MONDAY_TO_FRIDAY); }
  @OnClick(R.id.text_repeat_mon_to_sat)
  void onMonToSatClicked(View v) { onEdited(DayRepeatType.REPEAT_MONDAY_TO_SATURDAY); }
  @OnClick(R.id.text_repeat_sun_to_fir)
  void onSunToFirClicked(View v) { onEdited(DayRepeatType.REPEAT_SUNDAY_TO_FRIDAY); }
  @OnClick(R.id.text_repeat_sat_to_sun)
  void onSatToSunClicked(View v) { onEdited(DayRepeatType.REPEAT_SATURDAY_TO_SUNDAY); }
  @OnClick(R.id.text_repeat_customize)
  void onCustomizeClicked(View v) {
    new EditCustomDayConditionDialog(context)
            .setValue(value)
            .setTitle(text_set_custom_condition)
            .setOnEditCustomDayConditionDialogFinishListener(new EditCustomDayConditionDialog.OnEditCustomDayConditionDialogFinishListener() {
                @Override
                public void onEdited() { EditRepeatDialog.this.onEdited(DayRepeatType.REPEAT_CUSTOM); }
                @Override
                public void onCanceled() {}
            })
            .show();
  }
  @OnClick(R.id.btn_cancel)
  void onCancelClicked(View v) {
    if (onEditRepeatDialogFinishListener!= null) { onEditRepeatDialogFinishListener.onCanceled(); }
    dismiss();
  }
}
