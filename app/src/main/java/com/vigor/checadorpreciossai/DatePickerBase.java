package com.vigor.checadorpreciossai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import java.time.Year;
import java.util.Calendar;

public class DatePickerBase extends DialogFragment implements DatePickerDialog.OnDateSetListener{



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);

        // Return the DatePickerDialog
        return  dpd;
    }




    public void onDateSet(DatePicker view, int year, int month, int day){

        String txtName = getArguments().get("txtName").toString();
        month = month+1;
        String mes = "";
        String dia = "";
        if (month<10){
            mes="0"+String.valueOf(month);
        }else{
            mes=String.valueOf(month);
        }
        if (day<10){
            dia="0"+String.valueOf(day);
        }else{
            dia=String.valueOf(day);
        }

        if (txtName.equals("txtFechaInicio")) {
            TextView fecIni = getActivity().findViewById(R.id.txtFechaInicio);
            fecIni.setText(dia + "/" + mes + "/" + year);
            fecIni.clearFocus();
        }
        if(txtName.equals("txtFechaFin")) {
            TextView fecFin = getActivity().findViewById(R.id.txtFechaFin);
            fecFin.setText(dia + "/" + mes + "/" + year);
            fecFin.clearFocus();
        }

    }

    // Set a Listener for Dialog Cancel button click event
        /*
            onCancel(DialogInterface dialog)
                This method will be invoked when the dialog is canceled.
         */
    public void onCancel(DialogInterface dialog){
        // Send a message to confirm cancel button click
        Toast.makeText(getActivity(),"Date Picker Canceled.", Toast.LENGTH_SHORT).show();
    }


}
