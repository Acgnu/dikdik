package com.acgnu.dikdick;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.acgnu.dikdick.MergeListAdapter.DataHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
  private MergeListAdapter adapter;
  private ScrollDelListView listView;
  private FloatingActionButton mFloatBtnStop;
  private Intent mDickPlayIntent;
  private static final int INTENT_ADD_REQUEST_CODE = 1;
  private TextView prevActiveTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = findViewById(R.id.listview);
    final List<DataHolder> items = new ArrayList<DataHolder>();
    mDickPlayIntent = new Intent(MainActivity.this, DickPlayService.class);
    mFloatBtnStop = findViewById(R.id.float_btn_stop);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

    JSONObject jsonObject = DataUtil.instance().readJson(this);
    Iterator<String> keys = jsonObject.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      try {
        DataHolder item = new DataHolder();
        item.title = key;
        item.speed = jsonObject.getInt(key);
        items.add(item);
      } catch (Exception e){
      }
    }
    adapter = new MergeListAdapter(this, items, new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (view.getId() == R.id.del) {
          int position = listView.getPositionForView(view);
          DataHolder dataHolder = adapter.getAt(position);
          DataUtil.instance().del(getApplicationContext(), dataHolder.title);
          adapter.removeItem(position);
        }
      }
    });
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DataHolder dataHolder = adapter.getAt(i);
        if(null != prevActiveTextView){
          prevActiveTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDefault));
        }
        TextView tv = view.findViewById(R.id.title);
        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        prevActiveTextView = tv;
        getSupportActionBar().setTitle("当前节拍: " + dataHolder.title);
        mDickPlayIntent.putExtra("speed", dataHolder.speed);
        startService(mDickPlayIntent);
      }
    });
    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        DataHolder dataHolder = adapter.getAt(i);
        Intent intent = new Intent(MainActivity.this, AddNewActivity.class);
        intent.putExtra("title", dataHolder.title);
        intent.putExtra("speed", dataHolder.speed);
        stopPlay();
        startActivityForResult(intent, INTENT_ADD_REQUEST_CODE);
        return true;
      }
    });

    mFloatBtnStop.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        stopPlay();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.app_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    stopPlay();
    startActivityForResult(new Intent(MainActivity.this, AddNewActivity.class), INTENT_ADD_REQUEST_CODE);
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == INTENT_ADD_REQUEST_CODE && resultCode == RESULT_OK) {
      String title = data.getStringExtra("title");
      int speed = data.getIntExtra("speed", 0);
      adapter.addItem(title, speed);
    }
  }

  private void stopPlay() {
    stopService(mDickPlayIntent);
    getSupportActionBar().setTitle(getTitle());
    if(null != prevActiveTextView){
      prevActiveTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDefault));
    }
  }
}
