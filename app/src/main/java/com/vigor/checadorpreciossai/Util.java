package com.vigor.checadorpreciossai;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static String SUC_NAME = "ADMIN";
    public static int IS_ADMIN = 1;
    public static int  _LISTA_PRECIOS  = 0;

    //creates an datapicker
    public static void createDatePicker(final TextView txtFecha, final Activity activity) {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String now = format.format(date);


        txtFecha.setText(now);


        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(activity);
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String nd = "" + dayOfMonth;
                                String nm = "" + monthOfYear;
                                if (dayOfMonth < 10) {
                                    nd = "0" + dayOfMonth;
                                }


                                if ((monthOfYear + 1) < 10) {
                                    nm = "0" + (monthOfYear + 1);
                                } else {
                                    nm = "" + (monthOfYear + 1);
                                }

                                // set day of month , month and year value in the edit text
                                txtFecha.setText(nd + "/"
                                        + nm + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}