package xiaofan.yiapp.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by zhaoyu on 2014/10/19
 * .
 */
public abstract class NewFragmentStatePagerAdapter extends PagerAdapter{

    private static final String TAG = NewFragmentStatePagerAdapter.class.getSimpleName();
    private static final boolean DEBUG = false;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem = null;
    private final FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private long[] mItemIds = new long[0];
    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();

    protected NewFragmentStatePagerAdapter(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
        this.mItemIds = new long[getCount()];
        for(int i = 0; i < mItemIds.length;i++){
            mItemIds[i] = getItemId(i);
        }
    }


    public abstract Fragment getItem(int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }
        mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

 /*   @Override
    public void notifyDataSetChanged() {
        long[] newItemIds = new long[getCount()];
        for (int i = 0; i < newItemIds.length; i++){
            newItemIds[i] = getItemId(i);
        }
        if(!Arrays.equals(mItemIds,newItemIds)){
            ArrayList<Fragment.SavedState> newSavedState = new ArrayList<Fragment.SavedState>();
            ArrayList<Fragment> newFragments = new ArrayList<Fragment>();
            Fragment.SavedState savedState = null;
            for(int j = 0; j < mItemIds.length;j++){
                int k = -2;
                for(int m = 0; m < mSavedState.size();m++){
                    if(m < newItemIds.length){
                        if(mItemIds[m] == newItemIds[m]){
                            k = m;
                        }else{
                            if(k < 0) break;
                            if(j > mSavedState.size()) break;
                            savedState = mSavedState.get(j);
                            if(savedState == null) break;
                            while (newSavedState.size() <= k){
                                newSavedState.add(null);
                            }
                            newSavedState.set(j,savedState);
                        }
                    }
                }
                if(j < mFragments.size()){
                    Fragment fragment = mFragments.get(j);
                    if(fragment != null){
                        while (newFragments.size() <= k || k == -2){
                            newFragments.add(null);
                        }
                        newFragments.set(j,fragment);
                    }
                }
            }

            mItemIds = newItemIds;
            mFragments = newFragments;
            mSavedState = newSavedState;
        }
        super.notifyDataSetChanged();
    }*/


    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            mItemIds = bundle.getLongArray("itemids");
            if(mItemIds == null){
                mItemIds = new long[0];
            }
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i=0; i<fss.length; i++) {
                    mSavedState.add((Fragment.SavedState)fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key: keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    @Override
    public Parcelable saveState() {
        Bundle state = new Bundle();
        if (mItemIds.length > 0) {
            state.putLongArray("itemids", this.mItemIds);
        }
        if (mSavedState.size() > 0) {
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i=0; i<mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void startUpdate(ViewGroup container) {
       // super.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    public long getItemId(int position){
        return position;
    }

}
