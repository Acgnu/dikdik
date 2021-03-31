package com.acgnu.dikdick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MergeListAdapter extends BaseAdapter {

  private LayoutInflater mInflater;
  private List<DataHolder> mDataList = new ArrayList<DataHolder>();
  private OnClickListener mDelClickListener;

  public MergeListAdapter(Context context, List<DataHolder> dataList, View.OnClickListener delClickListener) {
    mInflater = LayoutInflater.from(context);
    mDelClickListener = delClickListener;
    if (dataList != null && dataList.size() > 0) {
      mDataList.addAll(dataList);
    }
  }

  private static class ViewHolder {
    public TextView title;
  }

  public static class DataHolder {
    public String title;
    public int speed;
    public LinearLayout rootView;
  }

  @Override
  public int getCount() {
    return mDataList.size();
  }

  @Override
  public Object getItem(int i) {
    return mDataList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    ViewHolder holder;
    if (convertView == null || convertView.getTag() == null) {
      convertView = mInflater.inflate(R.layout.scroll_del_listview_item, viewGroup, false);
      holder = new ViewHolder();
      holder.title = (TextView) convertView.findViewById(R.id.title);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    DataHolder item = mDataList.get(i);
    holder.title.setText(item.title);
    item.rootView = (LinearLayout) convertView.findViewById(R.id.lin_root);
    item.rootView.scrollTo(0, 0);
    TextView delTv = (TextView) convertView.findViewById(R.id.del);
    delTv.setOnClickListener(mDelClickListener);
    return convertView;
  }

  public void removeItem(int position) {
    mDataList.remove(position);
    notifyDataSetChanged();
  }

  public void addItem(String title, int speed){
    DataHolder holder = new DataHolder();
    holder.title = title;
    holder.speed = speed;
    mDataList.add(holder);
    notifyDataSetChanged();
  }

  public DataHolder getAt(int position){
    return mDataList.get(position);
  }
}
