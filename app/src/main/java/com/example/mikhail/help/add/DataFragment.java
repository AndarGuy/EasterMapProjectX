package com.example.mikhail.help.add;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.mikhail.help.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DataFragment extends Fragment {

    public DataFragment() {

    }

    private static final String TAG = "DataFragment";

    public static final String
            KEY_NAME_MAX_LEN = "len_max",
            KEY_NAME_MIN_LEN = "len_min";

    static final int RESULT_OK = -1;
    static final int REQUEST_TAKE_PHOTO = 1;

    static final byte NAME_MAX_LENGTH = 25;
    static final byte NAME_MIN_LENGTH = 4;

    byte nameMaxLength, nameMinLength;

    ImageView mImageView;
    String mCurrentPhotoPath;

    EditText description;
    TextView name;
    ViewSwitcher viewSwitcher;

    String nameString;

    private void getBundles() {
        nameMaxLength = getArguments().getByte(KEY_NAME_MAX_LEN);
        nameMinLength = getArguments().getByte(KEY_NAME_MIN_LEN);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LOCATION_IMAGE_" + timeStamp;
        File storageDir = this.getActivity().getObbDir();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        if (mCurrentPhotoPath != null) {
            File currentPhotoFile = new File(mCurrentPhotoPath);
            currentPhotoFile.delete();
        }
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.d(TAG, "createImageFile: " + mCurrentPhotoPath);

        return image;
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "setPic: " + mImageView.getMeasuredWidth() + " " + mImageView.getWidth());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: " + ex.getLocalizedMessage());
            }
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } else {
                Log.d(TAG, "dispatchTakePictureIntent: file is null");
            }
        }
    }

    private void setPic() {
        Log.d(TAG, "setPic: " + mImageView.getMeasuredWidth() + " " + mImageView.getWidth());
        BitmapFactory.decodeFile(mCurrentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mImageView.setImageBitmap(bitmap);
        mListener.OnSendImage(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private boolean isKnownSymbols(String s) {
        for (char c : s.toCharArray()) {
            int i = (int) c;
            if (!((i >= 48 && i <= 57) || (i >= 65 && i <= 90) || (i >= 97 && i <= 122) || (i >= 1040 && i <= 1103) || i == 32)) {
                return false;
            }
        }
        return true;
    }

    private void elementsLoad(View v) {
        mImageView = v.findViewById(R.id.myImage);
        description = v.findViewById(R.id.descriptionInput);
        name = v.findViewById(R.id.placeNameText);
        viewSwitcher = v.findViewById(R.id.placeViewSwitcher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data, container, false);

        getBundles();

        elementsLoad(v);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        description.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                mListener.OnSendDescription(description.getText().toString());
                return false;
            }
        });

        viewSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View element = viewSwitcher.getNextView();
                if (element instanceof EditText) {
                    viewSwitcher.showNext();
                    viewSwitcher.setClickable(false);
                    element.requestFocus();
                    ((EditText) element).selectAll();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(element, 0);
                    element.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (!b) {
                                if (((EditText) element).getText().length() >= NAME_MAX_LENGTH || ((EditText) element).getText().length() <= NAME_MIN_LENGTH) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.need_from_to_symbols).replace("%from%", NAME_MIN_LENGTH + "").replace("%to%", NAME_MAX_LENGTH + ""), Toast.LENGTH_SHORT).show();
                                } else if (!isKnownSymbols(((EditText) element).getText().toString())) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.unknown_symbols), Toast.LENGTH_SHORT).show();
                                } else {
                                    name.setText(((EditText) element).getText().toString());
                                    nameString = ((EditText) element).getText().toString();
                                    mListener.OnSendName(nameString);
                                }
                                viewSwitcher.setClickable(true);
                                viewSwitcher.showNext();
                            }
                        }
                    });

                }
            }
        });
        return v;
    }

    public interface OnDataFragmentDataListener {
        void OnSendName(String name);
        void OnSendDescription(String description);
        void OnSendImage(Bitmap image);
    }

    private OnDataFragmentDataListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDataFragmentDataListener) {
            mListener = (OnDataFragmentDataListener) context;
        }
    }

}
