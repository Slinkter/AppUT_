package com.cudpast.apputdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cudpast.apputdemo.R;
import com.cudpast.apputdemo.Support.ScreenItem;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {


    private Context mContext;
    private List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater li;
        View view ;

        ImageView image ;
        TextView title;
        TextView description;

        li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.layout_screen,null);

        image = view.findViewById(R.id.intro_image);
        title = view.findViewById(R.id.intro_title);
        description = view.findViewById(R.id.intro_description);

        image.setImageResource(mListScreen.get(position).getScreenImg());
        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

