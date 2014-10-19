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
        Fragment fragment = (Fragment) object;
        if(mCurTransaction == null) mCurTransaction = mFragmentManager.beginTransaction();
        int i = getItemPosition(object);
        if(i > 0){
            // remove the frgament
            if(mFragments.size() > i){
                mFragments.set(i,null);
            }
            //ensure that is the correct saved position
            while (mSavedState.size() <= i){
                mSavedState.add(null);
            }
            mSavedState.set(i,mFragmentManager.saveFragmentInstanceState(fragment));
            mCurTransaction.remove(fragment);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(mFragments.size() > position){
            Fragment fragment = mFragments.get(position);
            return fragment;
        }
        if(mCurTransaction == null) mCurTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = getItem(position);
        if(mSavedState.size() > position){
            Fragment.SavedState savedState = mSavedState.get(position);
            if(savedState != null){
                fragment.setInitialSavedState(savedState);
            }
        }
        while (mFragments.size() <= position){
            mFragments.add(null);
        }
        fragment.setMenuVisibility(true);
        mFragments.set(position,fragment);
        mCurTransaction.add(container.getId(),fragment);
        return fragment;
    }

    @Override
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
                        while (newFragments.size() <= k){
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
    }


    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if(state != null){
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            mItemIds = bundle.getLongArray("itemids");
            if(mItemIds == null){
                mItemIds = new long[0];
            }
            Parcelable[] states = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if(states != null){
               for (int j = 0; j < states.length; j++){
                   mSavedState.add((Fragment.SavedState)states[j]);
               }
            }
            Iterator<String> iterator = bundle.keySet().iterator();
            while (iterator.hasNext()){
                String str = iterator.next();
                if(str.startsWith("f")){
                    int i = Integer.parseInt(str.substring(1));
                    Fragment fragment = mFragmentManager.getFragment(bundle,str);
                    if(fragment != null){
                        while (mFragments.size() <= i){
                            mFragments.add(null);
                        }
                        fragment.setMenuVisibility(true);
                        mFragments.set(i,fragment);
                    }
                }else{
                    Log.w("FragmentStatePagerAdapter", "Bad fragment at key " + str);
                }
            }
        }
    }

    @Override
    public Parcelable saveState() {
        Bundle bundle = new Bundle();
        if(mItemIds.length > 0){
            bundle.putLongArray("itemids",mItemIds);
        }
        if(mSavedState.size() > 0){
            Fragment.SavedState[] savedStateArray = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(savedStateArray);
            bundle.putParcelableArray("states",savedStateArray);
        }
        for(int i = 0; i < mFragments.size(); i++){
            Fragment fragment = mFragments.get(i);
            if(fragment != null){
                String str = "f"+i;
                mFragmentManager.putFragment(bundle,str,fragment);
            }

        }
        return bundle;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment  = (Fragment) object;
        if(fragment != mCurrentPrimaryItem){
            if(mCurrentPrimaryItem != null){
                mCurrentPrimaryItem.setMenuVisibility(false);
            }
            if(fragment != null){
                fragment.setMenuVisibility(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void startUpdate(ViewGroup container) {
       // super.startUpdate(container);
    }

    @Override
    public void finishUpdate(View container) {
        if (this.mCurTransaction != null)
        {
            this.mCurTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return ((Fragment)o).getView() == view;
    }

    public long getItemId(int position){
        return position;
    }

}
