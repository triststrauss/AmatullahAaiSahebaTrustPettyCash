package com.iq.pettycash;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SummaryListAdapter extends RecyclerView.Adapter<SummaryListAdapter.ViewHolder>
{
	private ArrayList<Particular> particularList;

	@NonNull
	@Override
	public SummaryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		View listView = inflater.inflate(R.layout.summary_list_item,parent,false);

		ViewHolder viewHolder = new ViewHolder(listView);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull SummaryListAdapter.ViewHolder holder, int position)
	{
		if(position == 0)
		{
			holder.topicTextView.setText("Category");
			holder.cashAmountTextView.setText("Cash");
			holder.bankAmountTextView.setText("Bank");

			holder.itemView.setBackgroundColor(Color.parseColor("#c7a4ff"));
			return;
		}
		else if(position == getItemCount() - 1)
		{
			ArrayList<Particular> filteredList = Utility.getFilteredParticularList();

			int totalCashAmount = 0;
			int totalBankAmount = 0;
			for (int i = 0; i < filteredList.size(); i++)
			{
				int amount = filteredList.get(i).getAmount();
				if(filteredList.get(i).getTransactionType().equals(Particular.BANK))
				{
					totalBankAmount += filteredList.get(i).getAmount();
				}
				else
				{
					if(amount >= 0)
						totalCashAmount += filteredList.get(i).getAmount();
				}

			}

			holder.topicTextView.setText("Total");
			holder.cashAmountTextView.setText(totalCashAmount + "");
			holder.bankAmountTextView.setText(totalBankAmount + "");

			holder.itemView.setBackgroundColor(Color.parseColor("#c7a4ff"));
			return;
		}

		String topic = Utility.getTopics().get(position - 1);

		holder.topicTextView.setText(topic);
		int cashTotal = 0;
		int bankTotal = 0;
		for (int i = 0; i < particularList.size(); i++)
		{
			if(isSimilarTopic(particularList.get(i).getTopic(),topic))
			{
				if(particularList.get(i).getTransactionType().equals(Particular.BANK))
					bankTotal += particularList.get(i).getAmount();
				else
					cashTotal += particularList.get(i).getAmount();
			}
		}

		holder.cashAmountTextView.setText(Math.abs(cashTotal)+"");

		holder.bankAmountTextView.setText(Math.abs(bankTotal)+"");

		if(position % 2 == 0)
		{
			holder.itemView.setBackgroundColor(Color.parseColor("#f2f2f2"));
		}

	}

	private boolean isSimilarTopic(String topic, String topicToCheckWith)
	{
		if(topic.equals(topicToCheckWith))
			return true;

		if(topic.equals("Faltu Kharcha") && topicToCheckWith.equals("Others"))
		{
			return true;
		}
		else if(topic.equals("Add Money") && topicToCheckWith.equals("CashInWard"))
		{
			return true;
		}

		return false;
	}

	public SummaryListAdapter(ArrayList<Particular> particulars)
	{
		particularList = particulars;
	}

	@Override
	public int getItemCount()
	{
		return Utility.getTopics().size() + 2;
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		TextView topicTextView;
		TextView cashAmountTextView;
		TextView bankAmountTextView;

		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			topicTextView = itemView.findViewById(R.id.summary_topic);
			cashAmountTextView = itemView.findViewById(R.id.summary_cash_amount);
			bankAmountTextView = itemView.findViewById(R.id.summary_bank_amount);
		}
	}
}
