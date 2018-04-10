package com.example.iduma.medmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import MedHelperClass.ReminderDbAdapter;
import Utils.ReminderManager;

public class UsersListActivity extends Activity {

    //
    // Dialog Constants
    //
    private static final int DATE_PICKER_DIALOG = 0;
    private static final int TIME_PICKER_DIALOG = 1;

    //
    // Date Format
    //
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private BootstrapEditText etMedication;
    private BootstrapEditText etInterval;
    private Button mDateButton;
    private Button mTimeButton;
    private BootstrapButton mConfirmButton;
    private Long mRowId;
    private ReminderDbAdapter mDbHelper;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new ReminderDbAdapter(this);

        setContentView(R.layout.activity_users_list);

        mCalendar = Calendar.getInstance();
        etMedication = (BootstrapEditText) findViewById(R.id.etMedication);
        etInterval = (BootstrapEditText) findViewById(R.id.etTime_interval);
        mDateButton = (Button) findViewById(R.id.reminder_date);
        mTimeButton = (Button) findViewById(R.id.reminder_time);

        mConfirmButton = (BootstrapButton) findViewById(R.id.confirm);

        mRowId = savedInstanceState != null ? savedInstanceState.getLong(ReminderDbAdapter.KEY_ROWID)
                : null;

        registerButtonListenersAndSetDefaultText();





    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(ReminderDbAdapter.KEY_ROWID)
                    : null;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open();
        setRowIdFromIntent();
       //populateFields();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_PICKER_DIALOG:
                return showDatePicker();
            case TIME_PICKER_DIALOG:
                return showTimePicker();
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog showDatePicker() {


        DatePickerDialog datePicker = new DatePickerDialog(UsersListActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private TimePickerDialog showTimePicker() {

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                updateTimeButtonText();
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        return timePicker;
    }

    private void registerButtonListenersAndSetDefaultText() {

        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });


        mTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(TIME_PICKER_DIALOG);
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveState();
                setResult(RESULT_OK);
                Toast.makeText(UsersListActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
                finish();

                String medication = etMedication.getText().toString().trim();
                String interval = etInterval.getText().toString().trim();
                String message;
                if (TextUtils.isEmpty(medication)) {
                    message = "Enter Medication name";
                    etMedication.setError(message);
                    return;
                }
                if (TextUtils.isEmpty(interval)) {
                    message = "Enter Drug Interval";
                    etInterval.setError(message);
                    return;

                }

            }

        });

        updateDateButtonText();
        updateTimeButtonText();
    }

    private void populateFields()  {



        // Only populate the text boxes and change the calendar date
        // if the row is not null from the database.
        if (mRowId != null) {
            Cursor reminder = mDbHelper.fetchReminder(mRowId);
            startManagingCursor(reminder);

                etMedication.setText(reminder.getString(
                        reminder.getColumnIndexOrThrow(ReminderDbAdapter.KEY_MEDICATION_NAME)));
                etInterval.setText(reminder.getString(
                        reminder.getColumnIndexOrThrow(ReminderDbAdapter.KEY_INTERVAL)));

            // Get the date from the database and format it for our use.
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = null;
            try {
                String dateString = reminder.getString(reminder.getColumnIndexOrThrow(ReminderDbAdapter.KEY_DATE_TIME));
                date = dateTimeFormat.parse(dateString);
                mCalendar.setTime(date);
            } catch (ParseException e) {
                Log.e("UserListActivity", e.getMessage(), e);
            }
        } else {
            // This is a new task - add defaults from preferences if set.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if(defaultTitle != null)
                etMedication.setText(defaultTitle);

            if(defaultTime != null)
                mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

        }

        updateDateButtonText();
        updateTimeButtonText();

    }

    private void updateTimeButtonText() {
        // Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String timeForButton = timeFormat.format(mCalendar.getTime());
        mTimeButton.setText(timeForButton);
    }

    private void updateDateButtonText() {
        // Set the date button text based upon the value from the database
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ReminderDbAdapter.KEY_ROWID, mRowId);
    }



    private void saveState() {
        String title = etMedication.getText().toString().trim();
        String body = etInterval.getText().toString().trim();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

        if (mRowId == null) {

            long id = mDbHelper.createReminder(title, body, reminderDateTime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
        }

        new ReminderManager(this).setReminder(mRowId, mCalendar);
    }
}
