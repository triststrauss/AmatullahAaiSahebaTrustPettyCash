package com.iq.pettycash;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Utility
{
	public static final String TOPIC_KEY = "topics";
	public static final String PARTICULAR_KEY = "particulars";
	public static final String USER_KEY = "users";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String CASH_INWARD = "CashInward";

	private static DatabaseReference mDatabase;
	private static ArrayList<String> topics;
	private static ArrayList<Particular> particularsList;

	static ArrayList<Particular> filteredParticularList;

	static ArrayList<String> monthsList;
	static ArrayList<String> yearsList;
	private static int selectedMonth;
	private static int selectedYearPosition;

	public static  ArrayList<String> getMonthsList()
	{
		return monthsList;
	}

	public static  ArrayList<String> getYearsList()
	{
		return yearsList;
	}

	public static ArrayList<Particular> getFilteredParticularList()
	{
		return filteredParticularList;
	}

	public DatabaseReference getmDatabase()
	{
		return mDatabase;
	}

	public void setmDatabase(DatabaseReference mDatabase)
	{
		this.mDatabase = mDatabase;
	}

	public static ArrayList<String> getTopics()
	{
		return topics;
	}

	public void setTopics(ArrayList<String> topics)
	{
		this.topics = topics;
	}

	public ArrayList<Particular> getParticularsList()
	{
		return particularsList;
	}

	public void setParticularsList(ArrayList<Particular> particularsList)
	{
		this.particularsList = particularsList;
	}

	public static void init()
	{
		mDatabase = FirebaseDatabase.getInstance().getReference();

		topics = new ArrayList<>();
		particularsList = new ArrayList<>();
		filteredParticularList = new ArrayList<>();
		monthsList =
		new ArrayList<>();
		monthsList.add("January");
		monthsList.add("February");
		monthsList.add("March");
		monthsList.add("April");
		monthsList.add("May");
		monthsList.add("June");
		monthsList.add("July");
		monthsList.add("August");
		monthsList.add("September");
		monthsList.add("October");
		monthsList.add("November");
		monthsList.add("December");


		yearsList = new ArrayList<>();
		yearsList.add("2020");
		yearsList.add("2021");
		yearsList.add("2022");
		yearsList.add("2023");
		yearsList.add("2024");

		setSelectedMonth(Calendar.getInstance().get(Calendar.MONTH));
		setSelectedYearPosition(yearsList.indexOf(Calendar.getInstance().get(Calendar.YEAR) + ""));

		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < yearsList.size(); i++)
		{
			if(year == Integer.parseInt(yearsList.get(i)))
			{
				Calendar.getInstance().get(i);
				return;
			}
		}
	}


	public static void readNewData()
	{
		mDatabase.addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot)
			{
				topics.clear();
				particularsList.clear();

				DataSnapshot topicSnapshot = snapshot.child(TOPIC_KEY);
				for(DataSnapshot ts : topicSnapshot.getChildren())
				{
					String topic = ts.getValue().toString();
//					d(topic);
					topics.add(topic);
				}

				DataSnapshot particularSnapshot = snapshot.child(PARTICULAR_KEY);
				for(DataSnapshot ps : particularSnapshot.getChildren())
				{
					Particular particular = ps.getValue(Particular.class);
					particularsList.add(particular);
//					d(particular.toString());
				}


				if(MainActivity.getCurrentUser() == null && FirebaseAuth.getInstance().getCurrentUser() != null)
				{
					DataSnapshot userSnapshot = snapshot.child(USER_KEY);
					for(DataSnapshot us : userSnapshot.getChildren())
					{
						User user = us.getValue(User.class);
						if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
						{
							MainActivity.setCurrentUser(user);
							break;
						}
					}
				}


				setFilteredParticularList();
				MainActivity.instance.onDataFetch();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error)
			{

			}
		});
	}

	public static void addNewParticular(Particular particular)
	{
		mDatabase.child(PARTICULAR_KEY).child(particular.getDate().toString()).setValue(particular);
	}

	public static void addNewUser(User user)
	{
		mDatabase.child(USER_KEY).child(user.getUid()).setValue(user);
	}

	public static void d(String str)
	{
		System.out.println("<IQ>"+str);
	}

	public static void deleteParticular(Particular particularToDelete)
	{
		mDatabase.child(PARTICULAR_KEY).child(particularToDelete.getDate().toString()).removeValue();
	}

	public static void setFilteredParticularList()
	{
		filteredParticularList.clear();

		Calendar calendar = Calendar.getInstance();

		for (int i = 0; i < particularsList.size(); i++)
		{
			calendar.setTimeInMillis(particularsList.get(i).getDate());
			calendar.get(Calendar.MONTH);
			if(getSelectedMonth() == calendar.get(Calendar.MONTH) && getSelectedYear() == calendar.get(Calendar.YEAR))
			{
				filteredParticularList.add(particularsList.get(i));
			}
		}
	}

	public static void setSelectedMonth(int month)
	{
		selectedMonth = month;
	}

	public static int getSelectedMonth()
	{
		return selectedMonth;
	}

	public static void setSelectedYearPosition(int year)
	{
		selectedYearPosition = year;
	}

	public static int getSelectedYearPosition()
	{
		return selectedYearPosition;
	}

	public static int getSelectedYear()
	{
		return Integer.parseInt(yearsList.get(selectedYearPosition));
	}

	public static String getDateFromTimesMili(long milliSeconds)
	{
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public static String getSelectedMonthByName()
	{
		return monthsList.get(selectedMonth);
	}

	public static void createPDF()
	{
		PdfDocument pdfDocument = new PdfDocument();

		Paint paint = new Paint();
		float defaultSize = paint.getTextSize();



		Typeface typeface = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
		paint.setTypeface(typeface);

		int width = 800;
		int height = 1200;

		PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width,height,1).create();
		PdfDocument.Page page = pdfDocument.startPage(pageInfo);
		Canvas canvas = page.getCanvas();


		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(defaultSize * 1.5f);
		paint.setColor(Color.RED);
		canvas.drawText("Amatullah Aaisaheba Memorial Trust", width/2f, 20, paint);


		paint.setTextSize(defaultSize * 1.25f);
		canvas.drawText("Petty Cash Report", width/2f, 50, paint);


		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(defaultSize);
		paint.setColor(Color.BLACK);

		canvas.drawText(Utility.getSelectedMonthByName() + " - " + Utility.getSelectedYear(), width - 100, 70, paint);

		ArrayList<Particular> arrayList = Utility.getFilteredParticularList();

		float lineY = 90 ;
		float textY = 90 + 13;
		float startX = 10;

		canvas.drawLine(startX,lineY,width - 10,lineY, paint);

		canvas.drawLine(10,lineY,10,lineY + 20, paint);

		canvas.drawText("Topic", 12, textY, paint);
		canvas.drawLine(120,lineY,120,lineY + 20, paint);

		canvas.drawText("Description", 122, textY, paint);
		canvas.drawLine(500,lineY,500,lineY + 20, paint);

		canvas.drawText("Date", 502, textY, paint);
		canvas.drawLine(600,lineY,600,lineY + 20, paint);

		canvas.drawText("Amount", 602, textY, paint);
		canvas.drawLine(700,lineY,700,lineY + 20, paint);

		canvas.drawText("Remarks", 702, textY, paint);
		canvas.drawLine(790,lineY,790,lineY + 20, paint);

		typeface = Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);
		paint.setTypeface(typeface);


		float totalAmount = 0;
		float index = 0;
		float y;

		for (int i = 0; i < arrayList.size(); i++)
		{
			Particular particular = arrayList.get(i);

			if(particular.getAmount() < 0)
			{
				continue;
			}

			totalAmount += particular.getAmount();

			lineY = 110 + (index * 20);
			textY = 110 + (index * 20) + 13;

			canvas.drawLine(startX,lineY,width - 10,lineY, paint);

			canvas.drawLine(10,lineY,10,lineY + 20, paint);

			canvas.drawText(particular.getTopic(), 12, textY, paint);
			canvas.drawLine(120,lineY,120,lineY + 20, paint);

			canvas.drawText(particular.getDescription(), 122, textY, paint);
			canvas.drawLine(500,lineY,500,lineY + 20, paint);

			canvas.drawText(Utility.getDateFromTimesMili(particular.getDate()), 502, textY, paint);
			canvas.drawLine(600,lineY,600,lineY + 20, paint);

			canvas.drawText(particular.getAmountString(), 602, textY, paint);
			canvas.drawLine(700,lineY,700,lineY + 20, paint);

			canvas.drawLine(790,lineY,790,lineY + 20, paint);

			index++;
		}

		typeface = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
		paint.setTypeface(typeface);

		canvas.drawLine(startX,lineY + 20,width - 10,lineY + 20, paint);

		lineY = 110 + (index * 20);
		textY = 110 + (index * 20) + 13;

		canvas.drawLine(10,lineY,10,lineY + 20, paint);

		canvas.drawText("Total", 12, textY, paint);
		canvas.drawLine(120,lineY,120,lineY + 20, paint);

		canvas.drawText("", 122, textY, paint);
		canvas.drawLine(500,lineY,500,lineY + 20, paint);

		canvas.drawText("", 502, textY, paint);
		canvas.drawLine(600,lineY,600,lineY + 20, paint);

		canvas.drawText(totalAmount + "", 602, textY, paint);
		canvas.drawLine(700,lineY,700,lineY + 20, paint);

		canvas.drawText("", 702, textY, paint);
		canvas.drawLine(790,lineY,790,lineY + 20, paint);

		canvas.drawLine(startX,lineY + 20,width - 10,lineY + 20, paint);


		pdfDocument.finishPage(page);


		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/aaaaa.pdf");

		try
		{
			pdfDocument.writeTo(new FileOutputStream(file));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		pdfDocument.close();
	}
}
