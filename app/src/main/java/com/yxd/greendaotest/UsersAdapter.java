package com.yxd.greendaotest;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yxd.greendaotest.helper.ItemTouchHelperAdapter;
import com.yxd.greendaotest.helper.ItemTouchHelperViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by a on 2017/2/16.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> implements ItemTouchHelperAdapter {

    private List<User> users;
    private OnItemDeletListener mOnItemDeletListener;

    public UsersAdapter(OnItemDeletListener listener){
        this.mOnItemDeletListener = listener;
        this.users = new ArrayList<User>();
    }

    public void setUsers(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }

    public User getUser(int position){
        return users.get(position);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UsersAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getName());
        holder.tvAge.setText(user.getAge()+"");
        holder.tvSex.setText(user.getSex());
        holder.tvDate.setText(user.getDate());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(users, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mOnItemDeletListener.onDelet(position);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
        TextView tvName;
        TextView tvAge;
        TextView tvSex;
        TextView tvDate;
        LinearLayout linearLayout;

        public UserViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvAge = (TextView) v.findViewById(R.id.tv_age);
            tvSex = (TextView) v.findViewById(R.id.tv_sex);
            tvDate = (TextView) v.findViewById(R.id.tv_date);
            linearLayout = (LinearLayout) v.findViewById(R.id.ll_container);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public interface OnItemDeletListener{
        void onDelet(int position);
    }
}
