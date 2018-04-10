package com.haibeey.android.medmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haibeey.android.medmanager.model.User;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseUser user;
    private final static  int RC_SIGN_IN=1;
    private final static int RC_PHOTO_PICKER=2;
    private User UserProfile=new User();


    View.OnClickListener SaveDataButtonListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText editTextUsername=(EditText)findViewById(R.id.username_edit_text);
            EditText editTextFirstName=(EditText)findViewById(R.id.first_name_edit_text);
            EditText editTextLastName=(EditText)findViewById(R.id.last_name_edit_text);


            UserProfile.setFirstName(editTextFirstName.getText().toString());
            UserProfile.setUsername(editTextUsername.getText().toString());
            UserProfile.setLastName(editTextLastName.getText().toString());
            UserProfile.setEmail(user.getEmail());
            //update the user profile
            UpdateProfile(UserProfile);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //declare the fire base Methods
        DeclareFireBase();
        //fill up the user
        fillUser();
        //Set button for Listener
        Button button=(Button)findViewById(R.id.add_some_data);
        button.setOnClickListener(SaveDataButtonListener);
        //show user profile
        fillUpDataItems();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillUpDataItems(){

        EditText editTextUsername=(EditText)findViewById(R.id.username_edit_text);
        EditText editTextFirstName=(EditText)findViewById(R.id.first_name_edit_text);
        EditText editTextLastName=(EditText)findViewById(R.id.last_name_edit_text);
        CircleImageView circleImageView=(CircleImageView)findViewById(R.id.profile_view);
        TextView textViewFullName=(TextView)findViewById(R.id.full_name);
        TextView textViewEmail=(TextView)findViewById(R.id.email_text_view);

        editTextUsername.setText(UserProfile.getUsername());
        editTextFirstName.setText(UserProfile.getFirstName());
        editTextLastName.setText(UserProfile.getLastName());
        textViewFullName.setText(UserProfile.getLastName()+" "+UserProfile.getFirstName());
        textViewEmail.setText(user.getEmail());

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(mStorageReference)
                .into(circleImageView);
    }

    private void DeclareFireBase(){
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            signUpIfNotSignIn();
        }
        mStorageReference =FirebaseStorage.getInstance().getReference().child("images").child(user.getUid());
        mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("profile").child(user.getUid());
        //attaches A listener
        attachDatabaseReadListener();
    }

    private void signUpIfNotSignIn(){
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

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //new user data
                    UserProfile=dataSnapshot.getValue(User.class);
                    //sets the data to the local storage
                    Utils.SetProfileToPreferences(ProfileActivity.this,UserProfile.getUsername(),UserProfile.getFirstName(),
                            UserProfile.getLastName(),user.getEmail(),UserProfile.getImagePath());
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    public void GetImageAndUpload(View view){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);

        startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_PHOTO_PICKER);

    }

    @Override
    public void onActivityResult(int req,int res,Intent data){
        if(req==RC_PHOTO_PICKER && res==RESULT_OK){
            Uri SelectedImageUri=data.getData();

            mStorageReference.putFile(SelectedImageUri).addOnSuccessListener(this,
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    UserProfile.setImagePath(downloadUrl.toString());
                    UpdateProfile(UserProfile);
                }
            });
        }
    }

    private void fillUser(){
        UserProfile.setFirstName(Utils.getSharedPreferenceValue(this,Utils.profileFirstName));
        UserProfile.setLastName(Utils.getSharedPreferenceValue(this,Utils.profileLastName));
        UserProfile.setUsername(Utils.getSharedPreferenceValue(this,Utils.profileUserName));
        UserProfile.setImagePath(Utils.getSharedPreferenceValue(this,Utils.profileUrl));
    }

    private void UpdateProfile(User user) {
        mDatabaseReference.push().setValue(user);
    }
}
