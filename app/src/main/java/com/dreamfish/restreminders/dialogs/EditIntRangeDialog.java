package com.dreamfish.restreminders.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
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
import com.dreamfish.restreminders.utils.PixelTool;
import com.dreamfish.restreminders.utils.TextUtils;
import com.zyyoona7.picker.OptionsPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditIntRangeDialog extends Dialog {

  public EditIntRangeDialog(@NonNull Context context) {
    super(context, R.style.WhiteRoundDialog);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.dialog_edit_int_range);

    ButterKnife.bind(this);
    initEvent();
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

  private int startVal;
  private int endVal;

  private OnEditIntRangeDialogFinishListener onEditIntRangeDialogFinishListener;

  public interface OnEditIntRangeDialogFinishListener{
    void onEdited(int start, int end);
    void onCanceled();
  }

  @BindString(R.string.text_must_greater_than_zero)
  String text_must_greater_than_zero;
  @BindString(R.string.text_must_greater_than)
  String text_must_greater_than;
  @BindString(R.string.text_must_enter)
  String text_must_enter;

  private boolean error = false;

  @BindView(R.id.edit_start)
  EditText edit_start;
  @BindView(R.id.edit_end)
  EditText edit_end;
  @BindView(R.id.text_start)
  TextView text_start;
  @BindView(R.id.text_end)
  TextView text_end;
  @BindView(R.id.text_unit_start)
  TextView text_unit_start;
  @BindView(R.id.text_unit_end)
  TextView text_unit_end;
  @BindView(R.id.text_title)
  TextView text_title;
  @BindView(R.id.text_error)
  TextView text_error;

  private String textUnit;
  private String textStart;
  private String textEnd;
  private String title;

  public int getStartVal() {
    return startVal;
  }
  public int getEndVal() {
    return endVal;
  }
  public EditIntRangeDialog setOnEditIntRangeDialogFinishListener(OnEditIntRangeDialogFinishListener onEditIntRangeDialogFinishListener) {
    this.onEditIntRangeDialogFinishListener = onEditIntRangeDialogFinishListener;
    return this;
  }
  public EditIntRangeDialog setStartVal(int startVal) {
    this.startVal = startVal;
    return this;
  }
  public EditIntRangeDialog setEndVal(int endVal) {
    this.endVal = endVal;
    return this;
  }
  public EditIntRangeDialog setTextUnit(String t) { textUnit = t; return this; }
  public EditIntRangeDialog setTextStart(String t) {
    textStart = t;
    return this;
  }
  public EditIntRangeDialog setTextEnd(String t) {
    textEnd = t;
    return this;
  }
  public EditIntRangeDialog setTitle(String t) { title = t; return this; }

  /**
   * 初始化界面控件的显示数据
   */
  private void refreshView() {
    edit_start.setText(String.valueOf(startVal));
    edit_end.setText(String.valueOf(endVal));

    if(!TextUtils.isEmpty(textStart))
      text_start.setText(textStart);
    if(!TextUtils.isEmpty(textEnd))
      text_end.setText(textEnd);
    if(!TextUtils.isEmpty(title))
      text_title.setText(title);
    if(!TextUtils.isEmpty(textUnit)) {
      text_unit_start.setText(textUnit);
      text_unit_end.setText(textUnit);
    }
  }

  private void initEvent() {
    TextWatcher textWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) { }
      @Override
      public void afterTextChanged(Editable s) {
        getSelectedValue();
      }
    };
    edit_start.addTextChangedListener(textWatcher);
    edit_end.addTextChangedListener(textWatcher);
  }
  private void getSelectedValue() {
    String start = edit_start.getText().toString();
    String end = edit_end.getText().toString();
    if(!TextUtils.isEmpty(start)) startVal = Integer.valueOf(start).intValue();
    else {
      text_error.setText(String.format(text_must_enter, textStart));
      text_error.setVisibility(View.VISIBLE);
      error = true;
      return;
    }
    if(!TextUtils.isEmpty(end)) endVal = Integer.valueOf(end).intValue();
    else {
      text_error.setText(String.format(text_must_enter, textEnd));
      text_error.setVisibility(View.VISIBLE);
      error = true;
      return;
    }

    if(startVal <= 0) {
      text_error.setText(String.format(text_must_greater_than_zero, textStart));
      text_error.setVisibility(View.VISIBLE);
      error = true;
    } else if(endVal <= 0) {
      text_error.setText(String.format(text_must_greater_than_zero, textEnd));
      text_error.setVisibility(View.VISIBLE);
      error = true;
    } else if(endVal < startVal) {
      text_error.setText(String.format(text_must_greater_than, textEnd, textStart));
      text_error.setVisibility(View.VISIBLE);
      error = true;
    } else {
      text_error.setVisibility(View.GONE);
      error = false;
    }

  }

  @OnClick(R.id.btn_cancel)
  void onCancelClicked(View v) {
    if (onEditIntRangeDialogFinishListener!= null) { onEditIntRangeDialogFinishListener.onCanceled(); }
    dismiss();
  }
  @OnClick(R.id.btn_ok)
  void onOkClicked(View v) {
    if (onEditIntRangeDialogFinishListener!= null) {
      getSelectedValue();
      if(!error) {
        onEditIntRangeDialogFinishListener.onEdited(startVal, endVal);
        dismiss();
      }
    }
    else dismiss();
  }
}
