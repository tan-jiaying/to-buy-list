package com.bignerdranch.android.tobuylist;

import android.support.v4.app.Fragment;

public class ItemListActivity extends SingleFragmentActivity{ // controller class

    @Override
    protected Fragment createFragment() {
        return new ItemListFragment();
    }
}
