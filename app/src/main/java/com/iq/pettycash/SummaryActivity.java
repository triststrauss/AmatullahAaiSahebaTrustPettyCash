package com.iq.pettycash;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SummaryActivity extends AppCompatActivity
{

	Spinner monthSelectSpinner;
	Spinner yearSelectSpinner;
	TextView totalCreditTextView;
	TextView totalDebitTextView;
	TextView totalBalanceTextView;
	RecyclerView recyclerView;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);



		recyclerView = findViewById(R.id.summary_recycler_view);
		monthSelectSpinner = findViewById(R.id.month_select_spinner);
		yearSelectSpinner = findViewById(R.id.year_select_spinner);
//		totalCreditTextView = findViewById(R.id.credti_summary);
		totalDebitTextView = findViewById(R.id.debit_summary);
//		totalBalanceTextView = findViewById(R.id.balance_summary);

		setViewData();
		setSpinners();

	}


	public void setViewData()
	{


		ArrayList<Particular> filteredList = Utility.getFilteredParticularList();

		SummaryListAdapter summaryListAdapter = new SummaryListAdapter(filteredList);
		recyclerView.setAdapter(summaryListAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		int totalDebit = 0;
		for (int i = 0; i < filteredList.size(); i++)
		{
			int amount = filteredList.get(i).getAmount();
			if(amount > 0)
				totalDebit += filteredList.get(i).getAmount();
		}

		totalDebitTextView.setText(String.format("%s", totalDebit));

//		int totalCredit = 0;
//		for (int i = 0; i < filteredList.size(); i++)
//		{
//			int amount = filteredList.get(i).getAmount();
//			if(amount < 0)
//				totalCredit += filteredList.get(i).getAmount();
//		}
//
//		totalCredit = Math.abs(totalCredit);
//
//		totalCreditTextView.setText(String.format("%s", totalCredit));
//
//		totalBalanceTextView.setText((String.format("%s",(totalCredit - totalDebit))));
	}

	private void setSpinners()
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, Utility.getMonthsList());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		monthSelectSpinner.setAdapter(adapter);
		monthSelectSpinner.setSelection(Utility.getSelectedMonth());

		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, Utility.getYearsList());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSelectSpinner.setAdapter(adapter);
		yearSelectSpinner.setSelection(Utility.getSelectedYearPosition());

		monthSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				Utility.setSelectedMonth(position);
				Utility.setFilteredParticularList();
				setViewData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		yearSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				Utility.setSelectedYearPosition(position);
				Utility.setFilteredParticularList();
				setViewData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
	}


}
