package com.example.ghicefox.ordersystem.Utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ghicefox.ordersystem.Activity.MenuActivity;
import com.example.ghicefox.ordersystem.Models.MenuItem;
import com.example.ghicefox.ordersystem.R;

import java.util.List;

public class RightAdapter extends BaseAdapter {
    private MenuActivity context;
    private List<MenuItem> list;
    public RightAdapter(MenuActivity context, int menu_item, List<MenuItem> objects) {
        this.context = context;
        this.list = objects;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        private TextView mCount;
        private TextView mName;
        private Button mAdd;
        private Button mReduce;
        private TextView mPrice;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.menu_item,null);
            holder = new ViewHolder();
            holder.mCount = (TextView) convertView.findViewById(R.id.item_count);
            holder.mName = (TextView) convertView.findViewById(R.id.item_name);
            holder.mAdd = (Button) convertView.findViewById(R.id.item_add);
            holder.mReduce = (Button) convertView.findViewById(R.id.item_reduce);
            holder.mPrice = (TextView) convertView.findViewById(R.id.item_price);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mName.setText(list.get(position).getItemName());
        holder.mCount.setText(String.valueOf(list.get(position).getCount()));
        holder.mPrice.setText(list.get(position).getPrice()+"元/每例");
        holder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = list.get(position).getCount();
                String name = list.get(position).getItemName();
                int price = list.get(position).getPrice();
                int totalPrice = context.getTotalMoney();
                totalPrice += price;
                context.setTotalMoney(totalPrice);
                list.get(position).setCount(count+1);
                holder.mCount.setText(String.valueOf(count+1));
            }
        });
        holder.mReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = list.get(position).getCount();
                String name = list.get(position).getItemName();
                int price = list.get(position).getPrice();
                if(count==0)
                    ToastUtils.showToast(context,"商品数量不能为负数");
                else{
                    int totalPrice = context.getTotalMoney();
                    totalPrice -= price;
                    context.setTotalMoney(totalPrice);
                    list.get(position).setCount(count-1);
                    holder.mCount.setText(String.valueOf(count-1));
                }
            }
        });
        return convertView;
    }
}
