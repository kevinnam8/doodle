package com.ustwo.doodle;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ColorItemAdapter provides a binding from an colour set to views that are displayed within a RecyclerView.
 */
public class ColorItemAdapter extends RecyclerView.Adapter<ColorItemAdapter.ViewHolder> {
    String[] colorStringArray;
    int mSelectedItem = 0;
    int mItemSize = 0;
    ItemSelectedListener mSelectedListener;

    public ColorItemAdapter(Context context) {
        colorStringArray = context.getResources().getStringArray(R.array.color_picker_item_array);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_picker, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String colorCode = colorStringArray[position];
        final int color = Color.parseColor(colorCode);
        final int itemIndex = position;
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectedListener != null) {
                    mSelectedListener.onItemSelected(color, itemIndex);
                }
            }
        });

        holder.bindView(mItemSize,
                color,
                position,
                (position == mSelectedItem));
    }

    @Override
    public int getItemCount() {
        return colorStringArray.length;
    }

    public void setSelectedItem(int position) {
        if(mSelectedItem != position) {
            notifyItemChanged(mSelectedItem);
            notifyItemChanged(position);
            mSelectedItem = position;
        }
    }

    /**
     * @param itemSize - Width and height of an item
     */
    public void setItemSize(int itemSize) {
        mItemSize = itemSize;
        notifyDataSetChanged();
    }

    public void setItemSelectedListener(ItemSelectedListener selectedListener) {
        mSelectedListener = selectedListener;
    }

    public interface ItemSelectedListener {
        void onItemSelected(int color, int position);
    }

//region ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected View mItemView;
        private View mSelectedBorder;
        private View mColorItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mSelectedBorder = itemView.findViewById(R.id.layoutSelectedBorder);
            mColorItemView = itemView.findViewById(R.id.viewColorItem);
        }

        public void bindView(int itemSize, final int color, final int position, boolean bSelected) {
            if(itemSize > 0) {
                mItemView.getLayoutParams().width = itemSize;
                mItemView.getLayoutParams().height = itemSize;
            }
            mColorItemView.setBackgroundColor(color);
            if(bSelected) {
                mSelectedBorder.setBackgroundColor(ContextCompat.getColor(mItemView.getContext(), R.color.colorAccent));
            } else {
                mSelectedBorder.setBackgroundColor(ContextCompat.getColor(mItemView.getContext(), R.color.colorWhite));
            }
        }

    }
//endregion
}
