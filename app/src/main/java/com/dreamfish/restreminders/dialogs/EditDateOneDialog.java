package com.dreamfish.restreminders.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dreamfish.restreminders.R;
import com.dreamfish.restreminders.utils.AlertDialogTool;
import com.dreamfish.restreminders.utils.TextUtils;
import com.zyyoona7.picker.DatePickerView;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditDateOneDialog extends Dialog {

  public EditDateOneDialog(@NonNull Context context) {
    super(context, R.style.WhiteRoundDialog);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dialog_edit_date_one);

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

  private Calendar value;

  private OnEditDateOneDialogFinishListener onEditDateOneDialogFinishListener;

  public interface OnEditDateOneDialogFinishListener{
    void onEdited(Date val);
    void onCanceled();
  }

  @BindView(R.id.text_title)
  TextView text_title;
  @BindView(R.id.date_picker)
  DatePickerView date_picker;

  private String title;

  public Date getValue() {
    return value.getTime();
  }
  public EditDateOneDialog setOnEditDateOneDialogFinishListener(OnEditDateOneDialogFinishListener onEditDateOneDialogFinishListener) {
    this.onEditDateOneDialogFinishListener = onEditDateOneDialogFinishListener;
    return this;
  }
  public EditDateOneDialog setValue(Date startVal) {
    this.value.setTime(startVal);
    return this;
  }
  public EditDateOneDialog setTitle(String t) { title = t; return this; }

  private void refreshView() {

    if(value!=null) {
      date_picker.setSelectedDay(value.get(Calendar.DAY_OF_MONTH));
      date_picker.setSelectedMonth(value.get(Calendar.MONTH));
      date_picker.setSelectedDay(value.get(Calendar.YEAR));
    }
    if(!TextUtils.isEmpty(title))
      text_title.setText(title);

  }

  private void getSelectedValue() {
    value.set(Calendar.DAY_OF_MONTH, date_picker.getSelectedDay());
    value.set(Calendar.MONTH, date_picker.getSelectedMonth());
    value.set(Calendar.YEAR, date_picker.getSelectedYear());
  }

  @OnClick(R.id.btn_cancel)
  void onCancelClicked(View v) {
    if (onEditDateOneDialogFinishListener!= null) { onEditDateOneDialogFinishListener.onCanceled(); }
    dismiss();
  }
  @OnClick(R.id.btn_ok)
  void onOkClicked(View v) {
    if (onEditDateOneDialogFinishListener!= null) {
      getSelectedValue();
      onEditDateOneDialogFinishListener.onEdited(getValue());
      dismiss();
    }
    else dismiss();
  }
}
