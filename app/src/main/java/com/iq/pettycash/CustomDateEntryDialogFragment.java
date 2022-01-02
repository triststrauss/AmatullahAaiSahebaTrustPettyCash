package com.iq.pettycash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomDateEntryDialogFragment extends DialogFragment
{
	TextView dateTextView;
	TextView amountTextView;
	TextView descriptionTextView;
	Spinner topicSpinner;
	Button positiveButton;
	CheckBox isBankCheckBox;


	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = requireActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.custom_date_entry_dialog, null);
		builder.setView(view);


		dateTextView = view.findViewById(R.id.dialog_date_text);
		amountTextView = view.findViewById(R.id.dialog_amount_text);
		descriptionTextView = view.findViewById(R.id.dialog_description_text);
		topicSpinner = view.findViewById(R.id.dialog_topic_spinner);
		isBankCheckBox = view.findViewById(R.id.dialog_isBankCheckBox);
		DialogFragment datePickerFragment = new DatePickerFragment();


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.instance, android.R.layout.simple_spinner_item, Utility.getTopics());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		topicSpinner.setAdapter(adapter);


		dateTextView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				datePickerFragment.show(MainActivity.instance.getSupportFragmentManager(), "datePicker");
				((DatePickerFragment) datePickerFragment).setListener(MainActivity.instance.customDateEntryDialogFragment);
			}
		});


		builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				String topic = topicSpinner.getSelectedItem().toString();
				String description = descriptionTextView.getText().toString();
				int amount = amountTextView.getText().toString().equals("") ? 0 : Integer.parseInt(amountTextView.getText().toString());
				if(topic.equals(Utility.CASH_INWARD))
					amount = -amount;
				Calendar calendar = Calendar.getInstance();
				if(dateTextView.getText().toString().toLowerCase().contains("mm"))
					return;
				String[] date = dateTextView.getText().toString().split("/");
				calendar.set(Integer.parseInt(date[2]),Integer.parseInt(date[1]),Integer.parseInt(date[0]));
				long dateInMilis = calendar.getTimeInMillis();
				System.out.println("DATE : " +  dateInMilis);

				Particular particular = new Particular(topic,description,amount,dateInMilis, isBankCheckBox.isChecked() ? Particular.BANK : Particular.CASH);
				Utility.addNewParticular(particular);

			}


		}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// User cancelled the dialog
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();

		positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

		positiveButton.setEnabled(false);

		return dialog;
	}


	public void onDateSet(int dayOfMonth, int month, int year)
	{
		dateTextView.setText(dayOfMonth + "/" + month + "/" + year);
		positiveButton.setEnabled(true);
	}
}
