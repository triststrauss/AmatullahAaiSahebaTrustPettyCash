package com.iq.pettycash;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class MainActivityListAdapter extends RecyclerView.Adapter<MainActivityListAdapter.ViewHolder>
{
	private ArrayList<Particular> particularList;

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate the custom layout
		View listView = inflater.inflate(R.layout.list_item, parent, false);

		// Return a new holder instance
		ViewHolder viewHolder = new ViewHolder(listView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{

		// Get the data model based on position
		Particular particular = particularList.get(position);

		// Set item views based on your views and data model

		holder.topicTextView.setText(particular.getTopic());
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(particular.getDate());
		holder.descriptionTextView.setText(particular.getDescription());
		holder.amountTextView.setText(particular.getAmountString().replace("-", ""));
		holder.deleteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				System.out.println("Delete");
				MainActivity.instance.onDelete(particular);
			}
		});

		if (position % 2 == 0)
			holder.itemView.setBackgroundColor(Color.parseColor("#f2f2f2"));

		if(particular.getTransactionType().equals(Particular.BANK))
		{
			holder.amountTextView.setTextColor(Color.parseColor("#0000d6"));
		}
		else if (particular.getAmount() > 0)
		{
			holder.amountTextView.setTextColor(Color.parseColor("#d50000"));
		}
		else
		{
			holder.amountTextView.setTextColor(Color.parseColor("#2e7d32"));
		}
	}

	@Override
	public int getItemCount()
	{
		return particularList.size();
	}

	public MainActivityListAdapter(ArrayList<Particular> particulars)
	{
		particularList = particulars;
	}

	// Provide a direct reference to each of the views within a data item
	// Used to cache the views within the item layout for fast access
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		// Your holder should contain a member variable
		// for any view that will be set as you render a row
		public TextView topicTextView;
		public TextView descriptionTextView;
		public TextView amountTextView;
		public Button deleteButton;

		// We also create a constructor that accepts the entire item row
		// and does the view lookups to find each subview
		public ViewHolder(View itemView  )
		{
			// Stores the itemView in a public final member variable that can be used
			// to access the context from any ViewHolder instance.
			super(itemView);

			topicTextView = itemView.findViewById(R.id.topicTextView);
			descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
			amountTextView = itemView.findViewById(R.id.amountTextView);
			deleteButton = itemView.findViewById(R.id.deleteButton);
		}
	}
}
