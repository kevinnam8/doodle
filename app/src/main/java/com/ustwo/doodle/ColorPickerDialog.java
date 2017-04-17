package com.ustwo.doodle;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A Dialog that displays a colour tables in a gridlayout to choose one of colour
 * @author Kevin Nam
 */
public class ColorPickerDialog extends DialogFragment {

    Activity mActivity;
    private ViewGroup mLayout;
    TextView mTvTitle;
    private String mTitleString = null;

    RecyclerView mColorPickerRecycler;
    ColorItemAdapter mColorItemAdapter;

    int mSelectedItem;
    ColorItemAdapter.ItemSelectedListener mSelectedListener;

    public ColorPickerDialog() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        mLayout = (ViewGroup)inflater.inflate(R.layout.dialog_color_picker, container, false);
        mTvTitle = (TextView)mLayout.findViewById(R.id.tvTitle);
        if(mTitleString != null) {
            mTvTitle.setText(mTitleString);
        }

        setupRecycler(mLayout);
        return mLayout;
    }

    /**
     * Set the dialog's title text.
     * If Title TextView was initialised, set the title to the TextView as well.
     * @param title - text to be set to the title.
     */
    public void setTitleText(String title) {
        mTitleString = title;
        if(mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }

    public void setSelectedItem(int selectedItem) {
        mSelectedItem = selectedItem;
    }

    public void setItemSelectedListener(ColorItemAdapter.ItemSelectedListener selectedListener) {
        mSelectedListener = selectedListener;
    }

    /**
     * Shows the Color picker dialog after creating the instance.
     * @param activity
     * @param dialogTitle
     * @param selectedItem
     * @param selectedListener
     */
    public static void showColorPickerDialog(Activity activity,
                                             String dialogTitle,
                                             int selectedItem,
                                             final ColorItemAdapter.ItemSelectedListener selectedListener) {

        FragmentManager fm = activity.getFragmentManager();
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.setTitleText(dialogTitle);
        colorPickerDialog.setSelectedItem(selectedItem);
        colorPickerDialog.setItemSelectedListener(new ColorItemAdapter.ItemSelectedListener() {
                                                      @Override
                                                      public void onItemSelected(int color, int position) {
                                                          if(selectedListener != null) {
                                                              selectedListener.onItemSelected(color, position);
                                                          }
                                                          colorPickerDialog.dismiss();
                                                      }
                                                  });
        colorPickerDialog.show(fm, "fragment_color_picker_dialog");
    }

    /**
     * Setup a recycleView and adapter for the color picker
     */
    void setupRecycler(View rootView) {
        int columnCount = mActivity.getResources().getInteger(R.integer.color_picker_column_count);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, columnCount);
        mColorPickerRecycler = (RecyclerView)rootView.findViewById(R.id.recyclerColorPicker);
        mColorPickerRecycler.setLayoutManager(layoutManager);

        mColorItemAdapter = new ColorItemAdapter(mActivity);
        mColorPickerRecycler.setAdapter(mColorItemAdapter);
        mColorItemAdapter.setSelectedItem(mSelectedItem);
        mColorItemAdapter.setItemSelectedListener(mSelectedListener);

        int itemSize = (int)getResources().getDimension(R.dimen.default_color_picker_item_size);
        mColorItemAdapter.setItemSize(itemSize);
        mColorPickerRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int leftWas, int topWas, int rightWas, int bottomWas) {
                // Color item size should be adjusted in case layout size is changed.
                int dialogWidth = right - left;
                int itemSize = dialogWidth / getResources().getInteger(R.integer.color_picker_column_count);
                mColorItemAdapter.setItemSize(itemSize);
            }
        });
    }
}
