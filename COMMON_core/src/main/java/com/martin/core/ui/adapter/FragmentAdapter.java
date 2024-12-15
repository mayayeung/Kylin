package com.martin.core.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter  {
    private List<Fragment> mFragments;

    public FragmentAdapter(FragmentManager fragmentManager, List<Fragment> mFragments) {
        super(fragmentManager);
        this.mFragments = mFragments;
    }


    public FragmentAdapter(FragmentManager fragmentManager, Fragment []mFragmentArray) {
        super(fragmentManager);
        this.mFragments =  new ArrayList<>(Arrays.asList(mFragmentArray));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        if(isFragmentsDetachedOrDestroyed()){
            try {
                Field mFragments = getClass().getSuperclass().getDeclaredField("mFragments");
                mFragments.setAccessible(true);
                ((ArrayList) mFragments.get(this)).clear();

                Field mSavedState = getClass().getSuperclass().getDeclaredField("mSavedState");
                mSavedState.setAccessible(true);
                ((ArrayList) mSavedState.get(this)).clear();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return super.instantiateItem(container, position);
    }

    /**
     * @return 是否fragment被系统给detach或者销毁了
     */
    public boolean isFragmentsDetachedOrDestroyed() {
        if (getCount() > 0 && mFragments != null && mFragments.size() > 0) {
            for (int i = 0; i < mFragments.size(); i++) {
                if (mFragments.get(i) == null || mFragments.get(i).isDetached() || !mFragments.get(i).isAdded()) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
    }
}