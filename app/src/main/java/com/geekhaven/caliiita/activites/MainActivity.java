package com.geekhaven.caliiita.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.geekhaven.caliiita.data.ListItems;
import com.geekhaven.caliiita.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    int x=0;
    String frag="";
    Boolean visibility=false;
    TextView tvStudentName, tvStudentEmailId;
    Toolbar toolbar;
    NavController navController;
    NavigationView navigationView;
    NavOptions.Builder navBuilder;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DrawerLayout drawerLayout;
    CircleImageView imgUser;
    MenuItem addEvent,swap;
    FirebaseAuth mAuth;
    FloatingActionButton fab;
    GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private CardView cardView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> listItems;
    public static String MainViewMode;
    private static String URL_DATA="https://socupdate.herokuapp.com/events";
    private static String url ="https://socupdate.herokuapp.com/societies/check";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("toolbar-visibility"));
        mAuth=FirebaseAuth.getInstance();
        Intent intent =getIntent();
//        if(Objects.requireNonNull(intent.getExtras()).containsKey("Fragment")){
//            frag="profile";
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("271594298370-jmsnpsmnhm1ahm6viiag2gi2dnpqn0lg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        subscribeToTopic();
        fab=findViewById(R.id.floatBtnProfile);
        drawerLayout=findViewById(R.id.drawerMainActivity);
        navigationView=findViewById(R.id.navigation_main);
        collapsingToolbarLayout=findViewById(R.id.collapsingToolbarLayout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("type","add");
                intent.putExtra("from","Profile");
                startActivity(intent);
            }
        });
        tvStudentName=navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderStudentName);
        tvStudentEmailId=navigationView.getHeaderView(0).findViewById(R.id.tvNavHeaderStudentId);
        imgUser=navigationView.getHeaderView(0).findViewById(R.id.imgNavHeaderUser);

        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            String url=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().replace("s96-c","s400-c");
            Picasso
                    .get()
                    .load(url)
                    .into(imgUser);
        }

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        tvStudentName.setText(user.getDisplayName());
        tvStudentEmailId.setText(user.getEmail());

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Integer.parseInt("232323"));

        mAppBarConfiguration=new AppBarConfiguration.
                Builder(R.id.navigation_nav_main, R.id.navigation_nav_profile, R.id.navigation_nav_list,R.id.navigation_nav_soc_profile, R.id.navigation_nav_society)
                .setDrawerLayout(drawerLayout).build();
        
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navBuilder =  new NavOptions.Builder();
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out).setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out);

        Bundle bundle =new Bundle();
        if(intent.getExtras()!=null && intent.getExtras().containsKey("action")) {
            if(intent.getExtras().getString("action").equals("db")) {
                bundle.putString("loadFrom", "db");
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                if(prefs.getString("society", "false").equals("true")) {
                    visibility=true;
                }
                else
                    visibility=false;
            }
            else {
                bundle.putString("loadFrom", "server");
                sendIdToken();
                SharedPreferences.Editor editor = getSharedPreferences("settingUp", MODE_PRIVATE).edit();
                editor.putBoolean("success",true);
                editor.apply();
            }
        }
        else if(intent.getExtras()==null){
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            if(prefs.getString("society", "false").equals("true")) {
                visibility=true;
            }
            else
                visibility=false;
        }
        if(intent.getExtras()!=null&&intent.getExtras().containsKey("date")){
            bundle.putString("date",intent.getExtras().getString("date"));
        }
