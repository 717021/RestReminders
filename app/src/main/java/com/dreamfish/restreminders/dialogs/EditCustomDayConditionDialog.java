package com.dreamfish.restreminders.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.dreamfish.restreminders.R;
import com.dreamfish.restreminders.model.auto.DayCondition;
import com.dreamfish.restreminders.model.auto.IDayConditionGroup;
import com.dreamfish.restreminders.utils.AlertDialogTool;
import com.dreamfish.restreminders.utils.DateUtils;
import com.dreamfish.restreminders.utils.TextUtils;
import com.dreamfish.restreminders.widget.SmallCheckItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditCustomDayConditionDialog extends Dialog {

  public EditCustomDayConditionDialog(@NonNull Context context) {
    super(context, R.style.WhiteRoundDialog);
    this.context = context;
  }

  private Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dialog_edit_custom_day_condition);

    ButterKnife.bind(this);
    initEvents();
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

  private OnEditCustomDayConditionDialogFinishListener onEditCustomDayConditionDialogFinishListener;

  public interface OnEditCustomDayConditionDialogFinishListener{
    void onEdited();
    void onCanceled();
  }

  private String title;
  private IDayConditionGroup value;

  public EditCustomDayConditionDialog setOnEditCustomDayConditionDialogFinishListener(OnEditCustomDayConditionDialogFinishListener onEditCustomDayConditionDialogFinishListener) {
    this.onEditCustomDayConditionDialogFinishListener = onEditCustomDayConditionDialogFinishListener;
    return this;
  }
  public EditCustomDayConditionDialog setValue(IDayConditionGroup val) {
    this.value = val;
    return this;
  }
  public IDayConditionGroup getValue() {
    return value;
  }
  public EditCustomDayConditionDialog setTitle(String t) { title = t; return this; }

  @BindString(R.string.text_add_custom_condition)
  String text_add_custom_condition;

  @BindView(R.id.text_title)
  TextView text_title;

  @BindView(R.id.text_con_mon)
  SuperTextView text_con_mon;
  @BindView(R.id.text_con_tue)
  SuperTextView text_con_tue;
  @BindView(R.id.text_con_wed)
  SuperTextView text_con_wed;
  @BindView(R.id.text_con_thu)
  SuperTextView text_con_thu;
  @BindView(R.id.text_con_fir)
  SuperTextView text_con_fir;
  @BindView(R.id.text_con_sat)
  SuperTextView text_con_sat;
  @BindView(R.id.text_con_sun)
  SuperTextView text_con_sun;
  @BindView(R.id.view_custom_con)
  LinearLayout view_custom_con;

  private List<DayCondition> conditionsList = new ArrayList<>();

  private void initEvents() {
    text_con_mon.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.MONDAY, isChecked);
    });
    text_con_tue.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.TUESDAY, isChecked);
    });
    text_con_wed.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.WEDNESDAY, isChecked);
    });
    text_con_thu.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.THURSDAY, isChecked);
    });
    text_con_fir.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.FRIDAY, isChecked);
    });
    text_con_sat.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.SATURDAY, isChecked);
    });
    text_con_sun.getCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
      addOrDeleteDayOfWeekCon(Calendar.SUNDAY, isChecked);
    });
  }
  private void addOrDeleteDayOfWeekCon(int dayOfWeek, boolean add) {
    boolean finded = false;
    DayCondition dayCondition = null;
    int findIndex = 0;
    for (int i = conditionsList.size() - 1; i >= 0; i--) {
      dayCondition = conditionsList.get(i);
      if(dayCondition.getType() == DayCondition.TYPE_DAY_OF_WEEK)
        if(dayOfWeek == dayCondition.getDayOfWeek()) {
          finded = true;
          findIndex = i;
          break;
        }
    }
    if(finded && !add)
      conditionsList.remove(findIndex);
    else if(!finded && add)
      conditionsList.add(new DayCondition(dayOfWeek));
  }
  private void refreshView() {

    if(!TextUtils.isEmpty(title))
      text_title.setText(title);

    conditionsList.clear();
    conditionsList.addAll(value.getDayConditions());

    view_custom_con.removeAllViews();

    for (DayCondition dcn : conditionsList) {
      if(dcn.getType() == DayCondition.TYPE_DAY_OF_WEEK) {
        switch (dcn.getDayOfWeek()) {
          case Calendar.SUNDAY: text_con_sun.getCheckBox().setChecked(true); break;
          case Calendar.MONDAY: text_con_mon.getCheckBox().setChecked(true); break;
          case Calendar.TUESDAY: text_con_tue.getCheckBox().setChecked(true); break;
          case Calendar.WEDNESDAY: text_con_wed.getCheckBox().setChecked(true); break;
          case Calendar.THURSDAY: text_con_thu.getCheckBox().setChecked(true);  break;
          case Calendar.FRIDAY: text_con_fir.getCheckBox().setChecked(true);  break;
          case Calendar.SATURDAY: text_con_sat.getCheckBox().setChecked(true);  break;
        }
      }else {
        SmallCheckItem smallCheckItem = new SmallCheckItem(context);
        smallCheckItem.setDeleteMode(true);
        smallCheckItem.setText(DateUtils.format(dcn.getDate(), DateUtils.FORMAT_SHORT_CN));
        smallCheckItem.setOnClickListener(this::onItemDeleteClicked);
        smallCheckItem.setData(dcn);
        view_custom_con.addView(smallCheckItem);
      }
    }
  }
  private void getSelectedValue() {
    value.setDayConditions(conditionsList);
  }


  @OnClick(R.id.btn_add)
  void onAddCustomConClicked(View v) {
    new EditDateOneDialog(context)
            .setTitle(text_add_custom_condition)
            .setValue(new Date())
            .setOnEditDateOneDialogFinishListener(new EditDateOneDialog.OnEditDateOneDialogFinishListener() {
              @Override
              public void onEdited(Date val) {
                DayCondition dayCondition = new DayCondition(val);
                SmallCheckItem smallCheckItem = new SmallCheckItem(context);
                smallCheckItem.setDeleteMode(true);
                smallCheckItem.setText(DateUtils.format(dayCondition.getDate(), DateUtils.FORMAT_SHORT_CN));
                smallCheckItem.setOnClickListener(EditCustomDayConditionDialog.this::onItemDeleteClicked);
                smallCheckItem.setData(dayCondition);
                view_custom_con.addView(smallCheckItem);
              }
              @Override
              public void onCanceled() {}
            })
            .show();
  }
  void onItemDeleteClicked(View v) {
    if(v instanceof SmallCheckItem) {
      SmallCheckItem smallCheckItem = (SmallCheckItem)v;
      conditionsList.remove((DayCondition) smallCheckItem.getData());
      view_custom_con.removeView(v);
    }
  }
  @OnClick(R.id.btn_ok)
  void onOKClicked(View v) {
    if (onEditCustomDayConditionDialogFinishListener!= null) {
      getSelectedValue();
      onEditCustomDayConditionDialogFinishListener.onEdited();
    }
    else dismiss();
  }
  @OnClick(R.id.btn_cancel)
  void onCancelClicked(View v) {
    if (onEditCustomDayConditionDialogFinishListener!= null) { onEditCustomDayConditionDialogFinishListener.onCanceled(); }
    dismiss();
  }
}
