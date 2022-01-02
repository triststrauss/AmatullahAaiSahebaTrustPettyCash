package com.iq.pettycash;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{


	CustomDateEntryDialogFragment listener;

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
	{
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(),this,year,month,day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
	{
		System.out.println(year + " / " + month + " / " + dayOfMonth );
		if(listener != null)
			listener.onDateSet(dayOfMonth,month,year);
	}

	public void setListener(CustomDateEntryDialogFragment listener)
	{
		this.listener = listener;
	}
}
