package com.example.calendarapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class AddEventActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    public static final int PICKFILE_RESULT_CODE = 1;
    EditText eventName, eventDesc, eventVenue, sendTo, sendCC;
    public Button addEvent;
    TextView spinnerTime, spinnerMinute;
    CalendarView calendarView;
    String date;
    String hourSelect, minuteSelect;
    ImageButton mailItem;
    LinearLayout itemLayout1,attachmentParent,addEmailLayout;
    Button addAttachment;
    private Uri fileUri;
    private String filePath;
    public JSONArray jsonAttachments=new JSONArray();
    public static List<String> arr = new ArrayList<String>();
    List<String> to = new ArrayList<String>();
    List<Uri> uriList=new ArrayList<>();
    JSONArray jsonEmailRecipients=new JSONArray();
    String[] mailList= new String[15];
    int index=0;
    long size=0,prev;
    Map<String,String> map=new HashMap<String,String>();
    Map<String,Long> mapAttachSize=new HashMap<>();
    Map<String,Uri> uriMap =new HashMap<>();
    Map<String,Uri> uriMapAttach= new HashMap<>();
    Map<String,File> fileMap =new HashMap<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        String[] arraySpinner = new String[]{
//                 "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"
//        };
//        String[] minuteArray = new String[]{
//                 "00", "10", "20", "30", "40", "50"
//        };
        addEmailLayout=findViewById(R.id.addEmailLayout);
        attachmentParent=findViewById(R.id.attachmentLayout);
        addAttachment=findViewById(R.id.attachment);
        mailItem=findViewById(R.id.addMail);
        spinnerTime = findViewById(R.id.eventTime);
        calendarView = findViewById(R.id.calendarView);
        spinnerMinute = findViewById(R.id.eventMinute);
        eventName = findViewById(R.id.eventName);
        eventDesc = findViewById(R.id.eventDesc);
        eventVenue = findViewById(R.id.eventVenue);
        sendTo = findViewById(R.id.mailTo);
        addEvent = findViewById(R.id.add_event);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, arraySpinner);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerTime.setAdapter(adapter);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, minuteArray);
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerMinute.setAdapter(adapter2);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                String months="";
                if(month+1<10){
                    months="0"+(month+1);
                }
                date = dayOfMonth + "/" + months + "/" + year;
                Log.d("date", date);
            }
        });
        spinnerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String a=String.valueOf(selectedMinute),b=String.valueOf(selectedHour);
                        if(selectedMinute==0)
                            a="00";
                        if(selectedHour<10)
                            b="0"+selectedHour;
                        spinnerTime.setText(b);
                        spinnerMinute.setText(a);
                        hourSelect=b;
                        minuteSelect=a;
                        Log.d("time",hourSelect+":"+minuteSelect);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
//        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                hour = spinnerTime.getItemAtPosition(spinnerTime.getSelectedItemPosition()).toString();
//                Log.d("hour", hour);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spinnerMinute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                minute = spinnerMinute.getItemAtPosition(spinnerMinute.getSelectedItemPosition()).toString();
//                Log.d("minute", minute);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        Log.d("eventTime", hour + ":" + minute);
        
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventName.getText().toString().isEmpty()){
                    Toast.makeText(AddEventActivity.this,"Please enter event name",Toast.LENGTH_SHORT).show();
                }
                else if(eventDesc.getText().toString().isEmpty())
                    Toast.makeText(AddEventActivity.this,"Please enter event description",Toast.LENGTH_SHORT).show();
                else if(eventVenue.getText().toString().isEmpty())
                    Toast.makeText(AddEventActivity.this,"Please enter event venue",Toast.LENGTH_SHORT).show();
                else if(hourSelect.equals("00")&&minuteSelect.equals("00"))
                    Toast.makeText(AddEventActivity.this,"Please enter event time",Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(AddEventActivity.this,"Uploading....",Toast.LENGTH_SHORT).show();
                    Iterator iterator = uriMap.entrySet().iterator();
                    int x=0;
                    while(iterator.hasNext()){
                        Map.Entry mapElement = (Map.Entry)iterator.next();
                        uploadFile((Uri)mapElement.getValue());
                    }
                }
            }
        });
        mailItem.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                if(sendTo.getText().toString().isEmpty()){
                    Toast.makeText(AddEventActivity.this,"Enter recipients",Toast.LENGTH_SHORT).show();
                }
                else{
                    final LinearLayout attachLayout= new LinearLayout(AddEventActivity.this);
                    final int id=attachLayout.generateViewId();
                    attachLayout.setId(id);
                    attachLayout.setOrientation(LinearLayout.HORIZONTAL);
                    Resources r = AddEventActivity.this.getResources();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    30,
                                    r.getDisplayMetrics()));
                    attachLayout.setBackgroundResource(R.color.colorPrimaryDark);
                    params.topMargin=(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16,
                            r.getDisplayMetrics()
                    );
                    params.leftMargin=(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            22,
                            r.getDisplayMetrics()
                    );
                    params.rightMargin=(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            22,
                            r.getDisplayMetrics()
                    );
                    attachLayout.setWeightSum(10);
                    addEmailLayout.addView(attachLayout,params);
                    TextView attachmentName=new TextView(AddEventActivity.this);
                    attachmentName.setText(sendTo.getText().toString());
                    to.add(sendTo.getText().toString());
