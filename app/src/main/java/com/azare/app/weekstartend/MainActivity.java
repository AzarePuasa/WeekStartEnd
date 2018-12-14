package com.azare.app.weekstartend;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView tvDate;
    TextView tvTime;
    TextView tvStartWeekDate;
    TextView tvEndWeekDate;

    DatePicker simpleDatePicker;
    Button submit;

    TextView tvPickedDate;
    Spinner spinner;

    public final String[] dayOfWeek = {"Sunday", "Monday","Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvStartWeekDate = (TextView) findViewById(R.id.tvStartWeekDate);
        tvEndWeekDate = (TextView) findViewById(R.id.tvEndWeekDate);

        // initiate the date picker and a button
        simpleDatePicker = (DatePicker) findViewById(R.id.simpleDatePicker);
        submit = (Button) findViewById(R.id.submitButton);

        tvPickedDate = (TextView) findViewById(R.id.tvPickedDate);

        spinner = (Spinner) findViewById(R.id.spinner);

        //set spinner adapter
        ArrayAdapter<String> aaSpinner =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dayOfWeek);
        spinner.setAdapter(aaSpinner);


        //Process Current Date and Time Populate field
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String strDate = getCurrentDate(date);
        String strTime = getCurrentTime(date);
        tvDate.setText(strDate);
        tvTime.setText(strTime);

        // perform click event on submit button
        submit.setOnClickListener(submitClicked);
    }

    private View.OnClickListener submitClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            // get the values for day of month , month and year from a date picker
            String day = "Day = " + simpleDatePicker.getDayOfMonth();
            String month = "Month = " + (simpleDatePicker.getMonth() + 1);
            String year = "Year = " + simpleDatePicker.getYear();

            // display the values by using a toast
            Toast.makeText(getApplicationContext(), day + "\n"
                    + month + "\n" + year, Toast.LENGTH_LONG).show();

            String strDateFromCalendar = simpleDatePicker.getDayOfMonth()
                    + "/" + (simpleDatePicker.getMonth() + 1)
                    + "/" +  simpleDatePicker.getYear();

            tvPickedDate.setText(strDateFromCalendar);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date chosenDate = new Date();

            try {
                chosenDate = formatter.parse(strDateFromCalendar);
                Log.i("Date", formatter.format(chosenDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Read Spinner selection.
            String strSelectedSpinnerItem = (String) spinner.getSelectedItem();
            int iSelected = Arrays.asList(dayOfWeek).indexOf(strSelectedSpinnerItem);
            Log.i("Date", "Spinner Selection: " + iSelected );
            Log.i("Date", "Day Selected: " + dayOfWeek[iSelected] );

            //Process Start and End of chosen date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(chosenDate);
            calendar.setFirstDayOfWeek(iSelected + 1);

            Log.i("Date", "Chosen Date: " + formatter.format(calendar.getTime()) );
            Log.i("Date", "Set First Day of Chosen Date: " + calendar.getFirstDayOfWeek() );

            Date[] startEndDate = getStartEndDateOfWeek(chosenDate, strSelectedSpinnerItem);
            //String strEndOfWeek = getEndDateOfWeek(chosenDate, calendar);
            tvStartWeekDate.setText(formatter.format(startEndDate[0]));
            tvEndWeekDate.setText(formatter.format(startEndDate[1]));
        }
    };

    public void showDatePicker(View v) {
        DialogFragment newFragment = new MyDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    public static String getCurrentDate(Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");

        String strDate = dateformat.format(date);

        return strDate;
    }

    public static String getCurrentTime(Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa");


        String strTime = dateformat.format(date);

        return strTime;
    }

    /*
    Get the start date of Week for the given date
     */
    public Date[] getStartEndDateOfWeek(Date chosenDate, String selectedStartDayOfWeek) {

        Date[] startendDate = new Date[2];

        //Date format: for Day format: Monday, Tuesday...
        SimpleDateFormat sdfDayOfWeek=new SimpleDateFormat("EEEE");

        //Date format: for Date
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

        //identify the first day of week
        int iStartDayOfWeek = Arrays.asList(dayOfWeek).indexOf(selectedStartDayOfWeek) + 1;
        Log.i("Date", "\nStart Day Of Week(int): " + iStartDayOfWeek);

        //Calendar Object for chosen date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chosenDate);
        int iChosenDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String chosenDayOfTheWeek = sdfDayOfWeek.format(calendar.getTime());

        Log.i("Date","\nDay of Chosen Date(int): " + iChosenDayOfWeek);
        Log.i("Date","\nDay of Chosen Date(String): " + chosenDayOfTheWeek);

        int iDifference = iStartDayOfWeek - iChosenDayOfWeek;
        Log.i("Date","\nDifference: " + iDifference);

        Calendar cStart = Calendar.getInstance();

        if (iDifference < 0) {
            Log.i("Date","\nFirst Day of Week < Chosen Date ");
            cStart.setTime(chosenDate);
            cStart.add(Calendar.DATE,iDifference);
        } else if (iDifference > 0) {
            Log.i("Date","\nFirst Day of Week > Chosen Date ");
            cStart.setTime(chosenDate);
            cStart.add(Calendar.DATE,iDifference);
            cStart.add(Calendar.DATE,-7);
        } else if (iDifference == 0) {
            Log.i("Date","\nFirst Day of Week == Chosen Date");
            cStart.setTime(chosenDate);
        }

        startendDate[0] = cStart.getTime();

        Calendar cEnd = cStart;

        cEnd.add(Calendar.DATE,7);

        startendDate[1] = cEnd.getTime();

        Log.i("Date", "\nResult: " + cStart.getTime());

        //return sdf.format(cStart.getTime()) + " (" + sdfDayOfWeek.format(cStart.getTime()) + ")";
        return startendDate;

    }
}
