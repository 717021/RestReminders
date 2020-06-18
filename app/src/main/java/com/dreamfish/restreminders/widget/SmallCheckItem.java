package com.dreamfish.restreminders.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamfish.restreminders.R;

import androidx.annotation.Nullable;

public class SmallCheckItem extends LinearLayout {

  public SmallCheckItem(Context context) {
    super(context);
    initView(context, null);
  }
  public SmallCheckItem(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
  }
  public SmallCheckItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context, attrs);
  }
  public SmallCheckItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView(context, attrs);
  }


  private TextView text;
  private CheckBox checkBox;
  private ImageButton deleteButton;
  private Object data;
  private boolean isDeleteMode = false;

  //初始化视图
  private void initView(final Context context, AttributeSet attributeSet) {
    View inflate = LayoutInflater.from(context).inflate(R.layout.item_small_check_item, this);
    text = inflate.findViewById(R.id.text);
    checkBox = inflate.findViewById(R.id.checkBox);
    deleteButton = inflate.findViewById(R.id.deleteButton);

    init(context,attributeSet);
  }
  //初始化资源文件
  public void init(Context context, AttributeSet attributeSet){
  }

  public CheckBox getCheckBox() { return checkBox; }
  public boolean isChecked() { return checkBox.isChecked(); }
  public void setChecked(boolean checked) { checkBox.setChecked(checked); }
  public void setText(String str) { text.setText(str); }
  public void setText(int resId) { text.setText(resId); }
  public void setText(CharSequence c) { text.setText(c); }
  public CharSequence getText() { return text.getText(); }
  public Object getData() {
    return data;
  }
  public void setData(Object data) {
    this.data = data;
  }
  public boolean isDeleteMode() {
    return isDeleteMode;
  }
  public void setDeleteMode(boolean deleteMode) {
    isDeleteMode = deleteMode;
    if(isDeleteMode) {
      checkBox.setVisibility(GONE);
      deleteButton.setVisibility(VISIBLE);
    } else {
      checkBox.setVisibility(VISIBLE);
      deleteButton.setVisibility(GONE);
    }
  }
  public ImageButton getDeleteButton() {
    return deleteButton;
  }
}
