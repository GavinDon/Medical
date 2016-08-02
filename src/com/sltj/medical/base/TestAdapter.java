package com.sltj.medical.base;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by linan on 2016/1/8  17:50
 * Email:fengyunzhinan@163.com
 */

public class TestAdapter extends MyBaseAdapter<HashMap<String, String>> {
    private Context mContext;
    private List<HashMap<String, String>> srcData;
    /*
    通过view层传过来上下文 ，源数据。（这个数据可以是任何类型。baseAdapter中使用泛型类来接收数据）
     */

    public TestAdapter(Context context, List<HashMap<String, String>> srcData) {
        super(context, srcData);
        mContext = context;
    }

    /*
    获取到适配器需要填充的布局
     */
    @Override
    public View getLayout() {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = mInflater.inflate(R.layout.testadapter, null);
        //Todo return view;
        return null;
    }

    /*
    把适配器填充布局中的每一个小布局放在一个Int集合中
     */
    @Override
    public int[] getFindViewByIDs() {
        return new int[]{
//                R.id.tv_url, R.id.tv_adapter
        };
    }

    /*
    填充数据。(父类方法空实现 ，子类实现后。父类可直接引用)
     */

    @Override
    public void renderData(int position, ViewHolder vh) {
        HashMap<String, String> map = this.getItemT(position);
//        vh.getView(TextView.class, R.id.tv_adapter).setText(map.get("title"));
//        vh.getView(TextView.class, R.id.tv_url).setText(map.get("url"));
//        vh.getView(TextView.class, R.id.tv_adapter).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "你点我干嘛", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        TextView tv = vh.getView(R.id.tv_url);
    }
    DisplayMetrics display=new DisplayMetrics();


}
