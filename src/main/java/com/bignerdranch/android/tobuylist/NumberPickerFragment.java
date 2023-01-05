package com.bignerdranch.android.tobuylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerFragment extends DialogFragment {

    public static final String EXTRA_QUANTITY =
            "com.bignerdranch.android.tobuylist.quantity";

    private static final String ARG_QUANTITY = "quantity";

    private NumberPicker mNumberPicker;

    public static NumberPickerFragment newInstance(Number number) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUANTITY, number);

        NumberPickerFragment fragment = new NumberPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int quantity = (int) getArguments().getSerializable(ARG_QUANTITY);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_quantity, null);

        mNumberPicker = (NumberPicker) v.findViewById(R.id.dialog_number_picker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(50);

        mNumberPicker.setValue(quantity);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.quantity_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK, mNumberPicker.getValue());
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, int quantity) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_QUANTITY, quantity);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
