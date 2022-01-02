package com.iq.pettycash;

public class User
{
	private String contactNumber;
	private boolean isAdmin;
	private String uid;

	public User()
	{

	}

	public User(String contactNumber, boolean isAdmin, String uid)
	{
		this.contactNumber = contactNumber;
		this.isAdmin = isAdmin;
		this.uid = uid;
	}

	public String getContactNumber()
	{
		return contactNumber;
	}

	public void setContactNumber(String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	public boolean isAdmin()
	{
		return isAdmin;
	}

	public void setAdmin(boolean admin)
	{
		isAdmin = admin;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}
}
