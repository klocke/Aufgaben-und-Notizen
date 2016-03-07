package com.example.aufgabenundnotizen.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.activities.ItemDetailActivity;
import com.example.aufgabenundnotizen.fragments.ItemDetailFragment;
import com.example.aufgabenundnotizen.fragments.ItemListFragment;
import com.example.aufgabenundnotizen.helpers.Args;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.models.NoteItem;
import com.example.aufgabenundnotizen.models.TodoItem;
import com.example.aufgabenundnotizen.other.FilterType;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tobias on 24.02.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ItemListFragment mCaller;
    private FragmentManager mManager;

    private List<Item> mItems;

    public RecyclerViewAdapter(ItemListFragment fragment, FragmentManager manager, List<Item> items) {
        mCaller = fragment;
        mManager = manager;
        mItems = items;
    }

    public RecyclerViewAdapter(ItemListFragment fragment, FragmentManager manager) {
        this(fragment, manager, Collections.<Item>emptyList());
    }

    public void swapData(List<Item> items) {
        mItems = items;
        this.notifyDataSetChanged();
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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaller.isTwoPane()) {
                    Bundle arguments = new Bundle();
                    arguments.putString(Args.ARG_ITEM_ID, holder.mItem.getId());
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);

                    mCaller.getChildFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();

                    FilterType filterType = null;

                    if (holder.mItem instanceof TodoItem) {
                        filterType = FilterType.TODOS;

                    } else if (holder.mItem instanceof NoteItem) {
                        filterType = FilterType.NOTES;

                    }

                    ItemDetailActivity.start(v.getContext(), holder.mItem.getId(), filterType);
                }
            }
        });
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
