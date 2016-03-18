package com.example.aufgabenundnotizen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.models.Item;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tobias on 24.02.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Item> mItems;

    private OnItemClickListener mOnItemClickListener;

    public RecyclerViewAdapter(List<Item> items) {
        mItems = items;
    }

    public RecyclerViewAdapter() {
        this(Collections.<Item>emptyList());
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public void swapData(List<Item> items) {
        mItems = items;
        this.notifyDataSetChanged();
    }

    public void addItem(Item item) {
        mItems.add(item);

        this.notifyDataSetChanged();
    }

    public void updateItem(Item item) {
        String itemId = item.getId();

        // Anhand der Id finden, nicht Referenz.
        int position = getPosition(itemId);

        if (position != -1) {
            mItems.set(position, item);

            this.notifyDataSetChanged();
        }
    }

    /**
     * @return -1 Wenn itemId nicht gefunden werden kann.
     */
    public int getPosition(String itemId) {
        for (int i = 0; i < mItems.size(); i++) {
            Item currItem = mItems.get(i);

            if (currItem.getId().equals(itemId)) {
                return i;
            }
        }

        return -1;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        holder.mContentView.setText(mItems.get(position).getTitle());

        if (mOnItemClickListener != null) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.mItem);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