//                    mailList[index]=sendTo.getText().toString();
                    map.put(String.valueOf(id),sendTo.getText().toString());
                    index++;
                    sendTo.setText("");
                    attachmentName.setSingleLine();
                    LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                            0
                            ,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsText.gravity=Gravity.CENTER;
                    paramsText.weight=9;
                    paramsText.leftMargin=(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            8,
                            r.getDisplayMetrics()
                    );
                    LinearLayout imageLayout= new LinearLayout(AddEventActivity.this);
                    LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    layoutParams.rightMargin=(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            4,
                            r.getDisplayMetrics()
                    );
                    layoutParams.weight=1;
                    attachLayout.addView(attachmentName,0,paramsText);
                    attachLayout.addView(imageLayout,1,layoutParams);
                    ImageButton btn= new ImageButton(AddEventActivity.this);
                    btn.setImageResource(R.drawable.cross);
                    btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    btn.setBackgroundResource(R.color.colorPrimaryDark);
                    LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    50,
                                    r.getDisplayMetrics()
                            ),
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    50,
                                    r.getDisplayMetrics()
                            ));
                    paramsBtn.gravity= Gravity.CENTER;
                    imageLayout.addView(btn,0,paramsBtn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addEmailLayout.removeView(attachLayout);
//                            mailList[map.get(String.valueOf(id))]="";
                            to.remove(new String(map.get(String.valueOf(id))));
                        }
                    });
                }
            }
        });

        addAttachment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if (!EasyPermissions.hasPermissions(AddEventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    EasyPermissions.requestPermissions(this, "Read External data", 2, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                Intent chooseFile = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                }
                assert chooseFile != null;
                chooseFile.setType("*/*");
                chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });
    }
    public Long sizeOfFile(Uri uri){
        String scheme = uri.getScheme();
        System.out.println("Scheme type " + scheme);
        if(scheme.equals(ContentResolver.SCHEME_CONTENT))
        {
            try {
                InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(uri);
                return Long.valueOf(fileInputStream.available());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File f = null;
            String path = uri.getPath();
            try {
                f = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(f.length()!=0)
                return f.length();
        }
        long x=0;
        return x;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    if(null != data) { // checking empty selection
                        if(null != data.getClipData()) { // checking multiple selection or not
//                            final long[] dataSize = {0};
                            prev=size;
                            for(int i=0;i<data.getClipData().getItemCount();i++){
                                size = size +sizeOfFile(data.getClipData().getItemAt(i).getUri());
                            }
                            if(size <=5000000) {
                                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                    final int[] flag = {0};
                                    Uri uri = data.getClipData().getItemAt(i).getUri();
                                    filePath = uri.getPath();
                                    File file = new File(uri.getPath());
                                    String result = null;
                                    if (uri.getScheme().equals("content")) {
                                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                                        try {
                                            if (cursor != null && cursor.moveToFirst()) {
                                                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                            }
                                        } finally {
                                            cursor.close();
                                        }
                                    }
                                    if (result == null) {
                                        result = uri.getPath();
                                        int cut = result.lastIndexOf('/');
                                        if (cut != -1) {
                                            result = result.substring(cut + 1);
                                        }
                                    }
                                    final LinearLayout attachLayout = new LinearLayout(this);
                                    final int id = View.generateViewId();
                                    attachLayout.setId(id);
                                    attachLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    mapAttachSize.put(String.valueOf(id),sizeOfFile(uri));
                                    uriMap.put(String.valueOf(id),uri);
                                    uriMapAttach.put(String.valueOf(id),Uri.parse("file://" +result));
                                    fileMap.put(String.valueOf(id),file);
                                    Resources r = AddEventActivity.this.getResources();
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            (int) TypedValue.applyDimension(
                                                    TypedValue.COMPLEX_UNIT_DIP,
                                                    30,
                                                    r.getDisplayMetrics()));
                                    attachLayout.setBackgroundResource(R.color.colorPrimaryDark);
                                    if (i == 0) {
                                        params.topMargin = (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP,
                                                16,
                                                r.getDisplayMetrics()
                                        );
                                    } else {
                                        params.topMargin = (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP,
                                                8,
                                                r.getDisplayMetrics()
                                        );
                                    }
                                    params.leftMargin = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            22,
                                            r.getDisplayMetrics()
                                    );
                                    params.rightMargin = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            22,
                                            r.getDisplayMetrics()
                                    );
                                    attachLayout.setWeightSum(10);
                                    attachmentParent.addView(attachLayout, params);
                                    TextView attachmentName = new TextView(this);
                                    attachmentName.setText(result);
                                    attachmentName.setSingleLine();
                                    LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                                            0
                                            ,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    paramsText.gravity = Gravity.CENTER;
                                    paramsText.weight = 9;
                                    paramsText.leftMargin = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            8,
                                            r.getDisplayMetrics()
                                    );
                                    LinearLayout imageLayout = new LinearLayout(this);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            0,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                    );
                                    layoutParams.rightMargin = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            4,
                                            r.getDisplayMetrics()
                                    );
                                    layoutParams.weight = 1;
                                    attachLayout.addView(attachmentName, 0, paramsText);
                                    attachLayout.addView(imageLayout, 1, layoutParams);
                                    ImageButton btn = new ImageButton(this);
                                    btn.setImageResource(R.drawable.cross);
                                    btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                    btn.setBackgroundResource(R.color.colorPrimaryDark);
                                    LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                                            (int) TypedValue.applyDimension(
                                                    TypedValue.COMPLEX_UNIT_DIP,
                                                    50,
                                                    r.getDisplayMetrics()
                                            ),
                                            (int) TypedValue.applyDimension(
                                                    TypedValue.COMPLEX_UNIT_DIP,
                                                    50,
                                                    r.getDisplayMetrics()
                                            ));
                                    paramsBtn.gravity = Gravity.CENTER;
                                    imageLayout.addView(btn, 0, paramsBtn);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            attachmentParent.removeView(attachLayout);
                                            size = size -mapAttachSize.get(String.valueOf(id));
                                            uriMap.remove(String.valueOf(id));
                                            uriMapAttach.remove(String.valueOf(id));
                                        }
                                    });
                                }
                            }
                            else {
                                Toast.makeText(AddEventActivity.this,"Size limit exceeded",Toast.LENGTH_SHORT).show();
                                size=prev;
                            }
                        } else {
                            final int[] flag = {0};
                            Uri uri;
                            uri = data.getData();
                            String scheme = uri.getScheme();
                            System.out.println("Scheme type " + scheme);
                            prev=size;
                            size=size+sizeOfFile(uri);
                            if (size <= 5000000) {
                                filePath = uri.getPath();
                                File file = new File(uri.getPath());
                                String result = null;
                                if (uri.getScheme().equals("content")) {
                                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                                    try {
                                        if (cursor != null && cursor.moveToFirst()) {
                                            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                        }
                                    } finally {
                                        cursor.close();
                                    }
                                }
                                if (result == null) {
                                    result = uri.getPath();
                                    int cut = result.lastIndexOf('/');
                                    if (cut != -1) {
                                        result = result.substring(cut + 1);
                                    }
                                }
                                final LinearLayout attachLayout = new LinearLayout(this);
                                final int id = View.generateViewId();
                                attachLayout.setId(id);
                                attachLayout.setOrientation(LinearLayout.HORIZONTAL);
                                mapAttachSize.put(String.valueOf(id),sizeOfFile(uri));
                                uriMapAttach.put(String.valueOf(id),Uri.parse("file://" +result));
                                uriMap.put(String.valueOf(id),uri);
                                fileMap.put(String.valueOf(id),file);
                                Resources r = AddEventActivity.this.getResources();
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP,
                                                30,
                                                r.getDisplayMetrics()));
                                attachLayout.setBackgroundResource(R.color.colorPrimaryDark);
                                params.topMargin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        8,
                                        r.getDisplayMetrics()
                                );
                                params.leftMargin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        22,
                                        r.getDisplayMetrics()
                                );
                                params.rightMargin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        22,
                                        r.getDisplayMetrics()
                                );
                                attachLayout.setWeightSum(10);
                                attachmentParent.addView(attachLayout, params);
                                TextView attachmentName = new TextView(this);
                                attachmentName.setText(result);
                                attachmentName.setSingleLine();
                                LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                                        0
                                        ,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                paramsText.gravity = Gravity.CENTER;
                                paramsText.weight = 9;
                                paramsText.leftMargin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        8,
                                        r.getDisplayMetrics()
                                );
                                LinearLayout imageLayout = new LinearLayout(this);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                );
                                layoutParams.rightMargin = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        4,
                                        r.getDisplayMetrics()
                                );
                                layoutParams.weight = 1;
                                attachLayout.addView(attachmentName, 0, paramsText);
                                attachLayout.addView(imageLayout, 1, layoutParams);
                                ImageButton btn = new ImageButton(this);
                                btn.setImageResource(R.drawable.cross);
                                btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                btn.setBackgroundResource(R.color.colorPrimaryDark);
                                LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                                        (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP,
                                                50,
                                                r.getDisplayMetrics()
                                        ),
                                        (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP,
                                                50,
                                                r.getDisplayMetrics()
                                        ));
                                paramsBtn.gravity = Gravity.CENTER;
                                imageLayout.addView(btn, 0, paramsBtn);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        attachmentParent.removeView(attachLayout);
                                        size=size-mapAttachSize.get(String.valueOf(id));
                                        uriMap.remove(String.valueOf(id));
                                        uriMapAttach.remove(String.valueOf(id));
                                    }
                                });
                            }
                            else{
                                Toast.makeText(AddEventActivity.this,"Size limit exceeded",Toast.LENGTH_SHORT).show();
                                size=prev;
                            }
                        }
                        }
                    }


                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add, menu);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addAttachmnetUrl(String url){
        jsonAttachments.put(url);
        if(jsonAttachments.length()==uriMap.size())
            sendEvent(eventName.getText().toString(), eventDesc.getText().toString(), eventVenue.getText().toString(), hourSelect + ":" + minuteSelect, date);
        else
            Toast.makeText(AddEventActivity.this,"Loading Data...",Toast.LENGTH_SHORT).show();
    }
    public void uploadFile(Uri uri){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference =storage.getReference();
        final StorageReference ref
                = storageReference
                .child(
                        "files/"
                                + UUID.randomUUID().toString());
        UploadTask uploadTask=ref.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadUrl = downloadUri.toString();
                        Log.d("url", downloadUrl);
                        if(!downloadUrl.isEmpty())
                            addAttachmnetUrl(downloadUrl);
//                        jsonAttachments.put(downloadUrl[0]);
                } else {
                    Toast.makeText(AddEventActivity.this,"Upload Failed",Toast.LENGTH_LONG).show();
                    // Handle failures
                    // ...
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendEvent(final String name, final String desc, final String venue, final String time, final String date) {
        final String url = "https://socupdate.herokuapp.com/events";
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getToken() != null) {
                            JSONObject jsonObject =new JSONObject();
                            try {
                                JSONArray array = new JSONArray();
                                for (int i = 0; i < arr.size(); i++) {
                                    array.put(arr.get(i));
                                }
                                jsonObject.put("token", task.getResult().getToken());
                                jsonObject.put("name", name);
                                jsonObject.put("desc", desc);
                                jsonObject.put("date", date);
                                jsonObject.put("venue", venue);
                                jsonObject.put("time", time);
                                jsonObject.put("attachments",jsonAttachments);
                                Log.d("json",jsonObject.toString());
//                                Toast.makeText(AddEventActivity.this,array.length(),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RequestQueue requstQueue = Volley.newRequestQueue(AddEventActivity.this);
                            JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }
                            );
                            requstQueue.add(jsonobj);
                        }
                    }
                }
            });
        }
        Toast.makeText(this,"Event Added",Toast.LENGTH_SHORT).show();
        int size=0;
