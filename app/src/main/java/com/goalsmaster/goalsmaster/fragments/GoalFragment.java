package com.goalsmaster.goalsmaster.fragments;

import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.adapters.GoalViewAdapter;
import com.goalsmaster.goalsmaster.data.Goal;
import com.goalsmaster.goalsmaster.events.AllowedActionsChanged;
import com.goalsmaster.goalsmaster.events.CancelEvent;
import com.goalsmaster.goalsmaster.events.DateFilterChanged;
import com.goalsmaster.goalsmaster.events.DeleteSelectedEvent;
import com.goalsmaster.goalsmaster.events.EditEvent;
import com.goalsmaster.goalsmaster.events.InsertEvent;
import com.goalsmaster.goalsmaster.events.SelectAllEvent;
import com.goalsmaster.goalsmaster.events.ServerDataUpdatedEvent;
import com.goalsmaster.goalsmaster.events.SwitchStateChangedEvent;
import com.goalsmaster.goalsmaster.events.SwitchToEvent;
import com.goalsmaster.goalsmaster.events.GoalFragmentEvent;
import com.goalsmaster.goalsmaster.events.GoalFragmentItemLongClick;
import com.goalsmaster.goalsmaster.events.GoalItem;
import com.goalsmaster.goalsmaster.events.ToastMessage;
import com.goalsmaster.goalsmaster.events.ToolbarTitleChange;
import com.goalsmaster.goalsmaster.other.FabMenu;
import com.goalsmaster.goalsmaster.other.FragmentTypes;
import com.goalsmaster.goalsmaster.other.WrapContentGridLayoutManager;
import com.goalsmaster.goalsmaster.other.WrapContentLinearLayoutManager;
import com.goalsmaster.goalsmaster.rest.RestApi;
import com.goalsmaster.goalsmaster.rest.GoalApi;
import com.goalsmaster.goalsmaster.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class GoalFragment extends BaseFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public static final String TAG = GoalFragment.class.getSimpleName();

    RecyclerView recyclerView;
    GoalViewAdapter adapter;

    @BindView(R.id.dateFrom)
    TextView dateFrom;
    @BindView(R.id.dateTo)
    TextView dateTo;

    @OnClick({R.id.dateFrom, R.id.dateTo})
    public void onDateFilterClick(final View sender) {
        try {
            String txt = ((TextView) sender).getText().toString();
            String[] parts = txt.split("-");
            int day = Integer.valueOf(parts[0]);
            int month = Integer.valueOf(parts[1])-1;
            int year = Integer.valueOf(parts[2]);
            DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    ((TextView) sender).setText(String.format("%02d-%02d-%04d", dayOfMonth, month+1, year));
                    EventBus.getDefault().post(new DateFilterChanged());
                }
            }, year, month, day);
            datePickerDialog.setTitle("On which date is this goal due?");
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GoalFragment() {
    }

    @Override
    public FragmentTypes getFragmentType() {
        return null;
    }

    @SuppressWarnings("unused")
    public static GoalFragment newInstance(int columnCount) {
        GoalFragment fragment = new GoalFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        allowedOperations = new FabMenu[]{FabMenu.EDIT, FabMenu.INSERT};

        setHasOptionsMenu(true);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_list, container, false);
        View listView = view.findViewById(R.id.list);
        // Set the adapter
        if (listView instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) listView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new WrapContentGridLayoutManager(context, mColumnCount));
            }
            recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(2));

            adapter = new GoalViewAdapter(getContext());
            if(savedInstanceState != null) {
                adapter.restoreState(savedInstanceState);
            }
            recyclerView.setAdapter(adapter);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        dateFrom.setText(StringUtils.dateToString(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 60);
        dateTo.setText(StringUtils.dateToString(calendar.getTime()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().post(
                new AllowedActionsChanged(
                        new FabMenu[]{
                                FabMenu.INSERT,
                                FabMenu.EDIT
                        }
                )
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(final GoalFragmentEvent event) {
        //Crouton.showText(getActivity(), event.goal.getTitle(), Style.INFO, R.id.container);

        String notes = null; //event.goal.getNotes();
        if(notes != null && notes.length() > 0) {
            // Add display safety and beautify the string (wrap lines)
        } else {
            notes = "Goal";
        }

        final long id = event.goal.getId();
        Snackbar.make(getView(), notes, Snackbar.LENGTH_LONG)
                .setAction("Edit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //EventBus.getDefault().post(new ToastMessage("Edit notes for Goal with id = " + id));
                        EventBus.getDefault().post(new GoalFragmentItemLongClick(event.goal));
                    }
                }).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(GoalFragmentItemLongClick event) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseFragment.S_ITEM_ID, event.goal.getId());
        EventBus.getDefault().post(new SwitchToEvent(FragmentTypes.EditGoal));
        EventBus.getDefault().postSticky(new GoalItem(event.goal));
    }

    @Subscribe
    public void on(EditEvent event) {
        adapter.setEditMode(!adapter.getEditMode());
        EventBus.getDefault().post(
                new AllowedActionsChanged(
                        new FabMenu[]{
                                FabMenu.SELECT_ALL,
                                FabMenu.CANCEL
                        }
                )
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(SwitchStateChangedEvent event) {
        FabMenu[] fabMenu;
        if (event.state) {
            fabMenu = new FabMenu[]{
                    FabMenu.SELECT_ALL,
                    FabMenu.DESELECT_ALL,
                    FabMenu.DELETE,
                    FabMenu.CANCEL
            };
        } else {
            fabMenu = new FabMenu[]{
                    FabMenu.SELECT_ALL,
                    FabMenu.CANCEL
            };
        }
        EventBus.getDefault().post(new AllowedActionsChanged(fabMenu));
        int k = adapter.getSelectedCount();
        EventBus.getDefault().post(new ToolbarTitleChange((k > 0 ? (k + " items selected") : null)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(SelectAllEvent event) {
        int k = adapter.selectAll(event.state);
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        FabMenu[] fabMenu;
        if (event.state) {
            fabMenu = new FabMenu[]{
                    FabMenu.DESELECT_ALL,
                    FabMenu.DELETE,
                    FabMenu.CANCEL
            };
            EventBus.getDefault().post(new ToolbarTitleChange(k + " items selected"));
        } else {
            fabMenu = new FabMenu[]{
                    FabMenu.SELECT_ALL,
                    FabMenu.CANCEL
            };
            EventBus.getDefault().post(new ToolbarTitleChange(null));
        }
        EventBus.getDefault().post(new AllowedActionsChanged(fabMenu));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(CancelEvent event) {
        adapter.setEditMode(false);
        EventBus.getDefault().post(new AllowedActionsChanged(
                new FabMenu[]{
                        FabMenu.INSERT,
                        FabMenu.EDIT
                }));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void on(DeleteSelectedEvent event) {

        List<Goal> selectedArray = adapter.getSelected();

        if(selectedArray.size() == 0) {
            EventBus.getDefault().post(new ToastMessage("No items selected."));
            return;
        }

        final List<Integer> positions = new ArrayList<>();

        GoalApi api = RestApi.getGoalApi(getContext());
        int succeeded = 0, failed = 0;
        for (Goal selected : selectedArray) {
            Call<String> call = api.deleteGoalById(selected.getId());
            Response<String> response = null;
            try {
                response = call.execute();
                positions.add(adapter.getItemPosition(selected));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (final Integer position : positions) {
                    adapter.remove(position);
                }
                adapter.notifyDataSetChanged();
            }
        });

        String msg = "";
        if (failed == 0) {
            msg = "All items deleted successfully";
            EventBus.getDefault().post(new SelectAllEvent(false));
        } else if (succeeded == 0) {
            msg = "Could not delete any item(s)";
        } else {
            msg = "Deleted " + succeeded + ", failed " + failed;
        }

        EventBus.getDefault().post(new ToastMessage(msg));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void on(InsertEvent event) {
        EventBus.getDefault().post(new SwitchToEvent(FragmentTypes.AddGoal));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void on(ServerDataUpdatedEvent event) {
        adapter.queryData();
        while(null != EventBus.getDefault().removeStickyEvent(ServerDataUpdatedEvent.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(DateFilterChanged event) {
        Date from = StringUtils.dateFromCharSequence(dateFrom.getText());
        Date to = StringUtils.dateFromCharSequence(dateTo.getText());
        adapter.setDateFilters(from, to);
        adapter.queryData();
    }

    public List<Goal> getGoalList() {
        return adapter.getList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.goal_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_calendar) {
            long startMillis = System.currentTimeMillis();
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, startMillis);
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
