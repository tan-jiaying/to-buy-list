package com.bignerdranch.android.tobuylist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class ItemActivity extends SingleFragmentActivity {

    private static final String EXTRA_ITEM_ID = "com.bignerdranch.android.tobuylist.item_id";

    public static Intent newIntent(Context packageContext, UUID itemId) {
        Intent intent = new Intent(packageContext, ItemActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID itemId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_ITEM_ID);
        return ItemFragment.newInstance(itemId);
    }
}
