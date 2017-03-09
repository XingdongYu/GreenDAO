package com.yxd.greendaotest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a on 2017/3/9.
 */

public class Adapter extends BaseAdapter implements Filterable{

    private List<User> users = new ArrayList<>();
    private Context mContext;
    private ArrayList<User> mUnfilteredData;
    private ArrayFilter mFilter;

    public Adapter(Context context, List<User> users){
        this.mContext = context;
        this.users = users;
    }

    public void setUsers(List<User> users){
        this.users = users;
    }

    public List<User> getUsers(){
        return users;
    }

    @Override
    public int getCount() {
        return users == null ? 0 : users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null){
            view = View.inflate(mContext, R.layout.auto_text_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.tv_item);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.textView.setText(users.get(position).getName());
        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null){
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    static class ViewHolder{
        public TextView textView;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<User>(users);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<User> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<User> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<User> newValues = new ArrayList<User>(count);

                for (int i = 0; i < count; i++) {
                    User pc = unfilteredValues.get(i);
                    if (pc != null) {

                        if(pc.getName()!=null && pc.getName().startsWith(prefixString)){

                            newValues.add(pc);
                        }else if(pc.getSex()!=null && pc.getSex().startsWith(prefixString)){

                            newValues.add(pc);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //noinspection unchecked
            users = (List<User>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
