package gps_app.com.example.gps_app_4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static gps_app.com.example.gps_app_4.MainActivity.EXTRA_MESSAGE;

public class ShowHistory extends AppCompatActivity {
    private String dateText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        CalendarView calendarDate = (CalendarView) findViewById(R.id.calendarView2);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        dateText = df.format(Calendar.getInstance().getTime());


        calendarDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView arg0, int year, int month,
                                            int date) {
                String dateS = validateStringToDate(String.valueOf(date));
                String monthS = validateStringToDate(String.valueOf(month));
                String yearS = validateStringToDate(String.valueOf(year));
                dateText = dateS + "-" + monthS + "-" + yearS;
            }
        });

    }

    public void btnShowHistoryClick(View view)
    {
        //Toast.makeText(ShowHistory.this, dateText, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MapsActivity.class);
        String message = "2#" + dateText;
        intent.putExtra(EXTRA_MESSAGE, message); //wysyłanie wiadomości do podrzednej activity
        startActivity(intent);
    }

    public String validateStringToDate(String value)
    {
        if (value.length() == 1)
        {
            value = "0" + value;
            return  value;
        }
        return value;
    }
}
