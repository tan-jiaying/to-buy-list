package com.bignerdranch.android.tobuylist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ItemFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_QUANTITY = "DialogQuantity";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_QUANTITY = 3;
    private static final int REQUEST_TIME = 4;

    private Item mItem;
    private File mPhotoFile;
    private EditText mNameField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mQuantityButton;
    private CheckBox mBoughtCheckBox;
    private Button mHelperButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mDeleteButton;

    public static ItemFragment newInstance(UUID itemId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_ID, itemId);

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);
        mItem = ItemLab.get(getActivity()).getItem(itemId);
        mPhotoFile = ItemLab.get(getActivity()).getPhotoFile(mItem);
    }

    @Override
    public void onPause() {
        super.onPause();

        ItemLab.get(getActivity())
                .updateItem(mItem); // pushing updates
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item, container, false);

        mNameField = (EditText) v.findViewById(R.id.item_name);
        mNameField.setText(mItem.getName());
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mDateButton = (Button) v.findViewById(R.id.due_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mItem.getDate());
                dialog.setTargetFragment(ItemFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mItem.getTime());
                dialog.setTargetFragment(ItemFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mQuantityButton = (Button) v.findViewById(R.id.item_quantity);
        updateQuantity();
        mQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                NumberPickerFragment dialog = NumberPickerFragment
                        .newInstance(mItem.getQuantity());
                dialog.setTargetFragment(ItemFragment.this, REQUEST_QUANTITY);
                dialog.show(manager, DIALOG_QUANTITY);
            }
        });

        mBoughtCheckBox = (CheckBox)v.findViewById(R.id.item_bought);
        mBoughtCheckBox.setChecked(mItem.isBought());
        mBoughtCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isBought) {
                mItem.setBought(isBought);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.item_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND); // implicit intent to send item report
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getItemReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.item_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mHelperButton = (Button) v.findViewById(R.id.item_helper);
        mHelperButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mItem.getHelper() != null) {
            mHelperButton.setText(mItem.getHelper());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mHelperButton.setEnabled(false); // contacts app not found
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.item_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // implicit intent to ask for a new picture to be taken

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto); // disabled if there is no camera app or if no location to save photo

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.tobuylist.fileprovider",
                        mPhotoFile); // translates local filepath into a Uri the camera app can see
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                // to determine whether a camera app is available
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY); // query for activities that respond to camera implicit intent

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // grant a write permission
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.item_photo);
        updatePhotoView();

        mDeleteButton = (Button) v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemLab.get(getActivity()).deleteItem(mItem);
                Toast.makeText(getActivity(), "Item has been deleted!", Toast.LENGTH_SHORT).show();

                // navigate back to ItemListActivity
                Intent intent = new Intent(getActivity(), ItemListActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mItem.setDate(date);
            updateDate();

        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData(); // points at single contact picked by user

            // specify which fields you want your query to return values for
            // create a query that asks for all display names of the contacts in the returned data
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

            // perform your query - contactUri is like a "where" clause here
            // query contacts database and get a Cursor object
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                // double check that there are results
                if (c.getCount() == 0) {
                    return;
                }

                // pull out the first column of the first row of data - i.e. your helper's name
                c.moveToFirst(); // move to first item as cursor only contians one item
                String helper = c.getString(0); // name of helper
                mItem.setHelper(helper);
                mHelperButton.setText(helper);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.tobuylist.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // revoke permission when camera is done writing to file

            updatePhotoView();
        } else if (requestCode == REQUEST_QUANTITY) {
            int quantity = (int) data
                    .getSerializableExtra(NumberPickerFragment.EXTRA_QUANTITY);
            mItem.setQuantity(quantity);
            updateQuantity();
        } else if (requestCode == REQUEST_TIME) {
            Date time = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mItem.setTime(time);
            updateTime();
        }
    }

    private void updateTime() {
        mTimeButton.setText(DateFormat.format("HH:mm", mItem.getTime()).toString());
    }

    private void updateQuantity() {
        mQuantityButton.setText(String.valueOf(mItem.getQuantity()));
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEE, MMM dd", mItem.getDate()).toString());
    }

    private String getItemReport() {
        String boughtString = null;
        if (mItem.isBought()) {
            boughtString = getString(R.string.item_report_bought);
        } else {
            boughtString = getString(R.string.item_report_not_bought);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mItem.getDate()).toString();

        String helper = mItem.getHelper();
        if (helper == null) {
            helper = getString(R.string.item_report_no_helper);
        } else {
            helper = getString(R.string.item_report_helper, helper);
        }

        String report = getString(R.string.item_report, mItem.getName(), dateString, boughtString, helper);

        return report;
    }

    private void updatePhotoView() { // load bitmap into ImageView
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
