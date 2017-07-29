package com.goalsmaster.goalsmaster.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.data.Goal;
import com.goalsmaster.goalsmaster.events.AllowedActionsChanged;
import com.goalsmaster.goalsmaster.events.SwitchToEvent;
import com.goalsmaster.goalsmaster.events.ToastMessage;
import com.goalsmaster.goalsmaster.fragments.AddEditGoalFragment;
import com.goalsmaster.goalsmaster.fragments.AddEditTaskFragment;
import com.goalsmaster.goalsmaster.fragments.BaseFragment;
import com.goalsmaster.goalsmaster.fragments.GoalFragment;
import com.goalsmaster.goalsmaster.fragments.TaskFragment;
import com.goalsmaster.goalsmaster.other.FragmentTypes;
import com.goalsmaster.goalsmaster.other.Globals;
import com.goalsmaster.goalsmaster.utils.AppConfig;
import com.goalsmaster.goalsmaster.utils.SecurityUtils;
import com.goalsmaster.goalsmaster.utils.UserHelper;
import com.goalsmaster.goalsmaster.data.Role;
import com.goalsmaster.goalsmaster.events.CancelEvent;
import com.goalsmaster.goalsmaster.events.DeleteSelectedEvent;
import com.goalsmaster.goalsmaster.events.EditEvent;
import com.goalsmaster.goalsmaster.events.InsertEvent;
import com.goalsmaster.goalsmaster.events.SelectAllEvent;
import com.goalsmaster.goalsmaster.events.ToolbarTitleChange;
import com.goalsmaster.goalsmaster.other.FabMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_VISIBLE_ACTIONS = "VISIBLE_ACTIONS";

    @BindView(R.id.fab_multiple_actions)
    protected FloatingActionsMenu fabMenuButton;

    // Mechanism for updating the floating menu items
    private HashMap<FabMenu, FloatingActionButton> fabMap;
    private boolean[] visibleActions = new boolean[FabMenu.values().length];

    private Toolbar toolbar;

    NavigationView navigationView;
    private boolean visibleActionsRestored = false;
    private Bitmap userLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Add all menu items to the "actions" map
        FloatingActionButton fab;

        fabMap = new HashMap<FabMenu, FloatingActionButton>();

        // SELECT
        fab = new FloatingActionButton(getBaseContext());
        fab.setTitle("Select All");
        fab.setSize(SIZE_MINI);
        fab.setStrokeVisible(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SelectAllEvent(true));
                fabMenuButton.collapse();
            }
        });
        fabMenuButton.addButton(fab);
        fabMap.put(FabMenu.SELECT_ALL, fab);

        // DESELECT
        fab = new FloatingActionButton(getBaseContext());
        fab.setTitle("Deselect All");
        fab.setSize(SIZE_MINI);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SelectAllEvent(false));
                fabMenuButton.collapse();
            }
        });
        fabMenuButton.addButton(fab);
        fabMap.put(FabMenu.DESELECT_ALL, fab);

        // EDIT
        fab = new FloatingActionButton(getBaseContext());
        fab.setTitle("Edit");
        fab.setSize(SIZE_MINI);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EditEvent());
                fabMenuButton.collapse();
            }
        });
        fabMenuButton.addButton(fab);
        fabMap.put(FabMenu.EDIT, fab);

        // DELETE SELECTED
        fab = new FloatingActionButton(getBaseContext());
        fab.setTitle("Delete selected");
        fab.setSize(SIZE_MINI);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new DeleteSelectedEvent());
                fabMenuButton.collapse();
            }
        });
        fabMenuButton.addButton(fab);
        fabMap.put(FabMenu.DELETE, fab);

        // DELETE SELECTED
        fab = new FloatingActionButton(getBaseContext());
        fab.setTitle("Insert");
        fab.setSize(SIZE_MINI);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new InsertEvent());
                fabMenuButton.collapse();
            }
        });
        fabMenuButton.addButton(fab);
        fabMap.put(FabMenu.INSERT, fab);

        // CANCEL
        fab = new FloatingActionButton(getBaseContext());
        fab.setTitle("Cancel");
        fab.setSize(SIZE_MINI);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CancelEvent());
                fabMenuButton.collapse();
                on(new ToolbarTitleChange(null));
            }
        });
        fabMenuButton.addButton(fab);
        fabMap.put(FabMenu.CANCEL, fab);

        //UserPreferences.init(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            boolean notSet = true;
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(notSet) {
                    TextView title = (TextView) drawerView.findViewById(R.id.nav_title);
                    title.setText(UserHelper.getLoggedInUsername(getApplicationContext()));
                    TextView subtitle = (TextView) drawerView.findViewById(R.id.nav_subtitle);
                    subtitle.setText("Role: " + UserHelper.getLoggedInRoleName());
                    if(userLogo != null) {
                        ImageView logo = (ImageView) drawerView.findViewById(R.id.nav_logo);
                        logo.setImageBitmap(userLogo);
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                notSet = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                notSet = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        if(savedInstanceState != null) {
            boolean[] newActions = savedInstanceState.getBooleanArray(KEY_VISIBLE_ACTIONS);
            if(newActions != null) {
                visibleActions = newActions;
                visibleActionsRestored = true;
                updateAllActions();
            } else {
                visibleActionsRestored = false;
                hideAllActions();
            }
        } else {
            visibleActionsRestored = false;
            hideAllActions();
        }

        userLogo = null;

        Menu nav_Menu = navigationView.getMenu();
        String roleName = UserHelper.getLoggedInRoleName();
        /*if(Role.roleTitles[0].equals(roleName)) {
            // Simple user
            nav_Menu.findItem(R.id.nav_users).setVisible(false);
            nav_Menu.findItem(R.id.nav_task).setChecked(true);
        } else if(Role.roleTitles[1].equals(roleName)) {
            // Admin
            nav_Menu.findItem(R.id.nav_task).setVisible(false);
            nav_Menu.findItem(R.id.nav_users).setChecked(true);
        } else {*/
            nav_Menu.findItem(R.id.nav_task).setChecked(true);
        /*}*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(KEY_VISIBLE_ACTIONS, visibleActions);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
            //Intent intent = new Intent(this, SettingsActivity.class);
            //startActivityForResult(intent, REQ_CODE_SETTINGS);
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_task:
                if(!UserHelper.isLoggedUserAdmin(getApplicationContext())) {
                    on(new SwitchToEvent(FragmentTypes.Tasks));
                } else {
                    on(new ToastMessage("Sorry, only Supers can manage both users and tasks.", ToastMessage.Type.ALERT));
                }
                break;
            case R.id.nav_goals:
                if(!UserHelper.isLoggedUserAdmin(getApplicationContext())) {
                    on(new SwitchToEvent(FragmentTypes.Goals));
                } else {
                    on(new ToastMessage("Sorry, only Supers can manage both users and goals.", ToastMessage.Type.ALERT));
                }
                break;
            case R.id.nav_users:
                if(UserHelper.isLoggedUserAdmin(getApplicationContext()) || UserHelper.isLoggedUserSuper(getApplicationContext())) {
                    on(new SwitchToEvent(FragmentTypes.Users));
                } else {
                    on(new ToastMessage("Sorry, only Admins/Supers can manage users.", ToastMessage.Type.ALERT));
                }
                break;
            case R.id.nav_log_off:
                //EventBus.getDefault().post(new LogOffRequest());
                //EventBus.getDefault().post(new ToastMessage("User is not logged in", ToastMessage.Type.ALERT));
                firebaseAuth.signOut();
                AppConfig.putBoolean(getApplicationContext(), Globals.S_SMART_LOCK_STATE, false);
                recreate();
                break;
            /*case R.id.nav_settings:
                startSettingsActivity();
                break;*/
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == REQ_CODE_SETTINGS) {
            EventBus.getDefault().postSticky(new ServerDataUpdatedEvent());
        }*/
    }

    //private void startSettingsActivity() {
        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivityForResult(intent, REQ_CODE_SETTINGS);
    //}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(SwitchToEvent event) {
        FragmentManager fm = getSupportFragmentManager();
        BaseFragment fragment = null;
        switch (event.type) {
            case Tasks:
                fragment = TaskFragment.newInstance(1);
                break;
            case Goals:
                fragment = GoalFragment.newInstance(1);
                break;
            default:
                fragment = null;
                break;
        }
        if(fragment != null) {
            popAllBackStack(fm);
            fm.beginTransaction().replace(R.id.container, fragment).commit();
            return;
        }

        switch (event.type) {
            case AddTask:
                fragment = AddEditTaskFragment.newInstance(false);
                break;
            case EditTask:
                fragment = AddEditTaskFragment.newInstance(true);
                break;
            case AddGoal:
                fragment = AddEditGoalFragment.newInstance(false);
                break;
            case EditGoal:
                fragment = AddEditGoalFragment.newInstance(true);
                break;
        }

        fm.beginTransaction()
                .addToBackStack(fragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .add(R.id.container, fragment)
                .commit();
    }

    private void popAllBackStack(FragmentManager fm) {
        while(fm.getBackStackEntryCount() > 0)
            fm.popBackStackImmediate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if(userLogo == null)
            loadUserLogo();
    }

    private void loadUserLogo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String email = UserHelper.getLoggedInUsername(getApplicationContext());
                email = email.trim();
                email = email.toLowerCase(Locale.getDefault());
                String md5 = SecurityUtils.md5(email);
                final Uri uri = Uri.parse("https://www.gravatar.com/avatar/" + md5 + "?d=wavatar");
                Bitmap bmp;
                try {
                    bmp = Picasso.with(getApplicationContext()).load(uri).get();
                } catch (IOException e) {
                    bmp = null;
                    e.printStackTrace();
                }
                final Bitmap finalBmp = bmp;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      if(finalBmp != null)
                          userLogo = finalBmp;
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getFragments() == null) {
            if(UserHelper.isLoggedUserAdmin(getApplicationContext()))
                on(new SwitchToEvent(FragmentTypes.Users));
            else
                on(new SwitchToEvent(FragmentTypes.Tasks));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(ToastMessage event) {
        Crouton.showText(MainActivity.this,
                (event.message != null ? event.message : "Empty message"),
                (event.type == ToastMessage.Type.INFO ? Style.INFO : Style.ALERT),
                R.id.container);
    }

    private void hideAllActions() {
        Arrays.fill(visibleActions, false);
        for(FloatingActionButton fab: fabMap.values()) {
            fab.setVisibility(GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(AllowedActionsChanged event) {
        if(visibleActionsRestored == true) {
            visibleActionsRestored = false;
            return;
        }
        hideAllActions();
        for(FabMenu allowed: event.allowed) {
            visibleActions[allowed.ordinal()] = true;
        }
        updateAllActions();
        if(event.allowed.length == 0)
            fabMenuButton.setVisibility(GONE);
        else
            fabMenuButton.setVisibility(VISIBLE);
    }

    private void updateAllActions() {
        for(FabMenu allowed: FabMenu.values()) {
            FloatingActionButton fab = fabMap.get(allowed);
            fab.setVisibility((visibleActions[allowed.ordinal()] == true) ? View.VISIBLE : View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(ToolbarTitleChange event) {
        if(event.text != null)
            toolbar.setTitle(event.text);
        else
            toolbar.setTitle(getTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(CancelEvent event) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        String topVisibleFragmentTag = getTopmostVisibleFragmentTag();
        if(topVisibleFragmentTag != null) {
            if(topVisibleFragmentTag.equals(TaskFragment.TAG))
                navigationView.getMenu().findItem(R.id.nav_task).setChecked(true);
        }
    }

    private String getTopmostVisibleFragmentTag() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> list = fm.getFragments();
        for(int i=0; i<list.size(); i++) {
            Fragment f = list.get(i);
            if( (f instanceof TaskFragment) || (f instanceof AddEditTaskFragment))
                return TaskFragment.TAG;
        }
        return null;
    }
}
