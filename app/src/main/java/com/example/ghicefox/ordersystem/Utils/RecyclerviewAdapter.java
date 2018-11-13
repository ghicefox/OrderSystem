package com.example.ghicefox.ordersystem.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ghicefox.ordersystem.Activity.MenuActivity;
import com.example.ghicefox.ordersystem.Models.MenuItem;
import com.example.ghicefox.ordersystem.R;

import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {

    private MenuActivity context;
    private List<MenuItem> list;

    public RecyclerviewAdapter(MenuActivity context, List<MenuItem> objects){
        this.context = context;
        this.list = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mName.setText(list.get(position).getItemName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("这里是点击每一行item的响应事件",""+position);
                int menuId = list.get(position).getId();
                context.getMenuItemInfo(menuId);
            }
        });
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mCount;
        private TextView mName;
        private Button mAdd;
        private Button mReduce;
        private TextView mPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            mCount = (TextView) itemView.findViewById(R.id.item_count);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            mAdd = (Button) itemView.findViewById(R.id.item_add);
            mReduce = (Button) itemView.findViewById(R.id.item_reduce);
            mPrice = (TextView) itemView.findViewById(R.id.item_price);

        }
    }

}
