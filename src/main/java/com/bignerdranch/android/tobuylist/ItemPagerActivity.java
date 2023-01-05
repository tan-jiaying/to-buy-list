package com.bignerdranch.android.tobuylist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class ItemPagerActivity extends AppCompatActivity {
    private static final String EXTRA_ITEM_ID =
            "com.bignerdranch.android.tobuylist.item_id";

    private ViewPager mViewPager;
    private List<Item> mItems;

    public static Intent newIntent(Context packageContext, UUID itemId) {
        Intent intent = new Intent(packageContext, ItemPagerActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_pager);

        UUID itemId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_ITEM_ID);

        mViewPager = (ViewPager) findViewById(R.id.item_view_pager);

        mItems = ItemLab.get(this).getItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Item item = mItems.get(position);
                return ItemFragment.newInstance(item.getId());
            }

            @Override
            public int getCount() {
                return mItems.size();
            }
        });

        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId().equals(itemId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
