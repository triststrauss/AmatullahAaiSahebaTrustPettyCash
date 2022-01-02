package com.iq.pettycash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity
{
	public static final int AUTHUI_REQUEST_CODE = 11111;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		handleLoginRegister();
	}

	public void handleLoginRegister()
	{
		List<AuthUI.IdpConfig> provider = Arrays.asList(
//				new AuthUI.IdpConfig.EmailBuilder().build(),
				new AuthUI.IdpConfig.PhoneBuilder().build()
		);

		Intent intent = AuthUI.getInstance()
				.createSignInIntentBuilder()
				.setAvailableProviders(provider)
				.setAlwaysShowSignInMethodScreen(true)
				.setLogo(R.drawable.busaheba_logo)
				.build();

		startActivityForResult(intent,AUTHUI_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == AUTHUI_REQUEST_CODE)
		{
			if(resultCode == RESULT_OK)
			{
				FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
				String displayName = user.getPhoneNumber();
				Utility.d("User Logged In : " + displayName);

				if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp())
				{
					Toast.makeText(this, "Welcome" + displayName, Toast.LENGTH_SHORT).show();
					User tempUser = new User(displayName, false, user.getUid());
					MainActivity.setCurrentUser(tempUser);
					Utility.addNewUser(MainActivity.getCurrentUser());
				}
				else
				{
					Toast.makeText(this, "Welcome Back" + displayName, Toast.LENGTH_SHORT).show();
				}

				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
			}
			else
			{
				IdpResponse response = IdpResponse.fromResultIntent(data);
				if(response == null)
				{
					Utility.d("User cancelled sign in request");
				}
				else
				{
					Utility.d("Sign In error" + response.getError());
				}
			}
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}
}