//        toolbar.setVisibility(View.INVISIBLE);
        navController.setGraph(R.navigation.mobile_navigation,bundle);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences sharedPreferences=getSharedPreferences("SettingsData",MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("settings",false)) {
            checkAutoStartAndBatterySettings();
        }

        if(intent.getExtras()!=null&&intent.getExtras().containsKey("Fragment")){
            if(intent.getExtras().getString("Fragment").equals("profile")) {
                collapsingToolbarLayout.setBackgroundColor(Color.rgb(44, 43, 43));
                toolbar.setBackgroundColor(Color.rgb(44, 43, 43));
                onCreateOptionsMenu(toolbar.getMenu());
                fab.setVisibility(View.INVISIBLE);
                navController.navigate(R.id.navigation_nav_profile);
                addEvent.setVisible(false);
                swap.setVisible(false);

            }
            else if(intent.getExtras().getString("Fragment").equals("list")){
                Bundle b=new Bundle();
                if(intent.getExtras()!=null&&intent.getExtras().containsKey("date")){
                    b.putString("date",intent.getExtras().getString("date"));
                }
                fab.setVisibility(View.INVISIBLE);
                navController.navigate(R.id.navigation_nav_list,b);
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                if(prefs.getString("society", "false").equals("true")) {
                    visibility=true;
                }
                else
                    visibility=false;
            }
        }
        if(intent.getExtras()!=null&&intent.getExtras().containsKey("back")){
            fab.setVisibility(View.VISIBLE);
            navController.navigate(R.id.navigation_nav_soc_profile);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_log_out: {
                            signOut();
                            break;
                        }
                        case R.id.nav_home: {
                            x=0;
                            collapsingToolbarLayout.setBackgroundColor(Integer.parseInt("232323"));
                            toolbar.setBackgroundColor(Integer.parseInt("232323"));
                            swap.setVisible(true);
//                            swap.setIcon(R.drawable.ic_calendar_1);
                            SharedPreferences preferences =getSharedPreferences("user",MODE_PRIVATE);
                            if(preferences.getString("society", "false").equals("true"))
                                addEvent.setVisible(true);
                            if(navController.getCurrentDestination().getId()!=R.id.navigation_nav_main&&navController.getCurrentDestination().getId()!=R.id.navigation_nav_list){
                                if(MainViewMode.equals("Calendar")){
                                    navController.navigate(R.id.navigation_nav_main,null,navBuilder.build());
                                    fab.setVisibility(View.INVISIBLE);
                                    swap.setIcon(R.drawable.ic_list_view);
                                }
                                else {
                                    navController.navigate(R.id.navigation_nav_list,null,navBuilder.build());
                                    fab.setVisibility(View.INVISIBLE);
                                    swap.setIcon(R.drawable.ic_calendar_1);
                                }
                            }
                            break;
                        }
                        case R.id.nav_profile: {
                            x=0;
                            collapsingToolbarLayout.setBackgroundColor(Color.rgb(44,43,43));
                            toolbar.setBackgroundColor(Color.rgb(44,43,43));
                            swap.setVisible(false);
                            addEvent.setVisible(false);
                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            if(prefs.getString("society", "false").equals("false")) {
                                if (navController.getCurrentDestination().getId() != R.id.navigation_nav_profile) {
                                    fab.setVisibility(View.INVISIBLE);
                                    navController.navigate(R.id.navigation_nav_profile, null, navBuilder.build());

                                }
                            }
                            else{
                                if (navController.getCurrentDestination().getId() != R.id.navigation_nav_soc_profile) {
                                    fab.setVisibility(View.VISIBLE);
                                    navController.navigate(R.id.navigation_nav_soc_profile, null, navBuilder.build());
                                }
                            }
                            break;
                        }
                        case R.id.nav_societies: {
                            x=0;
                            collapsingToolbarLayout.setBackgroundColor(Integer.parseInt("232323"));
                            toolbar.setBackgroundColor(Integer.parseInt("232323"));
                            swap.setVisible(false);
                            addEvent.setVisible(false);
                            if(navController.getCurrentDestination().getId()!=R.id.navigation_nav_society) {
                                fab.setVisibility(View.INVISIBLE);
                                navController.navigate(R.id.navigation_nav_society, null, navBuilder.build());
                            }
                            break;
                        }
                        case R.id.nav_about_us:{
                            x=0;
                            fab.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                            break;
                        }
                    }
                drawerLayout.close();
                return false;
            }
        });
    }

    int RC_SETTINGS_1=1001, RC_SETTINGS_2=1002;
    AlertDialog settingsDialog1, settingsDialog2;

    private void openBatterySettings(){

        AlertDialog.Builder builder2=new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
        View view=inflater.inflate(R.layout.settings_alert_dialog_2,null);

        builder2.setView(view);
        settingsDialog2=builder2.create();
        settingsDialog2.setCanceledOnTouchOutside(false);
        settingsDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        PowerManager pm=(PowerManager) getSystemService(POWER_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(pm!=null && !pm.isIgnoringBatteryOptimizations(getPackageName()) && Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                settingsDialog2.show();
            }
        }

        TextView btnDeny, btnSettings;
        btnDeny=view.findViewById(R.id.deny);
        btnSettings=view.findViewById(R.id.settings);

        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("Warning!");
                alertBuilder.setMessage("You will have to manually enable this Setting to ensure proper functioning of the App! Do you want to Continue");

                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        settingsDialog2.dismiss();
                    }
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog=alertBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivityForResult(intent,RC_SETTINGS_2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SETTINGS_1){
            settingsDialog1.dismiss();
            openBatterySettings();
        }
        else if(requestCode==RC_SETTINGS_2){
            settingsDialog2.dismiss();
        }
    }

    private void openAutoStartSettings(){
        AlertDialog.Builder builder1=new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
        View view=inflater.inflate(R.layout.settings_alert_dialog_1,null);
        builder1.setView(view);
        settingsDialog1=builder1.create();
        settingsDialog1.setCanceledOnTouchOutside(false);
        settingsDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        settingsDialog1.show();

        TextView btnDeny, btnSettings;
        btnDeny=view.findViewById(R.id.deny);
        btnSettings=view.findViewById(R.id.settings);

        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("Warning!");
                alertBuilder.setMessage("You will have to manually enable this Setting to ensure proper functioning of the App! Do you want to Continue");

                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        settingsDialog1.dismiss();
                        openBatterySettings();
                    }
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog=alertBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean settings = true;
                    Intent intent = new Intent();
                    String manufacturer = android.os.Build.MANUFACTURER;
                    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                    } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    } else
                        settings = false;

                    if (settings) {
                        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (list.size() > 0) {
                            startActivityForResult(intent, RC_SETTINGS_1);
                        }
                    }
                    else {
                        settingsDialog1.dismiss();
                        openBatterySettings();
                    }
                }
                catch(Exception e){
                    Log.e("exc", String.valueOf(e));
                }
            }
        });
    }

    private void checkAutoStartAndBatterySettings(){
        String manufacturer = android.os.Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(manufacturer) || "oppo".equalsIgnoreCase(manufacturer) || "vivo".equalsIgnoreCase(manufacturer) ||
                    "Letv".equalsIgnoreCase(manufacturer) || "Honor".equalsIgnoreCase(manufacturer)) {
            openAutoStartSettings();
        }
        SharedPreferences sharedPreferences=getSharedPreferences("SettingsData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("settings",true);
        editor.apply();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if(message.equals("true")){
                toolbar.setVisibility(View.VISIBLE);
            }
            else{
                toolbar.setVisibility(View.INVISIBLE);
            }
        }
    };

    public void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("Event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to Event";
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("VARUN", msg);
                    }
                });
    }
    public void sendIdToken(){
//        final TextView textView = findViewById(R.id.check);
        final String[] token = new String[1];
        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null) {
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("VARUN BHARDWAJ IDTOKEN", Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getToken()));
                                token[0] =task.getResult().getToken();
