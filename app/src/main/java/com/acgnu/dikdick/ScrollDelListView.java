package com.acgnu.dikdick;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.acgnu.dikdick.MergeListAdapter.DataHolder;

public class ScrollDelListView extends ListView {
  private Context mContext;
  private LinearLayout itemRoot;
  private final int MAX_WIDTH = 100;
  private int mlastX = 0;
  //x的按下位置
  private int mXTouch = 0;
  //y的按下位置
  private int mYTouch = 0;

  public ScrollDelListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.mContext = context;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int maxLength = dipToPx(mContext, MAX_WIDTH);
    int x = (int) event.getX();
    int y = (int) event.getY();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        int position = pointToPosition(x, y);
        if (position != INVALID_POSITION) {
          DataHolder data = (DataHolder) getItemAtPosition(position);
          itemRoot = data.rootView;
        } else {
          itemRoot = null;
        }
        mXTouch = x;
        mYTouch = y;
      } break;
      case MotionEvent.ACTION_MOVE: {
        if(itemRoot != null){
          int scrollX = itemRoot.getScrollX();
          int newScrollX = scrollX + mlastX - x;
          if (newScrollX < 0) {
            newScrollX = 0;
          } else if (newScrollX > maxLength) {
            newScrollX = maxLength;
          }
          if(Math.abs(x - mXTouch) > Math.abs(y - mYTouch)) {
            itemRoot.scrollTo(newScrollX, 0);
          }
        }
      } break;
      case MotionEvent.ACTION_UP: {
        if(itemRoot != null){
          int scrollX = itemRoot.getScrollX();
          int newScrollX = scrollX + mlastX - x;
          if (scrollX > maxLength / 2) {
            newScrollX = maxLength;
          } else {
            newScrollX = 0;
          }
          itemRoot.scrollTo(newScrollX, 0);
        }
        mXTouch = 0;
        mYTouch = 0;
      } break;
    }
    mlastX = x;
    return super.onTouchEvent(event);
  }

  private int dipToPx(Context context, int dip) {
    return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
  }
}
