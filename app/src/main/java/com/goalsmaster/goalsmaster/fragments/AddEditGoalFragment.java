package com.goalsmaster.goalsmaster.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.data.Goal;
import com.goalsmaster.goalsmaster.events.CancelEvent;
import com.goalsmaster.goalsmaster.events.GoalItem;
import com.goalsmaster.goalsmaster.events.ServerDataUpdatedEvent;
import com.goalsmaster.goalsmaster.events.ToastMessage;
import com.goalsmaster.goalsmaster.other.FabMenu;
import com.goalsmaster.goalsmaster.other.FragmentTypes;
import com.goalsmaster.goalsmaster.utils.ViewUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEditGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditGoalFragment extends BaseFragment {
    private static final String ARG_IS_EDITABLE = "IS_EDITABLE";
    private static final String ARG_ITEM_ID = "ARG_ITEM_ID";

    public static final String TAG = AddEditGoalFragment.class.getSimpleName();

    private Boolean isEditable;
    private String itemId;

    @BindView(R.id.etTitle)
    public EditText etTitle;

    @BindView(R.id.etDescription)
    public EditText etDescription;

    @BindView(R.id.etDate)
    public TextView etDate;

    @BindView(R.id.btnAction)
    public Button btnAction;

    @BindView(R.id.ivPhoto)
    public ImageView photoView;

    @BindView(R.id.addEditMain)
    public ConstraintLayout layout;

    @BindView(R.id.spinPriority)
    public Spinner spinPriority;

    private String userId;
    private String title;
    private String description;
    private Date date;
    private long date_year;
    private long date_month;
    private long date_day;
    private String priority = "Absolutely";
    private String photoId = "NONE";

    private View.OnTouchListener touchHideKeyboardListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ViewUtils.hideKeyboardFromView(v);
            return false;
        }
    };


    @OnClick(R.id.etDate)
    public void onDateClick(View sender) {
        DatePickerDialog datePickerDialog;
        if(isEditable == false) {
            Date date = new Date();
            date_year = date.getYear() + 1900;
            date_month = date.getMonth();
            date_day = date.getDate();
        }
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date_year = year;
                date_month = month;
                date_day = dayOfMonth;
                setDate(new Date(year-1900, month, dayOfMonth, 0, 0));
            }
        }, (int)date_year, (int)date_month, (int)date_day);
        datePickerDialog.setTitle("On which date is this goal due?");
        datePickerDialog.show();
    }

    @OnClick(R.id.ivPhoto)
    public void onMapViewClick(View sender) {
        EventBus.getDefault().post(new ToastMessage("Not implemented yet!"));
    }

    @OnClick(R.id.btnAction)
    public void onActionClick(View sender) {
        if (isEditable == false) {
            // Insert
            if (dataValidated()) {
                // Create new goal
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = (user != null ? user.getUid() : "error_null_user");
                DatabaseReference goalsRef = database.getReference("Goals");
                DatabaseReference goalRef = goalsRef.push();
                final Goal goal = new Goal(goalRef.getKey(), userId, getTitle(), getDescription(), getDate(), getPriority(), getPhotoId());
                goalRef.setValue(goal)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                EventBus.getDefault().post(new ToastMessage("Goal inserted : \n" + goal.toString()));
                                EventBus.getDefault().post(new CancelEvent());
                                EventBus.getDefault().post(new ServerDataUpdatedEvent());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                EventBus.getDefault().post(new ToastMessage("Error: could not insert goal."));
                            }
                        });
                /*GoalApi api = RestApi.getGoalApi(getContext());
                Call<Id> call = api.createGoal(
                        getUserId(),
                        getTitle(),
                        getDescription(),
                        getDate(),
                        getPriority(),
                        getPhotoId()
                );
                try {
                    call.enqueue(new Callback<Id>() {
                        @Override
                        public void onResponse(Call<Id> call, Response<Id> response) {
                            if (response.code() == 201) {
                                Id id = response.body();
                                saveMapToCache(id.getId());
                                String msg = id.toString();
                                EventBus.getDefault().post(new ToastMessage(response.message() + " : \n" + msg));
                                EventBus.getDefault().post(new CancelEvent());
                                EventBus.getDefault().post(new ServerDataUpdatedEvent());
                            } else {
                                EventBus.getDefault().post(new ToastMessage("Error: could not insert goal."));
                            }
                        }

                        @Override
                        public void onFailure(Call<Id> call, Throwable t) {
                            EventBus.getDefault().post(new ToastMessage(t.getMessage()));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        } else {
            // Update
            if (dataValidated()) {
                // Create new goal
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = (user != null ? user.getUid() : "error_null_user");
                DatabaseReference goalsRef = database.getReference("Goals");
                DatabaseReference goalRef = goalsRef.push();
                goalRef.setValue(new Goal(getItemId(), getUserId(), getTitle(), getDescription(), getDate(), getPriority(), getPhotoId()));
                /*GoalApi api = RestApi.getGoalApi(getContext());
                Date date = getDate();
                date.setHours(1);
                date.setMinutes(1);
                date.setSeconds(1);
                Call<String> call = api.updateGoal(
                        getItemId(),
                        getUserId(),
                        getTitle(),
                        getDescription(),
                        date,
                        getPriority(),
                        getPhotoId()
                );
                try {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.code() == 200) {
                                saveMapToCache(getItemId());
                                String msg = String.valueOf(response.body());
                                EventBus.getDefault().post(new ToastMessage(response.message() + " : \n" + msg));
                                EventBus.getDefault().post(new CancelEvent());
                                EventBus.getDefault().post(new ServerDataUpdatedEvent());
                            } else {
                                EventBus.getDefault().post(new ToastMessage("Error: could not update goal."));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            EventBus.getDefault().post(new ToastMessage(t.getMessage()));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    @OnItemSelected(R.id.spinPriority)
    public void onSpinItemSelected(Spinner spinner, int position) {
        priority = (String)spinner.getItemAtPosition(position);
    }

    private void saveMapToCache(long id) {
        String pathSrc = getContext().getCacheDir().getAbsolutePath() + File.separator + "cache.jpg";
        String pathDest = getContext().getCacheDir().getAbsolutePath() + File.separator + String.valueOf(id) + ".jpg";
        try {
            File f = new File(pathSrc);
            f.renameTo(new File(pathDest));
            f = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean dataValidated() {
        if (getTitle() == null || getTitle().length() <= 3) {
            etTitle.setError("You need a title");
            etTitle.requestFocus();
            return false;
        }
        if (getDescription() == null || getDescription().length() <= 3) {
            etDescription.setError("You need a description");
            etDescription.requestFocus();
            return false;
        }
        if(getDate() == null || getDate().before(new Date(1, 0, 0, 0, 0, 0))) {
            etDate.setError("Date is incorrect");
            etDate.requestFocus();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnCancel)
    public void onCancelClick(View sender) {
        EventBus.getDefault().post(new CancelEvent());
    }

    public AddEditGoalFragment() {
        // Required empty public constructor
    }

    @Override
    public FragmentTypes getFragmentType() {
        return FragmentTypes.AddGoal;
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param isEditable Turns the fragment into edit mode if set to true
     * @return A new instance of fragment AddEditGoalFragment.
     */
    public static AddEditGoalFragment newInstance(Boolean isEditable) {
        AddEditGoalFragment fragment = new AddEditGoalFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_EDITABLE, isEditable);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param isEditable Turns the fragment into edit mode if set to true
     * @param itemId Tells the fragment to fetch Goal with id = itemId for editing
     * @return A new instance of fragment AddEditGoalFragment.
     */
    public static BaseFragment newInstance(boolean isEditable, long itemId) {
        AddEditGoalFragment fragment = new AddEditGoalFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_EDITABLE, isEditable);
        args.putLong(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEditable = getArguments().getBoolean(ARG_IS_EDITABLE);
        }

        allowedOperations = new FabMenu[] {};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_goal, container, false);
        ButterKnife.bind(this, view);
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                title = null;
                try {
                    title = s.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                description = null;
                try {
                    description = s.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(description != null) {
                    if(description.length() > 512) {
                        etDescription.setError("Too long, max 512 characters.");
                        etDescription.requestFocus();
                    }
                }
            }
        });

        etDate.setOnTouchListener(touchHideKeyboardListener);
        layout.setOnTouchListener(touchHideKeyboardListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        btnAction.setText( (isEditable ? "Update" : "Insert") );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        etTitle.setText(title);
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        etDescription.setText(description);
        this.description = description;
    }

    public Date getDate() {
        /*try {
            String dateText = etDate.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateText);
            return date;
        } catch (Exception e){
            return new Date(0, 0, 0, 0, 0, 0);
        }*/
        return date;
    }

    public void setDate(Date date) {
        try {
            this.date = date;
            this.date_year = date.getYear() + 1900;
            this.date_month = date.getMonth();
            this.date_day = date.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateText = sdf.format(date);
            etDate.setText(dateText);
        } catch (Exception e) {
            this.date = new Date(0, 0, 1);
            etDate.setText("1900-01-01");
            this.date_year = 1900;
            this.date_month = 0;
            this.date_day = 0;
        }
    }

    public String getUserId() {
        if(isEditable)
            return userId;
        else
            return "null";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public String getItemId() {
        return itemId;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void on(GoalItem event) {
        Goal goal = event.getGoal();
        setItemId(goal.getId());
        setUserId(goal.getUserId());
        setTitle(goal.getTitle());
        setDescription(goal.getDescription());
        setDate(goal.getDate());
        setPriority(goal.getPriority());
        setPhotoId(goal.getPhotoId());
        String path = "file:///" + getContext().getCacheDir().getAbsolutePath() + File.separator + getItemId() + ".jpg";
        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext()).load(path).error(R.drawable.ic_location_add).into(photoView);
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == REQ_CODE_MAP_LOCATION && resultCode == RESULT_OK) {
            if(data == null) {
                Toast.makeText(getContext(), "Data is null", Toast.LENGTH_SHORT).show();
            } else {
                byte[] byteArray = data.getByteArrayExtra("snapshot");
                String path = getContext().getCacheDir().getAbsolutePath() + File.separator + "cache.png";
                File f = null;
                BufferedOutputStream bos = null;
                try {
                    f = new File(path);
                    bos = new BufferedOutputStream(new FileOutputStream(f));
                    bos.write(byteArray);
                    bos.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(bos != null) {
                        try {
                            bos.close();
                        } catch (Exception e){
                        }
                        bos = null;
                    }
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                if(bmp != null) photoView.setImageBitmap(bmp);
                latitude = (double) data.getDoubleExtra("latitude", 0);
                longitude = (double) data.getDoubleExtra("longitude", 0);
                Picasso.with(getContext()).setIndicatorsEnabled(true);
                String path = "file:///" + getContext().getCacheDir().getAbsolutePath() + File.separator + "cache.jpg";
                Picasso.with(getContext()).load(path).error(R.drawable.ic_map).into(photoView);
            }
        }*/
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        for(int i=0; i<spinPriority.getCount(); i++) {
            String s = (String) spinPriority.getItemAtPosition(i);
            if(priority.equalsIgnoreCase(s)) {
                spinPriority.setSelection(i);
                break;
            }
        }
        this.priority = priority;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
}
