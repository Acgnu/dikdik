package com.acgnu.dikdick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewActivity extends AppCompatActivity {
    private Button mBtnSaveNew;
    private Button mBtnSeekSubstract;
    private Button mBtnSeekAdd;
    private SeekBar mSeekBar;
    private Context mContext;
    private EditText editText;
    private int MAX_DELAY = 900;
    public static final int MIN_DELAY = 240;
    private Intent mIntent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        mBtnSaveNew = findViewById(R.id.btn_save_new);
        mBtnSeekSubstract = findViewById(R.id.btn_seek_substract);
        mBtnSeekAdd = findViewById(R.id.btn_seek_add);
        mSeekBar = findViewById(R.id.seek_bar);
        editText = findViewById(R.id.edit_new_name);
        mContext = this;
        mIntent = new Intent(mContext, DickPlayService.class);

        //set if arg from main
        mSeekBar.setProgress(calcProgress(getIntent().getIntExtra("speed", (int) (new Float(MAX_DELAY) / 2) + MIN_DELAY)));
        editText.setText(getIntent().getStringExtra("title"));

        mBtnSeekSubstract.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSeekBar.getProgress() == 0) {
                    return;
                }
                int newProgress = mSeekBar.getProgress() - 1;
                mSeekBar.setProgress(newProgress);
                startDick(newProgress);
            }
        });

        mBtnSeekAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSeekBar.getProgress() == 100) {
                    return;
                }
                int newProgress = mSeekBar.getProgress() + 1;
                mSeekBar.setProgress(newProgress);
                startDick(newProgress);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startDick(seekBar.getProgress());
            }
        });

        mBtnSaveNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editText.getText().toString();
                if(TextUtils.isEmpty(title)) {
                    Toast.makeText(mContext, "好家伙, 标题不填了?", Toast.LENGTH_SHORT).show();
                    return;
                }
                int validDelay = calcDelay(mSeekBar.getProgress());
                DataUtil.instance().addNew(mContext, title, validDelay);
                Intent intent = getIntent();
                intent.putExtra("title", title);
                intent.putExtra("speed", validDelay);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        startDick(mSeekBar.getProgress());
    }

    private int calcDelay(int progress){
        float speed = new Float(progress);
        int delay = MAX_DELAY - (int) (MAX_DELAY * (speed / 100)) + MIN_DELAY;
        return delay;
    }

    private int calcProgress(int delay) {
        float delayF = new Float(delay);
        int p =  (int) (((delayF - MIN_DELAY) / MAX_DELAY) * 100);
        return 100 - p;
    }

    private void startDick(int progress){
        mIntent.putExtra("speed", calcDelay(progress));
        startService(mIntent);
    }

    @Override
    protected void onPause() {
        stopService(mIntent);
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                top = l[1],
                bottom = top + v.getHeight(),
                right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
