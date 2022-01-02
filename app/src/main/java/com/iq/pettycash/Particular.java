package com.iq.pettycash;

public class Particular
{
	public static final String CASH = "cash";
	public static final String BANK = "bank";

	private String topic;
	private int amount;
	private String description;
	private Long date;
	private String transactionType;

	public Particular()
	{

	}

	public Particular(String topic, String description,  int amount,Long date, String transactionType)
	{
		this.topic = topic;
		this.amount = amount;
		this.description = description;
		this.date = date;
		this.transactionType = transactionType;
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public int getAmount()
	{
		return amount;
	}

	public String getAmountString()
	{
		return amount+"";
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Long getDate()
	{
		return date;
	}

	public void setDate(Long date)
	{
		this.date = date;
	}

	@Override
	public String toString()
	{
		return "Particular{" +
				"topic='" + topic + '\'' +
				", amount=" + amount +
				", description='" + description + '\'' +
				", date=" + date +
				'}';
	}

	public String getTransactionType()
	{
		return transactionType != null ? transactionType : CASH;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}
}
