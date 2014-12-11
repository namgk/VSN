package com.auto.uploader;

import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.auto.vsn.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Uploader extends Activity {

	public static final String UPLOAD_DATA = "Log_Data";
	protected static final int LENGTH_SHORT = 0;
	SharedPreferences settings; 
	SharedPreferences.Editor prefEditor;
	
	Exception exception_e = null;
	Exception exception_i = null;
	String Messages = "";
	
	TextView textViewUserName = null;
	TextView textViewPassWord = null;
	Button buttonGetData = null;
	EditText editTextUserName = null;
	EditText editTextPassWord = null;
	TextView textViewFirstName = null;
	TextView textViewLastName = null;
	TextView textViewFacebookID = null;
	//TextView textViewMD5key = null;
	TextView textViewSetTime = null;
	//TextView textViewMD5data = null;
	
	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	File sdDir = android.os.Environment.getExternalStorageDirectory();

	int intFacebookID = 0;
	
	//md5
	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	
	//SHA-256
	public byte[] getHash(String password) {
	       MessageDigest digest=null;
	    try {
	        digest = MessageDigest.getInstance("SHA-256");
	    } catch (NoSuchAlgorithmException e1) {
	        e1.printStackTrace();
	    }
	       digest.reset();
	       return digest.digest(password.getBytes());
	 }
	//SHA-256 wrapper
	static String bin2hex(byte[] data) {
	    return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
	}
	
	public static File lastFileModified(String dir) {
	    File fl = new File(dir);
	    File[] files = fl.listFiles(new FileFilter() {          
	        public boolean accept(File file) {
	            return file.isFile();
	        }
	    });
	    long lastMod = Long.MIN_VALUE;
	    File latestFile = null;
	    for (File file : files) {
	        if (file.lastModified() > lastMod) {
	            latestFile = file;
	            lastMod = file.lastModified();
	        }
	    }
	    return latestFile;
	}
	
	// TODO	
	FileObserver observer = new FileObserver(lastFileModified(sdDir + "/torqueLogs/").getPath()) 
	{ // set up a file observer to watch this directory on sd card
        @Override
	    public void onEvent(int event, String file) 
	    {
	        if(event == FileObserver.MODIFY)
	        { 
	           	Log.i("ClientServerDemo", "Torque Log modified!");
	        		      
	        	UploadTorqueLog mUploadTorqueLog = new UploadTorqueLog(Uploader.this);
				mUploadTorqueLog.execute("");

	        }
        }
        
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(UPLOAD_DATA, MODE_PRIVATE);
		prefEditor = settings.edit();

		prefEditor.putInt("FacebookID", 0);
		prefEditor.putLong("LastTime", System.currentTimeMillis());
		prefEditor.commit();
				
			    
		setContentView(R.layout.uploader);
		
		buttonGetData = (Button) findViewById(R.id.buttonGetData);
		editTextUserName = (EditText) findViewById(R.id.editTextUserName);
		editTextPassWord = (EditText) findViewById(R.id.editTextPassWord);
		textViewUserName = (TextView) findViewById(R.id.textViewUserName);
		textViewPassWord = (TextView) findViewById(R.id.textViewPassWord);
		textViewFirstName = (TextView) findViewById(R.id.textViewFirstName);
		textViewLastName = (TextView) findViewById(R.id.textViewLastName);
		textViewFacebookID = (TextView) findViewById(R.id.textViewFacebookID);
		//textViewMD5key = (TextView) findViewById(R.id.textViewMD5key);
		textViewSetTime = (TextView) findViewById(R.id.textViewSetTime);
		//textViewMD5data = (TextView) findViewById(R.id.textViewMD5data);


		//Setup the Button's OnClickListener
		buttonGetData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Get the data
				DoPOSTLogin mDoPOSTLogin = new DoPOSTLogin(Uploader.this, editTextUserName.getText().toString(), editTextPassWord.getText().toString());
				mDoPOSTLogin.execute("");
				buttonGetData.setEnabled(false);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uploader, menu);
		return true;
	}
	
	public class UploadTorqueLog extends AsyncTask<String, String, Integer>{
		Context mContext = null;
		String Message = "";
		int valid = 4;
		UploadTorqueLog(Context context){
			mContext = context;
		}
		@Override
		protected Integer doInBackground(String... arg0) {

		  	settings = getSharedPreferences(UPLOAD_DATA, MODE_PRIVATE);

		  	if ((System.currentTimeMillis() - settings.getLong("LastTime", 0))>10000)
        	{
        		
        		Log.i("ClientServerDemo", "Current: " + System.currentTimeMillis());
        		Log.i("ClientServerDemo", "Last: " + settings.getLong("LastTime", 0));
        		Log.i("ClientServerDemo", "Diff: " + (System.currentTimeMillis() - settings.getLong("LastTime", 0)));
        	
	    		try{

					//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        	HttpResponse response = null;
		        	HttpClient httpclient = new DefaultHttpClient();
		            HttpPost httppost = new HttpPost("http://54.172.173.31/upload.php");
		            File LogFileLocation= lastFileModified(sdDir + "/torqueLogs/");
		            FileBody file2upload = new FileBody(LogFileLocation);
		            //StringBody FaceBookIDtemp = new StringBody("" + intFacebookID);
		            
		            //nameValuePairs.add(new BasicNameValuePair("id", "" + intFacebookID));
	
		            // MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		            
		            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		           	builder.addPart("LogFile", file2upload);
		           	
		           	HttpEntity reqEntity = builder.build();
		           	
					//reqEntity.addPart("id", FaceBookIDtemp);
					//httppost.addHeader(nameValuePairs.get(0).getName(), nameValuePairs.get(0).getValue());
					httppost.addHeader("id", "" + intFacebookID);
					
					
					//System.out.println("httppost header..." + httppost.getHeaders("id").getName().toString() + "..." + nameValuePairs.get(0).getValue().toString());
					System.out.println("httppost header..." + httppost.getLastHeader("id").getName().toString() + httppost.getLastHeader("id").getValue().toString());
					
		            httppost.setEntity(reqEntity);
	
		            System.out.println("executing request " + httppost.getRequestLine());
					
					File directory = new File(sdDir + "/torqueLogs/");
					File from      = new File(directory, lastFileModified(sdDir + "/torqueLogs/").getName());
					if (from.length() > 1000)
			        {
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
						String currentDateandTime = sdf.format(new Date());
						File to    = new File(directory, currentDateandTime + "trackLog.csv");
				        from.renameTo(to);
				        String tempMessage = "Renaming LogFile:"+ from.length();
				        publishProgress(tempMessage);
			        }
		            
					response = httpclient.execute(httppost);

		            HttpEntity resEntity = response.getEntity();
	
		            //System.out.println("receiving respond" + resEntity.toString());
		            
		            if (resEntity != null) {
		                String page = EntityUtils.toString(resEntity);
		                if (page != "")
		                {
		                	System.out.println("receiving respond: " + page.toString());
							//JSONObject jsonObject = new JSONObject(page);
							
							//Retrieve the data from the JSON object
							//intFacebookID = jsonObject.getInt("id");
							valid = 0;
		                }else 
		                {
		                	valid = 5;
		                }
						prefEditor = settings.edit();
				    	prefEditor.putInt("FacebookID", intFacebookID);
				    	prefEditor.putLong("LastTime", System.currentTimeMillis());
				    	prefEditor.commit();
				
						

        			} else valid = 2;
				}catch (Exception e){
					Log.e("ClientServerDemo", "Error: Here!", e);
					valid = 3;
					return valid;
					
				}
				
        	}else valid = 1; 
        	return valid;
		}


	     protected void onProgressUpdate(String progress) {

			Toast.makeText(mContext, progress, Toast.LENGTH_SHORT).show();

	     }

	     protected void onPostExecute(Integer mvalid){
    		if(mvalid == 0) 
	    	{
        		Toast.makeText(getApplicationContext(), "Torque Log Uploaded for ID:\n" + intFacebookID , LENGTH_SHORT).show();
        		
           	}
	    	else if(mvalid == 2)
	    	{
	    		Toast.makeText(mContext, "Upload Torque Log FAILED!", Toast.LENGTH_SHORT).show();
	    	}else if(mvalid ==1)
	    	{
	    		//Toast.makeText(getApplicationContext(), "Torque Log modified!", Toast.LENGTH_SHORT).show();
	    	}else if(mvalid ==3)
	    	{
	    		Toast.makeText(getApplicationContext(), "Exception Error!", Toast.LENGTH_SHORT).show();
	    	}else if(mvalid ==5)
	    	{
	    		Toast.makeText(getApplicationContext(), "Error Returned!", Toast.LENGTH_SHORT).show();
	    	}
    	}
	}

	public class DoPOSTLogin extends AsyncTask<String, Void, Boolean>{

		Context mContext = null;
		String strUserNameToSearch = "";
		String strKeyToSearch = "";
		
		
		
		//Result data
		String strFirstName = "";
		String strLastName = "";
		String strUserName = "";
		String strPassWord = "";
		String strMD5key = "";
		//String strMD5data = "";
		String strSetTime = "";
		

		
		DoPOSTLogin(Context context, String usernameToSearch, String keyToSearch){
			mContext = context;
			strUserNameToSearch = usernameToSearch;
			//strKeyToSearch = md5(keyToSearch + keyToSearch + keyToSearch);
			strKeyToSearch = bin2hex(getHash(keyToSearch + keyToSearch + keyToSearch)) . toLowerCase();
			
			System.out.println("sha:  " + strKeyToSearch);
			//System.out.println("md5:  " + strKeyToSearch);
		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			try{

				//Setup the parameters
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("UserNameToSearch", strUserNameToSearch));
				nameValuePairs.add(new BasicNameValuePair("KeyToSearch", strKeyToSearch));
				//Add more parameters as necessary

				//Create the HTTP request
				HttpParams httpParameters = new BasicHttpParams();

				//Setup timeouts
				HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
				HttpConnectionParams.setSoTimeout(httpParameters, 15000);			

				HttpClient httpclient = new DefaultHttpClient(httpParameters);
				//HttpPost httppost = new HttpPost("http://ec2-54-213-100-90.us-west-2.compute.amazonaws.com/login.php");
				HttpPost httppost = new HttpPost("http://54.213.100.90/login.php"); // TODO
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				String result = EntityUtils.toString(entity);
				
				//System.out.println("result: " + result);

				try{
					// Create a JSON object from the request response
					JSONObject jsonObject = new JSONObject(result);
	
					//Retrieve the data from the JSON object
					intFacebookID = jsonObject.getInt("id");
					strUserName = jsonObject.getString("un");
					strPassWord = jsonObject.getString("pw");
					strFirstName = jsonObject.getString("fn");
					strLastName = jsonObject.getString("ln");
					strMD5key = jsonObject.getString("md5hash");
					//strMD5data = jsonObject.getString("md5data");

					//Toast.makeText(mContext, "All Good", Toast.LENGTH_LONG).show();
				}catch (Exception i){
					Log.e("ClientServerDemo", "Error:", i);
					exception_i = i;
					i = null;
				}

			}catch (Exception e){
				Log.e("ClientServerDemo", "Error:", e);
				exception_e = e;
				e = null;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean valid){
			if(exception_i != null){
				strFirstName = "Login Failure!";
				strLastName = "Wrong Username/Password";
				intFacebookID = 0;
				observer.stopWatching();
				exception_i = null;

				
			}else
			{
				observer.startWatching();
				textViewSetTime.setText("Torque Log Upload frequency: every 10s after edit");
			}
			
			//Update the UI
			editTextUserName.setText(strUserName);
			textViewFirstName.setText("First Name: " + strFirstName);
			textViewLastName.setText("Last Name: " + strLastName);
			textViewPassWord.setText("Password: \n" + strPassWord);
			textViewFacebookID.setText("Facebook ID: " + intFacebookID);
			//textViewMD5key.setText("MD5 key: " + strMD5key);

			//textViewMD5data.setText("MD5 data: " + strMD5data);
			
			buttonGetData.setEnabled(true);
			if(exception_e != null){
				//Toast.makeText(getApplicationContext(), "Here!!!", LENGTH_SHORT).show();
				Toast.makeText(mContext, "e:" + exception_e.getMessage(), Toast.LENGTH_LONG).show();
				exception_e = null;
			}
		}

	}
}
