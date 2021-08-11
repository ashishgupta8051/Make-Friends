package com.social.makefriends.loginactivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.manage.userprofile.UpdateProfile;
import com.social.makefriends.notification.Token;
import com.social.makefriends.utils.CheckInternetConnection;
import com.social.makefriends.utils.SharedPrefManager;

import java.util.Arrays;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    private TextView Signup,resetpassword;
    private EditText email,password;
    private FirebaseAuth firebaseAuth;
    private Button loginButton,loginWithPhone;
    private LoginButton facebook_login;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private FirebaseUser firebaseUser;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPrefManager sharedPrefManager;
    private DatabaseReference userDetailsRef;
    private ActivityResultLauncher<Intent> resultLauncher;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = new SharedPrefManager(this);
        Signup = (TextView)findViewById(R.id.signup);
        resetpassword = (TextView)findViewById(R.id.forgot_password);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details");

        email = (EditText)findViewById(R.id.mail);
        password = (EditText)findViewById(R.id.password);

        loginButton = (Button)findViewById(R.id.login);
        loginWithPhone = findViewById(R.id.loginWithPhone);

        facebook_login = (LoginButton)findViewById(R.id.facebook_login_button);
        signInButton = (SignInButton)findViewById(R.id.google_login);

        firebaseAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        facebook_login.setPermissions(Arrays.asList(EMAIL));

        loginWithPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginWithPhone.class));
                finish();
            }
        });

        facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFacebook(v);
            }
        });

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,SignUp.class));
                finish();
            }
        });

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ResetPassword.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if (TextUtils.isEmpty(Email)){
                    email.requestFocus();
                    email.setError("Invalid Email");
                }else if (TextUtils.isEmpty(Password)){
                    password.requestFocus();
                    password.setError("Invalid Password");
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

                    TextView title,message;
                    title = view.findViewById(R.id.progressBarTitle);
                    message = view.findViewById(R.id.progressBarMessage);

                    title.setText("Login");
                    message.setText("Logging In ...");

                    builder.setCancelable(false).setView(view);
                    AlertDialog progressDialog = builder.create();
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                userDetailsRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String id = userDetails.getChatBackgroundWall();
                                            progressDialog.dismiss();
                                            sharedPrefManager.saveWallpaper(id);
                                            startActivity(new Intent(Login.this, Home.class));
                                            finish();
                                        }else {
                                            Log.d("Error","Not found user details");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Google Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLauncher.launch(mGoogleSignInClient.getSignInIntent());
            }
        });

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

                            TextView title,message;
                            title = view.findViewById(R.id.progressBarTitle);
                            message = view.findViewById(R.id.progressBarMessage);

                            title.setText("Login With Google");
                            message.setText("Logging In ...");

                            builder.setCancelable(false).setView(view);
                            AlertDialog progressDialog = builder.create();
                            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            progressDialog.show();

                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                loginWithGoogle(account.getIdToken(),progressDialog);
                            } catch (ApiException e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(Login.this, "not", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithGoogle(String idToken, AlertDialog progressDialog) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Check(progressDialog,"You are login with your 𝐆𝐦𝐚𝐢𝐥 𝐀𝐜𝐜𝐨𝐮𝐧𝐭");
                }else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void loginWithFacebook(View view){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

                TextView title,message;
                title = view.findViewById(R.id.progressBarTitle);
                message = view.findViewById(R.id.progressBarMessage);

                title.setText("Sign In with Facebook");
                message.setText("Logging In ...");

                builder.setCancelable(false).setView(view);
                AlertDialog progressDialog = builder.create();
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                progressDialog.show();

                handleFactoryToken(loginResult.getAccessToken(),progressDialog);
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, "User Canceled it.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFactoryToken(AccessToken accessToken, AlertDialog progressDialog) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Check(progressDialog, "You are login with your 𝐅𝐚𝐜𝐞𝐛𝐨𝐨𝐤 𝐀𝐜𝐜𝐨𝐮𝐧𝐭.");
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }
            }
        });
    }

    private void Check(AlertDialog progressDialog, String loginDetails) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userDetailsRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                String id = userDetails.getChatBackgroundWall();
                                progressDialog.dismiss();
                                sharedPrefManager.saveWallpaper(id);

                                progressDialog.dismiss();
                                startActivity(new Intent(Login.this, Home.class));
                                finish();
                            }else {
                                Log.d("Error","Not found user details");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    String Name = firebaseUser.getDisplayName();
                    String Email = firebaseUser.getEmail();
                    String CurrentUserUid = firebaseAuth.getCurrentUser().getUid();

                    HashMap<String,Object> addUserDetails = new HashMap<>();
                    addUserDetails.put("userName",Name);
                    addUserDetails.put("userEmail",Email);
                    addUserDetails.put("userDob","");
                    addUserDetails.put("userAddress","");
                    addUserDetails.put("userBio","");
                    addUserDetails.put("userProfileImageUrl","None");
                    addUserDetails.put("usersName","");
                    addUserDetails.put("loginDetails",loginDetails);
                    addUserDetails.put("userUid",CurrentUserUid);
                    addUserDetails.put("onlineDate","");
                    addUserDetails.put("onlineTime","");
                    addUserDetails.put("onlineStatus","");
                    addUserDetails.put("chatBackgroundWall","d");
                    addUserDetails.put("userPassword","");

                    databaseReference.setValue(addUserDetails);
                    sharedPrefManager.saveWallpaper("d");

                    progressDialog.dismiss();
                    Intent intent = new Intent(Login.this, UpdateProfile.class);
                    intent.putExtra("Value","A");
                    startActivity(intent);
                    finish();
                    Toast.makeText(Login.this, "Login Successful, Please update your Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter =  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


}