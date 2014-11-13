package com.example.samplews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

//import com.example.samplews.MainActivity.AsyncTaskRunner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class SegundaActivity extends Activity {
	
	private static String token = null;
	TextView tvMostraToken;
	static SoapSerializationEnvelope result = null;
	private static ArrayAdapter<String> arrayAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.segunda_activity);
		recuperaToken();
		Fragment fragment = new SegundaTelaFragment();
		if(savedInstanceState == null){
			getFragmentManager().beginTransaction()
            .add(R.id.container, new SegundaTelaFragment())
            .commit();
		}
		//tvMostraToken = (TextView) findViewById(R.id.textView1);
		//tvMostraToken.setText(token);
	}
	
	public void recuperaToken(){
		SharedPreferences prefs = getSharedPreferences (MainActivity.APP_PREFS, MODE_PRIVATE);
		token = prefs.getString(MainActivity.USERNAME_TOKEN, null ) ;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static class SegundaTelaFragment extends Fragment{
		
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			AsyncTaskRunner2 runner = new AsyncTaskRunner2();
			runner.execute(); 
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			//return super.onCreateView(inflater, container, savedInstanceState);
			/*String [] teste = {
					"Um", "Dois", "Três"
			};*/
			
			List<String> list = new ArrayList<String>(/*Arrays.asList(teste)*/);
			
			//
			if(result != null){
				SoapObject resultSOAP = (SoapObject) /*(SoapPrimitive)*/( ((SoapObject) result.bodyIn)
						.getProperty(0));
				String resp = resultSOAP.getPropertyAsString(0);
				Log.v("TESTE",resp);
				
			}else{
				Log.v("TESTE","Nulo");
			}
			
			//
			
			View rootView = inflater.inflate(R.layout.fragment_segunda_tela, 
					container, false);
			arrayAdapter = new ArrayAdapter<String>(getActivity(),
					R.layout.list_item_segunda_tela, 
					R.id.list_item_segunda_tela_textview,
					list);
			ListView listView = (ListView) rootView.findViewById(
					R.id.listview_teste);
			listView.setAdapter(arrayAdapter);
			return rootView;
		}

		
		
		
		
	}
	
	
	
	
	
	
	
	private static class AsyncTaskRunner2 extends AsyncTask<String, String, SoapSerializationEnvelope> {

		private String resp;
		private static final String METHOD = "listarFila";

		@Override
		protected SoapSerializationEnvelope doInBackground(String... params) {
			publishProgress("Carregando conteúdos..."); // Calls onProgressUpdate()
			SoapSerializationEnvelope envelope = null;
			try {
				// SoapEnvelop.VER11 is SOAP Version 1.1 constant
				envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				SoapObject request = new SoapObject(MainActivity.NAMESPACE, METHOD);
				//bodyOut is the body object to be sent out with this envelope
				
				//Adicionando propriedades
				PropertyInfo pi1 = new PropertyInfo();
				 pi1.setName("token");
				 pi1.setValue(token);//get the string that is to be sent to the web service
				 pi1.setType(String.class);
				
				 request.addProperty(pi1);
				 
				//Adicionando propriedades 
				 
				//request.addProperty("login", "MEDICWARE");
				//request.addProperty("senha", "1");
				Log.v("TESTE",request.toString());
				envelope.dotNet = true;
				envelope.bodyOut = request;
				HttpTransportSE transport = new HttpTransportSE(MainActivity.URL);
				try {
					transport.call(MainActivity.NAMESPACE + MainActivity.SOAP_ACTION_PREFIX + METHOD, envelope);
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
		protected void onPostExecute(SoapSerializationEnvelope resultAux) {
			// execution of result of Long time consuming operation
			// In this example it is the return value from the web service
			//textView.setText(result);
			//login(result); 
			result = resultAux;
			SoapObject resultSOAP = (SoapObject) /*(SoapPrimitive)*/( ((SoapObject) result.bodyIn)
					.getProperty(0));
			SoapObject resp = (SoapObject) resultSOAP.getProperty(1);
			//
			arrayAdapter.clear();
			String[] codigos = new String[resp.getPropertyCount()];
			for (int i = 0; i < codigos.length; i++) {
	            SoapObject pii = (SoapObject)resp.getProperty(i);
	            String category = pii.getProperty(0).toString();
	            codigos[i] = category;
	            Log.v("TESTE","Código: "+codigos[i]);
	        }
			arrayAdapter.addAll(codigos);
			//
			Log.v("TESTE",""+resp.getPropertyCount());
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
			//textView.setText(text[0]);
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}

}
