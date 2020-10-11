package com.example.calendarapp.activites;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.calendarapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class AddEventActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    public static final int PICKFILE_RESULT_CODE = 1;
    final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    EditText eventName, eventDesc, eventVenue, sendTo;
    public Button addEvent,addAttachment;
    TextView spinnerTime, heading;
    CalendarView calendarView;
    ConstraintLayout constraintLayout;
    String date,eventId,hourSelect="00", minuteSelect="00",requestType="post";
    ImageButton mailItem;
    LinearLayout attachmentParent,addEmailLayout;
    Map<String,Integer> index_attachment=new HashMap<>();
    public static List<String> arr = new ArrayList<String>();
    List<String> to = new ArrayList<String>();
    int index=0,mailItemSize=1;
    String attachmentFile;
    int columnIndex;
    long size=0,prev;
    ArrayList<String> attachmentList =new ArrayList<>();
    ArrayList<String> attachmentNameList =new ArrayList<>();
    Map<String,String> map=new HashMap<String,String>();
    Map<String,Long> mapAttachSize=new HashMap<>();
    Map<String,Uri> uriMap =new HashMap<>();
    Map<String,String> names_of_attachment=new HashMap<>();
    Map<String,String> json_map_attachments=new HashMap<>();
    String url = "https://socupdate.herokuapp.com/events";
    String urlPut="https://socupdate.herokuapp.com/events/";

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Date dateToday = new Date();
        date=dateFormat.format(dateToday);
        Log.d("date",date);
        heading=findViewById(R.id.textView3);
        constraintLayout=findViewById(R.id.layout);
        addEmailLayout=findViewById(R.id.addEmailLayout);
        attachmentParent=findViewById(R.id.attachmentLayout);
        addAttachment=findViewById(R.id.attachment);
        mailItem=findViewById(R.id.addMail);
        spinnerTime = findViewById(R.id.eventTime);
        calendarView = findViewById(R.id.calendarView);
        eventName = findViewById(R.id.eventName);
        eventDesc = findViewById(R.id.eventDesc);
        eventDesc.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (eventDesc.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });
        eventVenue = findViewById(R.id.eventVenue);
        sendTo = findViewById(R.id.mailTo);
        addEvent = findViewById(R.id.add_event);
        Calendar mcurrentTime = Calendar.getInstance();
        final int[] hour = {mcurrentTime.get(Calendar.HOUR_OF_DAY)};
        final int[] minute = {mcurrentTime.get(Calendar.MINUTE)};
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                String months="";
                if(month+1<10){
                    months="0"+(month+1);
                }
                else
                    months=""+(month+1);
                String dateSelected;
                if(dayOfMonth<10)
                    dateSelected= "0" +dayOfMonth;
                else
                    dateSelected=""+dayOfMonth;
                date = dateSelected + "/" + months + "/" + year;
                Log.d("date", date);
            }
        });

        Intent intent=getIntent();
        if(intent.getExtras().getString("type").equals("edit")){

            requestType="put";
            heading.setText("Edit Event");
            eventName.setText(intent.getExtras().getString("name"));
            eventDesc.setText(intent.getExtras().getString("desc"));
            eventVenue.setText(intent.getExtras().getString("venue"));
            spinnerTime.setText(intent.getExtras().getString("time"));
            eventId=intent.getExtras().getString("eventId");
            attachmentList=intent.getExtras().getStringArrayList("attachments");
            attachmentNameList=intent.getExtras().getStringArrayList("attachments_name");
            Log.d("Attachment_Size",attachmentList+"/"+attachmentNameList);
            String time=intent.getExtras().getString("time");
            String[] split=time.split(":");
            hourSelect=split[0];
            minuteSelect=split[1];
            hour[0]= Integer.parseInt(split[0]);
            minute[0]=Integer.parseInt(split[1]);
            String dateString=intent.getExtras().getString("date");
            date=dateString;
            Log.d("dateToParse",dateString);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                Date date = sdf.parse(dateString);

                long startDate = date.getTime();
                calendarView.setDate(startDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            urlPut = urlPut+eventId;
            addEvent.setText("Update Event");
            for(int i=0;i<attachmentList.size();i++){
                final LinearLayout attachLayout = new LinearLayout(this);
                final int id = View.generateViewId();
                json_map_attachments.put(attachmentNameList.get(i),attachmentList.get(i));
                index_attachment.put(String.valueOf(id),i);
                attachLayout.setId(id);
                attachLayout.setOrientation(LinearLayout.HORIZONTAL);
//                mapAttachSize.put(String.valueOf(id),sizeOfFile(uri));
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
                final TextView attachmentName = new TextView(this);
                attachmentName.setText(attachmentNameList.get(i));
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
//                        size=size-mapAttachSize.get(String.valueOf(id));
//                        uriMap.remove(String.valueOf(id));
//                        names_of_attachment.remove(String.valueOf(id));
                        json_map_attachments.remove(attachmentNameList.get(index_attachment.get(String.valueOf(id))));
                        Log.d("attachNameB",attachmentNameList.size()+"");
                        attachmentNameList.remove(attachmentNameList.get(index_attachment.get(String.valueOf(id))));
                        Log.d("attachNameA",attachmentNameList.size()+"");
                        attachmentList.remove(attachmentList.get(index_attachment.get(String.valueOf(id))));
                        Log.d("btn_remove","working");
                    }
                });
            }
        }

        spinnerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String a=String.valueOf(selectedMinute),b=String.valueOf(selectedHour);
                        if(selectedMinute<10)
                            a="0"+selectedMinute;
                        if(selectedHour<10)
                            b="0"+selectedHour;
                        spinnerTime.setText(b+" : "+a);
                        hour[0] =selectedHour;
                        minute[0] =selectedMinute;
                        hourSelect=b;
                        minuteSelect=a;
                        Log.d("time",hourSelect+":"+minuteSelect);
                    }
                }, hour[0], minute[0], true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        addEvent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    if (eventName.getText().toString().isEmpty()) {
                        Snackbar.make(constraintLayout, "Please enter event name", Snackbar.LENGTH_LONG).show();
                    } else if (eventDesc.getText().toString().isEmpty())
                        Snackbar.make(constraintLayout, "Please enter event description", Snackbar.LENGTH_LONG).show();
                    else if (eventVenue.getText().toString().isEmpty())
                        Snackbar.make(constraintLayout, "Please enter event venue", Snackbar.LENGTH_LONG).show();
                    else if (hourSelect.equals("00") && minuteSelect.equals("00"))
                        Snackbar.make(constraintLayout, "Please enter event time", Snackbar.LENGTH_LONG).show();
                    else {
                        if (!uriMap.isEmpty()) {
                            Snackbar.make(constraintLayout, "Uploading...", Snackbar.LENGTH_INDEFINITE).show();
                            Iterator iterator = uriMap.entrySet().iterator();
                            int x = 0;
                            while (iterator.hasNext()) {
                                Map.Entry mapElement = (Map.Entry) iterator.next();
                                uploadFile((Uri) mapElement.getValue(), (String) mapElement.getKey());
                            }
                        } else {
                            if (requestType.equals("post"))
                                sendEvent(eventName.getText().toString(), eventDesc.getText().toString(), eventVenue.getText().toString(), hourSelect + ":" + minuteSelect, date);
                            else if (requestType.equals("put"))
                                sendEventPut(eventName.getText().toString(), eventDesc.getText().toString(), eventVenue.getText().toString(), hourSelect + ":" + minuteSelect, date);
                        }
                    }
                }
                else{
                    Snackbar.make(constraintLayout,"Not connected to internet! Cannot create event.",Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mailItem.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                if(sendTo.getText().toString().isEmpty()){
                    Snackbar.make(constraintLayout,"Enter recipients",Snackbar.LENGTH_LONG).show();
                }
                else{
                    final LinearLayout attachLayout= new LinearLayout(AddEventActivity.this);
                    final int id=View.generateViewId();
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
                    if(mailItemSize==1)
                    params.topMargin=(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16,
                            r.getDisplayMetrics()
                    );
                    else
                        params.topMargin=(int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                8,
                                r.getDisplayMetrics()
                        );
                    mailItemSize++;
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
                if (!EasyPermissions.hasPermissions(AddEventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    EasyPermissions.requestPermissions(AddEventActivity.this, "Read External data",2, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                    startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICKFILE_RESULT_CODE);
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
            super.onBackPressed();
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
                                    final int id = View.generateViewId();
                                    Uri uri = data.getClipData().getItemAt(i).getUri();
                                    uriMap.put(String.valueOf(id),uri);
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

//                                    if(result.length()>=23){
//                                        String name=result.split(".")[0];
//                                        String extension=result.split(".")[1];
//                                        result=name.substring(16)+"...";
//                                    }

                                    StringBuilder convertString = new StringBuilder(result);
                                    for(int x=0;x<result.length();x++){
                                        if(result.charAt(x)=='.'){
                                            convertString.setCharAt(x, '-');
                                        }
                                    }
                                    names_of_attachment.put(String.valueOf(id), convertString.toString());
                                    final LinearLayout attachLayout = new LinearLayout(this);
                                    attachLayout.setId(id);
                                    attachLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    mapAttachSize.put(String.valueOf(id),sizeOfFile(uri));
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
                                    final TextView attachmentName = new TextView(this);
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
                                            names_of_attachment.remove(String.valueOf(id));
                                        }
                                    });
                                }
                            }
                            else {
                                Snackbar.make(constraintLayout,"Size limit exceeded",Snackbar.LENGTH_LONG).show();
                                size=prev;
                            }
                        } else {
                            final int[] flag = {0};
                            final int id = View.generateViewId();
                            Uri uri;
                            uri = data.getData();
                            String scheme = uri.getScheme();
                            System.out.println("Scheme type " + scheme);
                            prev=size;
                            size=size+sizeOfFile(uri);
                            if (size <= 5000000) {
//                                filePath = uri.getPath();
                                uriMap.put(String.valueOf(id),uri);
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
//                                if(result.length()>=23){
//                                    String name=result.split(".")[0];
//                                    String extension=result.split(".")[1];
//                                    result=name.substring(16)+"...";
//                                }
                                StringBuilder convertString = new StringBuilder(result);
                                for(int x=0;x<result.length();x++){
                                    if(result.charAt(x)=='.'){
                                        convertString.setCharAt(x, '-');
                                    }
                                }
                                names_of_attachment.put(String.valueOf(id),convertString.toString());

                                final LinearLayout attachLayout = new LinearLayout(this);

                                attachLayout.setId(id);
                                attachLayout.setOrientation(LinearLayout.HORIZONTAL);
                                mapAttachSize.put(String.valueOf(id),sizeOfFile(uri));
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
                                        names_of_attachment.remove(String.valueOf(id));
                                    }
                                });
                            }
                            else{
                                Snackbar.make(constraintLayout,"Size limit exceeded",Snackbar.LENGTH_LONG).show();
                                size=prev;
                            }
                        }
                        }
                    }
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addAttachmnetUrl(String url,String id){
        json_map_attachments.put(names_of_attachment.get(String.valueOf(id)),url);
        int size=0;
        if(!attachmentNameList.isEmpty()){
            size=attachmentNameList.size();
        }
        Log.d("json_map_attachSize", String.valueOf(json_map_attachments.size()));
        Log.d("uriMapSize+size", uriMap.size()+" "+ size);
        if(json_map_attachments.size()==uriMap.size()+size) {
            if(requestType.equals("post"))
                sendEvent(eventName.getText().toString(), eventDesc.getText().toString(), eventVenue.getText().toString(), hourSelect + ":" + minuteSelect, date);
            else if (requestType.equals("put"))
                sendEventPut(eventName.getText().toString(), eventDesc.getText().toString(), eventVenue.getText().toString(), hourSelect + ":" + minuteSelect, date);
        }
//        else
//            Toast.makeText(AddEventActivity.this,"Loading Data...",Toast.LENGTH_SHORT).show();
    }

    public void uploadFile(Uri uri, final String ids){
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
                            addAttachmnetUrl(downloadUrl,ids);
//                        jsonAttachments.put(downloadUrl[0]);
                } else {
                    Snackbar.make(constraintLayout,"Upload Failed",Snackbar.LENGTH_LONG).show();
                    // Handle failures
                    // ...
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendEvent(final String name, final String desc, final String venue, final String time, final String date) {
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
                                if(json_map_attachments.size()!=0) {
                                    jsonObject.put("attachments", new JSONObject(json_map_attachments));
                                }
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
                                            Toast.makeText(AddEventActivity.this,"Event Added",Toast.LENGTH_SHORT).show();
                                            if(to.size()!=0) {
                                                String[] subarray = new String[to.size()];
                                                int z = 0;
                                                for (int i = 0; i < to.size(); i++) {
                                                    subarray[i] = to.get(i);
                                                }
                                                try {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra(Intent.EXTRA_EMAIL, subarray);
                                                    intent.putExtra(Intent.EXTRA_SUBJECT,eventName.getText().toString());
                                                    String body = eventDesc.getText().toString() + " at " + eventVenue.getText().toString() + ", " + hourSelect + ":" + minuteSelect;
                                                    ArrayList<String> body_text=new ArrayList<>();
                                                    body_text.add(body);
                                                    intent.putExtra(Intent.EXTRA_TEXT,body);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    intent.setType("vnd.android.cursor.dir/email");
                                                    ArrayList<Uri> uris=new ArrayList<Uri>();
                                                    if (uriMap.size()!=0) {
                                                        Iterator i= uriMap.entrySet().iterator();
                                                        while(i.hasNext()){
                                                            Map.Entry mapElement = (Map.Entry) i.next();
                                                            uris.add((Uri)mapElement.getValue());
                                                        }
                                                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
                                                    }
                                                    intent.setPackage("com.google.android.gm");
                                                    startActivity(intent);
                                                    finish();
                                                } catch (Throwable t) {
                                                    Toast.makeText(AddEventActivity.this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(AddEventActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            }
                                            else{
                                                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Snackbar.make(constraintLayout,"Event creation failed! Please try again later",Snackbar.LENGTH_LONG);
                                            startActivity(new Intent(AddEventActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }
                            );
                            jsonobj.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            requstQueue.add(jsonobj);

                        }
                    }
                }
            });
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendEventPut(final String name, final String desc, final String venue, final String time, final String date) {
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
                                Snackbar.make(constraintLayout,"Updating event!",Snackbar.LENGTH_INDEFINITE).show();
                                jsonObject.put("token", task.getResult().getToken());
                                jsonObject.put("name", name);
                                jsonObject.put("desc", desc);
                                jsonObject.put("date", date);
                                jsonObject.put("venue", venue);
                                jsonObject.put("time", time);
                                if(json_map_attachments.size()!=0)
                                    jsonObject.put("attachments",new JSONObject(json_map_attachments));
                                Log.d("json",jsonObject.toString());
//                                Toast.makeText(AddEventActivity.this,array.length(),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RequestQueue requstQueue = Volley.newRequestQueue(AddEventActivity.this);
                            JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.PUT, urlPut, jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Toast.makeText(AddEventActivity.this,"Event Updated",Toast.LENGTH_SHORT).show();
                                            if(to.size()!=0) {
                                                String[] subarray = new String[to.size()];
                                                int z = 0;
                                                for (int i = 0; i < to.size(); i++) {
                                                    subarray[i] = to.get(i);
                                                }
                                                try {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra(Intent.EXTRA_EMAIL, subarray);
                                                    intent.putExtra(Intent.EXTRA_SUBJECT,eventName.getText().toString());
                                                    String body = eventDesc.getText().toString() + " at " + eventVenue.getText().toString() + ", " + hourSelect + ":" + minuteSelect;
                                                    intent.putExtra(Intent.EXTRA_TEXT, body);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    intent.setType("vnd.android.cursor.dir/email");
                                                    ArrayList<Uri> uris=new ArrayList<Uri>();
                                                    if (uriMap.size()!=0) {
                                                        Iterator i= uriMap.entrySet().iterator();
                                                        while(i.hasNext()){
                                                            Map.Entry mapElement = (Map.Entry) i.next();
                                                            uris.add((Uri)mapElement.getValue());
                                                        }
                                                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
                                                    }
                                                    intent.setPackage("com.google.android.gm");
                                                    startActivity(intent);
                                                    finish();
                                                } catch (Throwable t) {
                                                    Toast.makeText(AddEventActivity.this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            else{
                                                Intent intent=new Intent(AddEventActivity.this, MainActivity.class);
                                                intent.putExtra("action","db");
                                                Log.d("home_intent","yes");
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(AddEventActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    }

                            ){
                                @Override
                                public Map<String, String> getHeaders()
                                {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    headers.put("Content-Type", "application/json");
                                    return headers;
                                }
                                @Override
                                public String getBodyContentType() {
                                    return "application/json";
                                }
                            };
                            jsonobj.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            requstQueue.add(jsonobj);

                        }
                    }
                }
            });
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICKFILE_RESULT_CODE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Snackbar.make(constraintLayout,"You will not be able to upload attachments!",Snackbar.LENGTH_LONG).show();
    }

}

