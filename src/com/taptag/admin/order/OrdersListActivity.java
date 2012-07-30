package com.taptag.admin.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonRootName;

import com.taptag.admin.R;
import com.taptag.admin.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class OrdersListActivity extends Activity {

	public final String ROOT = "http://taptag.herokuapp.com";
	public final String ORDERS =  "/orders.json";
	public final String JSON = "application/json";
	
	private TextView titleView;
	private ListView ordersListView;
	private AlphabeticalOrderAdapter adapter;
	private Order[] allData;
	private ProgressDialog loading;
	private boolean ordersLoaded;
	private Date lastOrdersLoaded;
	private HttpClient httpClient;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderslist);
        
        ordersListView = (ListView) findViewById(R.id.ordersList);
		titleView = (TextView) findViewById(R.id.ordersListTitle);
		ordersLoaded = false;
		lastOrdersLoaded = new Date(0);
		allData = new Order[0];
		httpClient = new DefaultHttpClient();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	ordersLoaded = false;
    }
    
    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && !ordersLoaded && shouldLoadOrders()) {
			showLoadingDialog();
			Thread backgroundThread = new Thread( new Runnable() {
				@Override
				public void run() {
					loadList();		
				}			
			});
			backgroundThread.run();
		}
		ordersLoaded = true;
	}
    
    public void loadList() {
    	//Execute Async Task
    	LoadOrdersTask loadOrdersTask = new LoadOrdersTask();
    	loadOrdersTask.execute(null, null, null);
    	System.out.println(allData.toString());
    }
    
    public boolean shouldLoadOrders() {
    	Date now = new Date();
    	Long twoMinutes = (long) (2 * 60 * 1000);
    	return ((now.getTime() - lastOrdersLoaded.getTime()) > twoMinutes);
    }
    
    /**
     * Get the InputStream for the Orders List
     * @return
     */
    public InputStream ordersInputStream() {
    	try {
			URI ordersUri = new URI(ROOT + ORDERS);
			HttpGet httpGet = new HttpGet(ordersUri);
			httpGet.setHeader("Accept", JSON);
			httpGet.setHeader("Content-Type", JSON);
			HttpResponse response = httpClient.execute(httpGet);
			InputStream content = response.getEntity().getContent(); 
			return content;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void printAllOrders() {
    	try {
    		InputStream is = ordersInputStream();
    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
    		String line = br.readLine();
    		while (line != null) {
    			Log.i(line, line);
    			line = br.readLine();
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Convert the inputStream into an array of orders
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public Order[] getAllOrders() {
    	InputStream ordersStream = ordersInputStream();
    	ObjectMapper om = new ObjectMapper();
    	//om.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
    	try {
    		OrdersListWrapper olw = om.readValue(ordersStream, OrdersListWrapper.class);
    		return olw.getOrders();
    	} catch (Exception e) {
    		e.printStackTrace();
    		Order[] blank = new Order[0];
    		return blank;
    	}
    }
    
    public void showLoadingDialog() {
		if (loading == null) {
			loading = ProgressDialog.show(OrdersListActivity.this, "TapTag Admin",  "Loading Orders...", true);
			loading.setCancelable(true);
		} else {
			loading.show();
		}
	}
    
    
    /**
     * AsyncTask to load all of the Orders
     * @author samstern
     */
    public class LoadOrdersTask extends AsyncTask<Void, Void, Void> {

    	@Override
		protected void onPreExecute() {
			lastOrdersLoaded = new Date();
		}

		@Override
		protected Void doInBackground(Void... params) {
			printAllOrders();
			allData = getAllOrders();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
	    	//Set array adapter
	    	adapter = new AlphabeticalOrderAdapter(OrdersListActivity.this, R.layout.orderslistitem, R.id.ordersListItemTitle, allData);
			ordersListView.setAdapter(adapter);
			//TODO configure on click action
			adapter.notifyDataSetChanged();
			loading.dismiss();
		}
    	
    }
    
    /**
     * Wrapper for the list of Orders
     * @author samstern
     */
    @JsonRootName(value = "orders")
    public static class OrdersListWrapper {
    	
    	private Order[] orders;
    	
    	public OrdersListWrapper() {
    		orders = new Order[0];
    	}
    	
    	public Order[] getOrders() {
    		return orders;
    	}
    	
    	public void setOrders(Order[] orders) {
    		this.orders = orders;
    	}
    	
    }
}