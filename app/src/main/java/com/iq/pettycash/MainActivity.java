package com.iq.pettycash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
{
	Spinner topicSpinner;
	Spinner monthSelectSpinner;
	static MainActivity instance;
	RecyclerView recyclerView;


	TextView editTextAmount;
	TextView editTextDescription;
	TextView totalDebitTextView;
	TextView totalCreditTextView;
	TextView balanceTextView;
	CheckBox isBankCheckBox;
//	TextView totalBankDebitTextView;

	AlertDialog.Builder builder;
	AlertDialog alert;
	Particular particularToDelete;

	CustomDateEntryDialogFragment customDateEntryDialogFragment;

	private static User currentUser;

	public static User getCurrentUser()
	{
		return currentUser;
	}

	public static void setCurrentUser(User currentUser)
	{
		MainActivity.currentUser = currentUser;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		super.onCreate(savedInstanceState);

		Utility.init();

		if(FirebaseAuth.getInstance().getCurrentUser() == null)
		{
			startActivity(new Intent(this, LoginActivity.class));
			this.finish();
		}

		setContentView(R.layout.activity_main);
		instance = this;

		Utility.readNewData();

		customDateEntryDialogFragment = new CustomDateEntryDialogFragment();



		topicSpinner = findViewById(R.id.topicSpinner);

		recyclerView = findViewById(R.id.recylerView);

		Button addButton = findViewById(R.id.addButton);
		editTextAmount = findViewById(R.id.editTextAmount);
		editTextDescription = findViewById(R.id.editTextDescription);
		totalDebitTextView = findViewById(R.id.totalDebitTextView);
		totalCreditTextView = findViewById(R.id.totalCreditTextView);
		balanceTextView = findViewById(R.id.balanceTextView);
		isBankCheckBox = findViewById(R.id.isBankCheckBox);
//		totalBankDebitTextView = findViewById(R.id.totalBankDebitTextView);

		addButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onAddClicked();
			}
		});
	}

	private void setWarningDialog()
	{
		if(alert != null)
			return;

		builder = new AlertDialog.Builder(this);

		if(currentUser.isAdmin())
		{
			builder.setMessage("Are you sure you want to delete ?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							if (particularToDelete != null)
								Utility.deleteParticular(particularToDelete);

							Toast.makeText(getApplicationContext(), "Deleted ...",
									Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();
							particularToDelete = null;
						}
					});

			alert.setTitle("Delete ?");
		}
		else
		{
			builder.setMessage("You are not authorized to make changes !!!").setTitle("Error !!!").setCancelable(true);
		}

		alert = builder.create();


	}

	private void onAddClicked()
	{
		if(!currentUser.isAdmin())
		{
			showAlert();
			return;
		}

		String topic = topicSpinner.getSelectedItem().toString();
		String description = String.valueOf(editTextDescription.getText());
		int amount = editTextAmount.getText().toString().equals("") ? 0 : Integer.parseInt(editTextAmount.getText().toString());
		long date = new GregorianCalendar().getTimeInMillis();
		if(topic.equals(Utility.CASH_INWARD))
			amount = -amount;

		Particular particular = new Particular(topic, description, amount, date, isBankCheckBox.isChecked() ? Particular.BANK : Particular.CASH);
		Utility.addNewParticular(particular);

		editTextAmount.setText("");
		editTextDescription.setText("");
		isBankCheckBox.setChecked(false);
	}

	private void showAlert()
	{
		setWarningDialog();
		alert.show();
	}

	public void onDataFetch()
	{
		setViewData();

	}

	private void setViewData()
	{

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, Utility.getTopics());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		topicSpinner.setAdapter(adapter);

		ArrayList<Particular> arrayList = Utility.getFilteredParticularList();
		Collections.reverse(arrayList);

		MainActivityListAdapter mainActivityListAdapter = new MainActivityListAdapter(arrayList);
		recyclerView.setAdapter(mainActivityListAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		int totalDebitCash = 0;
		int totalDebitBank = 0;
		for (int i = 0; i < arrayList.size(); i++)
		{
			int amount = arrayList.get(i).getAmount();
			if(amount > 0)
			{
				if(arrayList.get(i).getTransactionType().equals(Particular.CASH))
					totalDebitCash += arrayList.get(i).getAmount();
				else
					totalDebitBank += arrayList.get(i).getAmount();
			}
		}

		totalDebitTextView.setText(String.format("%s", totalDebitCash));
//		totalBankDebitTextView.setText(String.format("%s", totalDebitBank));

		int totalCredit = 0;
		for (int i = 0; i < arrayList.size(); i++)
		{
			int amount = arrayList.get(i).getAmount();
			if(amount < 0)
				totalCredit += arrayList.get(i).getAmount();
		}

		totalCredit = Math.abs(totalCredit);

		totalCreditTextView.setText(String.format("%s", totalCredit));

		balanceTextView.setText((String.format("%s",(totalCredit - totalDebitCash))));
	}

	public void onDelete(Particular particular)
	{
		particularToDelete = particular;
		showAlert();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_spinner, menu);
		setMonthSelectSpinner(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId()) {
			case R.id.addBackDateEntry:
				if(currentUser.isAdmin())
					setBackDateEntryDialogue();
				else
					showAlert();
				return true;
			case R.id.menu_summary:
				setSummaryView();
				return true;
			case R.id.generate_report:
				generateReport();
			case R.id.menu_logout:
				logout();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void generateReport()
	{
		if(ContextCompat.checkSelfPermission(this ,"android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)
		{
			requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"},110);
		}
		else
		{
			Utility.createPDF();
		}
	}

	private void logout()
	{
		AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>()
		{
			@Override
			public void onComplete(@NonNull Task<Void> task)
			{
				if(task.isSuccessful())
				{
					startActivity(new Intent(MainActivity.instance, LoginActivity.class));
					finish();
				}
				else
				{
					Utility.d(task.getException().toString());
				}
			}
		});
	}

	private void setSummaryView()
	{
		Intent summaryActivity = new Intent(this, SummaryActivity.class);
		startActivity(summaryActivity);
	}

	private void setBackDateEntryDialogue()
	{
		customDateEntryDialogFragment.show(getSupportFragmentManager(),null);
	}

	private void setMonthSelectSpinner(Menu menu)
	{
		MenuItem item = menu.findItem(R.id.selectMonthSpinner);
		monthSelectSpinner = (Spinner) item.getActionView();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, Utility.getMonthsList());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		monthSelectSpinner.setAdapter(adapter);
		monthSelectSpinner.setSelection(Utility.getSelectedMonth());

		monthSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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

		monthSelectSpinner.setBackgroundColor(Color.parseColor("#b39ddb"));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == 110)
		{
			Utility.createPDF();
		}
	}
}