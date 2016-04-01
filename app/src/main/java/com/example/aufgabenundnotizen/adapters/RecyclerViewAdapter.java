package com.example.aufgabenundnotizen.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.helpers.JodaTimeUtils;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.models.TodoItem;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobias on 24.02.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Item> mItems;
    private List<TodoItem> mDoneItems = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    private Map<Integer, Drawable> mDrawables;
    private boolean mShowIcons;

    public RecyclerViewAdapter(List<Item> items) {
        mItems = items;
    }

    public RecyclerViewAdapter() {
        this(Collections.<Item>emptyList());
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
        void onItemLongClick(Item item);

        void onItemCheckedChanged(Item item, boolean isChecked);
    }

    public void swapData(List<Item> items) {
        mItems = items;

        this.notifyDataSetChanged();
    }

    public void addItem(Item item) {
        mItems.add(item);

        this.notifyItemInserted(mItems.size() - 1);
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

    public void deleteItem(String itemId){

        // Anhand der Id finden, nicht Referenz.
        int position = getPosition(itemId);

        if (position != -1) {
            mItems.remove(position);

            this.notifyItemRemoved(position);
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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);

        ImageView imvIcon = (ImageView) v.findViewById(R.id.imv_icon);
        AppCompatCheckBox chbItem = (AppCompatCheckBox) v.findViewById(R.id.chb_item);
        TextView tevContent = (TextView) v.findViewById(R.id.content);
        TextView tevDueDate = (TextView) v.findViewById(R.id.tev_duedate);

        if (!mShowIcons) {
            imvIcon.setVisibility(View.GONE);
        }

        if (viewType == R.id.todo_item) {
            if (mDrawables != null) {
                Drawable drawable = mDrawables.get(R.id.todo_item);
                imvIcon.setImageDrawable(drawable);
            }

        } else if (viewType == R.id.note_item) {
            if (mDrawables != null) {
                Drawable drawable = mDrawables.get(R.id.note_item);
                imvIcon.setImageDrawable(drawable);
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tevContent.getLayoutParams();
            params.setMargins(50, 0, 0, 0);

            tevContent.setLayoutParams(params);

            chbItem.setVisibility(View.GONE);
            tevDueDate.setVisibility(View.GONE);
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position){
        Item item = mItems.get(position);

        if (item instanceof TodoItem) {
            return R.id.todo_item;
        } else {
            return R.id.note_item;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // init List Item
        holder.mItem = mItems.get(position);
        holder.mContentView.setText(mItems.get(position).getTitle());

        Item item = mItems.get(position);

        if (item instanceof TodoItem) {
            // verhindern von Endlosrekursion
            holder.mChbDone.setOnCheckedChangeListener(null);
            holder.mChbDone.setChecked(((TodoItem) item).getDone());
            holder.mChbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mOnItemClickListener.onItemCheckedChanged(holder.mItem, isChecked);
                }
            });

            LocalDate dueDate = ((TodoItem) item).getDueDate();
            String formattedDateString = JodaTimeUtils.getFormattedDateString(dueDate);

            holder.mTevDueDate.setText(formattedDateString);
        }

        if (mOnItemClickListener != null) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.mItem);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.mItem);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setDrawables(Map<Integer, Drawable> drawables) {
        mDrawables = drawables;
    }

    public void setShowIcons(boolean showIcons) {
        mShowIcons = showIcons;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final AppCompatCheckBox mChbDone;
        public final TextView mTevDueDate;
        public Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            mChbDone = (AppCompatCheckBox) view.findViewById(R.id.chb_item);
            mTevDueDate = (TextView) view.findViewById(R.id.tev_duedate);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
