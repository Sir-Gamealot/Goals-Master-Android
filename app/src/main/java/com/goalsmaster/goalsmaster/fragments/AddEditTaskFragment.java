package com.goalsmaster.goalsmaster.fragments;


import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.goalsmaster.goalsmaster.data.Goals;
import com.goalsmaster.goalsmaster.data.Tasks;
import com.goalsmaster.goalsmaster.events.ServerDataUpdatedEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.data.Id;
import com.goalsmaster.goalsmaster.data.Task;
import com.goalsmaster.goalsmaster.events.CancelEvent;
import com.goalsmaster.goalsmaster.events.TaskItem;
import com.goalsmaster.goalsmaster.events.ToastMessage;
import com.goalsmaster.goalsmaster.other.FabMenu;
import com.goalsmaster.goalsmaster.other.FragmentTypes;
import com.goalsmaster.goalsmaster.rest.RestApi;
import com.goalsmaster.goalsmaster.rest.TaskApi;
import com.goalsmaster.goalsmaster.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEditTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditTaskFragment extends BaseFragment {
    private static final String ARG_IS_EDITABLE = "IS_EDITABLE";
    private static final String ARG_ITEM_ID = "ARG_ITEM_ID";
    public static final int REQ_CODE_MAP_LOCATION = 2000;

    public static final String TAG = AddEditTaskFragment.class.getSimpleName();

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

    private String userId;
    private String title;
    private String description;
    private Date date;
    private long date_year;
    private long date_month;
    private long date_day;
    private long durationInSeconds;
    private double latitude;
    private double longitude;

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
        datePickerDialog.setTitle("On which date is this task due?");
        datePickerDialog.show();
    }

    @OnClick(R.id.ivPhoto)
    public void onMapViewClick(View sender) {
        EventBus.getDefault().post(new ToastMessage("Not implemented yet!"));
    }

    @OnClick(R.id.btnAction)
    public void onActionClick(View sender) {
        if(isEditable == false) {
            // Insert
            if (dataValidated()) {
                // Create new task
                DatabaseReference tasksRef = Tasks.getFirebaseNodeRef(getContext());
                DatabaseReference taskRef = tasksRef.push();
                final Task task = new Task(taskRef.getKey(), userId, getTitle(), getDescription(), getDate().getTime(), getDuration(), getLatitude(), getLongitude());
                taskRef.setValue(task)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                EventBus.getDefault().post(new ToastMessage("Task inserted : \n" + task.toString()));
                                EventBus.getDefault().post(new CancelEvent());
                                EventBus.getDefault().post(new ServerDataUpdatedEvent());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                EventBus.getDefault().post(new ToastMessage("Error: could not insert task."));
                            }
                        });
            }
        } else {
            // Update
            if (dataValidated()) {
                // Edit existing task
                DatabaseReference tasksRef = Tasks.getFirebaseNodeRef(getContext());
                DatabaseReference taskRef = tasksRef.child(getItemId());
                final Task task = new Task(getItemId(), getUserId(), getTitle(), getDescription(), getDate().getTime(), getDuration(), getLatitude(), getLongitude());
                taskRef.setValue(task)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                EventBus.getDefault().post(new ToastMessage("Task edited : \n" + task.toString()));
                                EventBus.getDefault().post(new CancelEvent());
                                EventBus.getDefault().post(new ServerDataUpdatedEvent());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                EventBus.getDefault().post(new ToastMessage("Error: could not edit task."));
                            }
                        });
            }
        }
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

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public FragmentTypes getFragmentType() {
        return FragmentTypes.AddTask;
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param isEditable Turns the fragment into edit mode if set to true
     * @return A new instance of fragment AddEditTaskFragment.
     */
    public static AddEditTaskFragment newInstance(Boolean isEditable) {
        AddEditTaskFragment fragment = new AddEditTaskFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_EDITABLE, isEditable);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param isEditable Turns the fragment into edit mode if set to true
     * @param itemId Tells the fragment to fetch Task with id = itemId for editing
     * @return A new instance of fragment AddEditTaskFragment.
     */
    public static BaseFragment newInstance(boolean isEditable, long itemId) {
        AddEditTaskFragment fragment = new AddEditTaskFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_edit_task, container, false);
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

    public long getDuration() {
        return 3600; // Typically 1 hour
    }

    public void setDuration(long duration) {
        this.durationInSeconds = duration;
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void on(TaskItem event) {
        Task task = event.getTask();
        setItemId(task.getId());
        setUserId(task.getUserId());
        setTitle(task.getTitle());
        setDescription(task.getDescription());
        setDate(new Date(task.getTimestamp()));
        setDuration(task.getDuration());
        setLatitude(task.getLatitude());
        setLongitude(task.getLongitude());
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
                Picasso.with(getContext()).load(path).error(R.drawable.ic_map).into(mapView);
            }
        }*/
    }
}
