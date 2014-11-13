package com.example.samplews;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Accessing Web service in Android application 
 * @author Prabu
 * @version 1.0
 * @since SEP 10 2013
 */
public class MainActivity extends Activity {
	public final static String URL = " http://medicware.tecnova.com.br:8014/wsfilaespera/filaespera.asmx";
	public static final String NAMESPACE = "http://tempuri.org/";
	public static final String SOAP_ACTION_PREFIX = "";
	private static final String METHOD = "login";
	private TextView textView;
	//Inserido para Tela de Login
	private EditText  username=null;
	private EditText  password=null;
	private TextView attempts;
	private Button login;
	int counter = 3;
	
	final static String APP_PREFS = "app_prefs";
	final static String USERNAME_TOKEN = "token";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.test);
		//Inserido para tela de Login
		username = (EditText)findViewById(R.id.editText1);
	      password = (EditText)findViewById(R.id.editText2);
	      attempts = (TextView)findViewById(R.id.textView5);
	      attempts.setText(Integer.toString(counter));
	      login = (Button)findViewById(R.id.button1);
	      login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AsyncTaskRunner runner = new AsyncTaskRunner();
				runner.execute(); 
			}
		});
	     //Inserido para tela de Login
		/*AsyncTaskRunner runner = new AsyncTaskRunner();
		runner.execute();*/
	}
	
	public void login(SoapSerializationEnvelope envelope){
		SoapObject resultSOAP = (SoapObject) /*(SoapPrimitive)*/( ((SoapObject) envelope.bodyIn)
				.getProperty(0));
		String resp = resultSOAP.getPropertyAsString(0);
		 
		if(resp.equals("S")){
	      Toast.makeText(getApplicationContext(), "Redirecionando...", 
	      Toast.LENGTH_LONG).show();
	      textView.setText("Login bem sucedido");
	      
	      //Salvando token do login
	      SharedPreferences prefs = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE) ;
	      Editor editor = prefs.edit();
	      editor.putString(USERNAME_TOKEN, resultSOAP.getPropertyAsString(1));
	      editor.commit() ;
	      
	      //Chamando próxima Activity
	      Intent intent =  new Intent(this, SegundaActivity.class);
	      startActivity(intent);

	      
	   }	
	      else {
	    	  Toast.makeText(getApplicationContext(), "Erro no Login",
	    	  Toast.LENGTH_LONG).show();
	          attempts.setBackgroundColor(Color.RED);	
	          textView.setText("Login Mal Sucedido");
	          counter--;
	          attempts.setText(Integer.toString(counter));
	         if(counter==0){
	            login.setEnabled(false);
	       }

	   }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Private class which runs the long operation.
	 * @author Prabu 
	 * 
	 */
	private class AsyncTaskRunner extends AsyncTask<String, String, SoapSerializationEnvelope> {

		private String resp;

		@Override
		protected SoapSerializationEnvelope doInBackground(String... params) {
			publishProgress("Carregando conteúdos..."); // Calls onProgressUpdate()
			SoapSerializationEnvelope envelope = null;
			try {
				// SoapEnvelop.VER11 is SOAP Version 1.1 constant
				envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				SoapObject request = new SoapObject(NAMESPACE, METHOD);
				//bodyOut is the body object to be sent out with this envelope
				
				//Adicionando propriedades
				PropertyInfo pi1 = new PropertyInfo();
				 pi1.setName("login");
				 pi1.setValue(username.getText().toString());//get the string that is to be sent to the web service
				 pi1.setType(String.class);
				
				 request.addProperty(pi1);
				 
				PropertyInfo pi2 = new PropertyInfo();
				 pi2.setName("senha");
				 pi2.setValue(password.getText().toString());//get the string that is to be sent to the web service
				 pi2.setType(String.class);
		
				 request.addProperty(pi2);
				//Adicionando propriedades 
				//request.addProperty("login", "MEDICWARE");
				//request.addProperty("senha", "1");
				Log.v("TESTE",request.toString());
				envelope.dotNet = true;
				envelope.bodyOut = request;
				HttpTransportSE transport = new HttpTransportSE(URL);
				try {
					transport.call(NAMESPACE + SOAP_ACTION_PREFIX + METHOD, envelope);
					//Log.v("TESTE","Request Dump: "+transport.requestDump);
				} catch (IOException e) {
					e.printStackTrace();
					Log.v("TESTE", "IOException: "+e.toString());
				} catch (XmlPullParserException e) {
					e.printStackTrace();
					Log.v("TESTE", "XmlPullParserException: "+e.toString());
				}
				//bodyIn is the body object received with this envelope
				if (envelope.bodyIn != null) {
					//getProperty() Returns a specific property at a certain index.
					/*SoapPrimitive*/SoapObject resultSOAP = (SoapObject) /*(SoapPrimitive)*/( ((SoapObject) envelope.bodyIn)
							.getProperty(0));
					resp = resultSOAP.getPropertyAsString(0);
					//resp=resultSOAP.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
				Log.v("TESTE", "Teste: "+resp);
			}
			
			return envelope;//resp;
		}

		/**
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(SoapSerializationEnvelope result) {
			// execution of result of Long time consuming operation
			// In this example it is the return value from the web service
			//textView.setText(result);
			login(result); 
		}

		/**
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
		}

		/**
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... text) {
			textView.setText(text[0]);
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}
}
