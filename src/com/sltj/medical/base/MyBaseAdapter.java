package com.sltj.medical.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by linan on 2016/1/8  17:42
 * Email:fengyunzhinan@163.com
 */
public abstract class MyBaseAdapter<TItem> extends BaseAdapter {

    protected Context mContext;
    private List<TItem> lst;


    public abstract int[] getFindViewByIDs();

    public abstract View getLayout();

    public MyBaseAdapter(Context context, List<TItem> lst) {
        this.mContext = context;
        this.lst = lst;

    }


    public void insert(TItem data) {
        lst.add(0, data);
        this.notifyDataSetChanged();
    }

    public void append(TItem data) {
        lst.add(data);
        this.notifyDataSetChanged();
    }

    public void replace(TItem data) {
        int idx = this.lst.indexOf(data);
        this.replace(idx, data);
    }

    public void replace(int index, TItem data) {
        if (index < 0) return;
        if (index > lst.size() - 1) return;
        lst.set(index, data);
        this.notifyDataSetChanged();
    }

    public List<TItem> getItems() {
        return lst;
    }

    public TItem getItemT(int position) {
        return lst.get(position);
    }


    public void removeItem(int position) {
        if (lst.size() <= 0) return;
        if (position < 0) return;
        if (position > lst.size() - 1) return;

        lst.remove(position);
        this.notifyDataSetChanged();
    }

    public void clear() {
        lst.clear();
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return lst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = this.getLayout();
            int[] idAry = this.getFindViewByIDs();
            if (idAry == null) idAry = new int[]{};


            //在viewHolder中我们需要把不同的ID赋值给他来实现缓存
            for (int id : idAry) {
                vh.setView(id, convertView.findViewById(id));
            }
            convertView.setTag(vh);
        } else
            vh = (ViewHolder) convertView.getTag();
        //提出数据
        this.renderData(position, vh);
        return convertView;
    }


    public abstract void renderData(int position, ViewHolder vh);

}