//                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                ClipData clip = ClipData.newPlainText("idtoken", token[0]);
//                                clipboard.setPrimaryClip(clip);
                                HashMap<String,String> map=new HashMap<String, String>();
                                map.put("token",token[0]);
                                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setMessage("Checking user...");
                                progressDialog.show();
                                RequestQueue requstQueue = Volley.newRequestQueue(MainActivity.this);
                                progressDialog.dismiss();

                                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(map),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
//                                                    textView.setText(response.getString("society"));
                                                    if(response.getString("society").equals("true")){
                                                        addEvent.setVisible(true);
                                                        visibility=true;
                                                        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                                        editor.putString("society", "true");
                                                        editor.apply();
                                                    }
                                                    else{
                                                        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                                        editor.putString("society", "false");
                                                        editor.apply();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(MainActivity.this,"Society check failed!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                                requstQueue.add(jsonobj);

                            } else
                                token[0] = "0";
                        }
                    });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        addEvent=menu.findItem(R.id.add_event);
        swap=menu.findItem(R.id.calendar);
        MainViewMode="Calendar";

        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("Fragment")){
            if(getIntent().getExtras().getString("Fragment").equals("profile")){
                addEvent.setVisible(false);
                visibility=false;
                swap.setVisible(false);
            }
            else {
                swap.setIcon(R.drawable.ic_calendar_1);
            }
        }
        addEvent.setVisible(visibility);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed() {
        x++;
        if(x==1){
            Toast.makeText(getApplicationContext(),"Press one more time to exit!",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_event){
            Intent intent =new Intent(MainActivity.this,AddEventActivity.class);
            intent.putExtra("type","add");
            intent.putExtra("from","Main");
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.calendar){
            x=0;
            if(navController.getCurrentDestination().getId()!=R.id.navigation_nav_list) {
                navController.navigate(R.id.navigation_nav_list,null,navBuilder.build());
                swap.setIcon(R.drawable.ic_calendar_1);
                MainViewMode="List";
            }
            else {
                Bundle bundle=new Bundle();
                bundle.putString("action","db");
                navController.navigate(R.id.navigation_nav_main,bundle,navBuilder.build());
                swap.setIcon(R.drawable.ic_list_view);
                MainViewMode="Calendar";
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void signOut(){
        mAuth.signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"LogOut Successful",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("settingUp", MODE_PRIVATE).edit();
                        editor.putBoolean("success",false);
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
    }
}