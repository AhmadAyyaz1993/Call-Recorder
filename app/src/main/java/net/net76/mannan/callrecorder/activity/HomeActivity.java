package net.net76.mannan.callrecorder.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.net76.mannan.callrecorder.R;
import net.net76.mannan.callrecorder.activity.fragments.IncomingFragment;
import net.net76.mannan.callrecorder.activity.fragments.OutGoingFragment;
import net.net76.mannan.callrecorder.adapter.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tutorialTitle;
    TabLayout tabLayout;
    AppBarLayout appBarLayout;
    private ViewPager mViewPager;
    int defaultCurrentTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.app_title);

//        tutorialTitle = (TextView) findViewById(R.id.tutorial_name_tv);
//        tutorialTitle.setText(R.string.app_title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewPager = (ViewPager) findViewById(R.id.containerViewPager);
        setUpViewPager(mViewPager);

        initIntances();
        setUpTabLayout();

        int page = getIntent().getIntExtra("ARG_PAGE", defaultCurrentTabIndex);
        mViewPager.setCurrentItem(page, true);
//        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void initIntances() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    }

    private void setUpTabLayout() {
//        tabLayout.setSelectedTabIndicatorColor(R.color.white);
        tabLayout.setupWithViewPager(mViewPager);
        List<Integer> tabIconList = new ArrayList<Integer>();
        tabIconList.add(android.R.drawable.sym_call_incoming);
        tabIconList.add(android.R.drawable.sym_call_outgoing);
        for (int i = 0; i < tabIconList.size(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIconList.get(i));
        }
    }

    private void setUpViewPager(ViewPager mViewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this.getSupportFragmentManager());
        List<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(new IncomingFragment());
        fragmentArrayList.add(new OutGoingFragment());
        List<String> tabTitleStringList = new ArrayList<>();
        tabTitleStringList.add("Incoming Calls");
        tabTitleStringList.add("Outgoing Calls");
        for (int i = 0; i < tabTitleStringList.size(); i++) {
            adapter.addFrag(fragmentArrayList.get(i), tabTitleStringList.get(i));
        }
        mViewPager.setAdapter(adapter);
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
//        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