//        for (int i = 0; i < 15; i++) {
//            if(!mailList[i].isEmpty())
//                size++;
//        }
        String[] subarray = new String[to.size()];
        int z=0;
        for (int i = 0; i < to.size(); i++) {
            subarray[i]=to.get(i);
        }
        try {
            Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
            emailSelectorIntent.setData(Uri.parse("mailto:"));
            Log.d("mailList", Arrays.toString(subarray));
            final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, subarray);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, eventName.getText().toString());
            ArrayList<String> bodyList = new ArrayList<>();
            String body = eventDesc.getText().toString() + " at " + eventVenue.getText().toString() + ", " + hourSelect + ":" + minuteSelect;
            bodyList.add(body);
            ArrayList<Uri> uris = new ArrayList<Uri>();
            emailIntent.putExtra(Intent.EXTRA_TEXT, bodyList);
            emailIntent.setSelector(emailSelectorIntent);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            emailIntent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Iterator iterator = fileMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry) iterator.next();
                File f =(File) mapElement.getValue();
                f.setReadable(true,false);
                uris.add(Uri.fromFile(f));
            }
//            emailIntent.setType("text/html");
//            emailIntent.setType("application/pdf");
//            emailIntent.setType("message/rfc822");
//            emailIntent.setType("text/plain");
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            Log.d("URI", String.valueOf(uris));
            startActivity(Intent.createChooser(emailIntent, "Choose an email application..."));
        }
        catch (Throwable t)
        {
            Toast.makeText(this, "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Intent chooseFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }
        chooseFile.setType("*/*");
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this,"You will not be able to upload data",Toast.LENGTH_SHORT).show();
    }
}

