package com.haibeey.android.medmanager;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.SearchEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.haibeey.android.medmanager.adapters.AdapterForMainActivityTabLayout;
import com.haibeey.android.medmanager.adapters.AdapterForSearchItems;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int RC_SIGN_IN = 1;
    private FirebaseUser user;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerView;//recyclerView for Search
    private boolean recyclerViewForSearchISVisible=false;


    private MenuItemCompat.OnActionExpandListener SearchListener=new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            onBackPressed();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setting up the require fire base classes
        mFirebaseAuth=FirebaseAuth.getInstance();
        //sign in user if not signed in
        signUpIfNotSignIn();
        //configures the view pager
        setUpViewPager();
        //setting navigation Drawer
        setUpNavigationBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(recyclerView!=null && recyclerViewForSearchISVisible){
                recyclerView.setVisibility(View.GONE);
                recyclerViewForSearchISVisible=false;
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) item.getActionView();
        MenuItemCompat.setOnActionExpandListener( item,SearchListener);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }else if(id==R.id.logout){
            mFirebaseAuth.signOut();
            finish();
        }else if (id==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_search:{
                onSearchRequested();
                break;
            }
            case R.id.nav_share:{
                share();
                break;
            }
            case R.id.nav_profile:{
                startProfileActivity();
               break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setSearchUp(String Query) {
        AdapterForSearchItems adapterForSearchItems=new AdapterForSearchItems(this,Query);
        recyclerView=(RecyclerView)findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewForSearchISVisible=true;
        recyclerView.setAdapter(adapterForSearchItems);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void setUpViewPager(){
        ViewPager viewPager=(ViewPager)findViewById(R.id.home_viewpager);
        AdapterForMainActivityTabLayout mainActivityTabLayout=new AdapterForMainActivityTabLayout(getSupportFragmentManager());
        viewPager.setAdapter(mainActivityTabLayout);
    }


    private void setUpNavigationBar(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void signUpIfNotSignIn(){
        user=mFirebaseAuth.getCurrentUser();
        if(user==null){
            if(Utils.checkConnectivity(this)){
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
                        startActivityForResult(
                            AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }else{
                Toast.makeText(this,getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private void startProfileActivity(){
        Intent intent=new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    private void share(){

        Intent I=new Intent(Intent.ACTION_SEND);
        I.setType("text/plain");
        I.putExtra(Intent.EXTRA_TEXT,"With Med Manager you will get reminded for your medication\nget it now !!");
        startActivity(I);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            setSearchUp(query);

        }
    }
}
