package com.example.lenovo.osc.SupplierFunction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lenovo.osc.Main.LoginActivity;
import com.example.lenovo.osc.Order.OrderAdapter;
import com.example.lenovo.osc.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class SupplierOrderListActivity extends ActionBarActivity {

    private ListView lvSupplierOrderList;
    private ProgressDialog mProgressDialog;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_order_list);

        lvSupplierOrderList = (ListView) findViewById(R.id.lvSupplierOrderList);
        new LoadOrderFromParse().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_supplier_order_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("userId", "").commit();
            prefs.edit().putString("loginState", "false").commit();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadOrderFromParse extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(SupplierOrderListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("OSC");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();

            defaultDisplay();
        }
    }

    /**
     * Default display of order list
     */
    public void defaultDisplay(){
        ParseObject object = ParseObject.createWithoutData("Supplier",
                LoginActivity.currentUser.getObjectID());

        final ParseQuery<ParseObject> orderQuery = new ParseQuery<ParseObject>("CentreOrder");
        orderQuery.include("SupplierObjectId");
        orderQuery.include("CentreStockObjectID");
        orderQuery.include("StaffObjectID");
        orderQuery.whereEqualTo("ReceiveDate", null);
        orderQuery.whereEqualTo("DeliverDate", null);
        orderQuery.whereEqualTo("SupplierObjectId", object);
        orderQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, final ParseException e) {
                if (e == null) {
                    OrderAdapter adapterOrder =
                            new OrderAdapter(SupplierOrderListActivity.this, objects);
                    lvSupplierOrderList.setAdapter(adapterOrder);
                    lvSupplierOrderList.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick
                                        (AdapterView<?> parent, View view, int position, long id) {
                                    //Send selected stock data to OrderProfileFragment
                                    Intent i = new Intent(SupplierOrderListActivity.this,
                                            SupplierOrderProfileActivity.class);

                                    i.putExtra("objectID", objects.get(position).getObjectId());
                                    i.putExtra("name", objects.get(position).
                                            getParseObject("CentreStockObjectID").getString("Name"));
                                    i.putExtra("quantity", objects.get(position).getInt("Quantity"));
                                    i.putExtra("amount", objects.get(position).getInt("Amount"));
                                    i.putExtra("orderDate", objects.get(position).getDate("OrderDate").
                                            toString());
                                    if (objects.get(position).getDate("DeliverDate") != null)
                                        i.putExtra("deliverDate", objects.get(position).
                                                getDate("DeliverDate").toString());

                                    startActivity(i);
                                }
                            });
                }
            }
        });
    }

    /**
     * Display pending order
     * @param v
     */
    public void pending(View v){
        defaultDisplay();
    }

    /**
     * Display delivering order
     * @param v
     */
    public void delivering(View v){
        ParseObject object = ParseObject.createWithoutData("Supplier",
                LoginActivity.currentUser.getObjectID());

        final ParseQuery<ParseObject> orderQuery = new ParseQuery<ParseObject>("CentreOrder");
        orderQuery.include("SupplierObjectId");
        orderQuery.include("CentreStockObjectID");
        orderQuery.include("StaffObjectID");
        orderQuery.whereEqualTo("ReceiveDate", null);
        orderQuery.whereNotEqualTo("DeliverDate", null);
        orderQuery.whereEqualTo("SupplierObjectId", object);
        orderQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, final ParseException e) {
                if (e == null) {
                    OrderAdapter adapterOrder =
                            new OrderAdapter(SupplierOrderListActivity.this, objects);
                    lvSupplierOrderList.setAdapter(adapterOrder);
                    lvSupplierOrderList.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick
                                        (AdapterView<?> parent, View view, int position, long id) {
                                    //Send selected stock data to OrderProfileFragment
                                    Intent i = new Intent(SupplierOrderListActivity.this,
                                            SupplierOrderProfileActivity.class);

                                    i.putExtra("objectID", objects.get(position).getObjectId());
                                    i.putExtra("name", objects.get(position).
                                            getParseObject("CentreStockObjectID").getString("Name"));
                                    i.putExtra("quantity", objects.get(position).getInt("Quantity"));
                                    i.putExtra("amount", objects.get(position).getInt("Amount"));
                                    i.putExtra("orderDate", objects.get(position).getDate("OrderDate").
                                            toString());
                                    if (objects.get(position).getDate("DeliverDate") != null)
                                        i.putExtra("deliverDate", objects.get(position).
                                                getDate("DeliverDate").toString());

                                    startActivity(i);
                                }
                            });
                }
            }
        });
    }

    /**
     * Display completed order
     * @param v
     */
    public void completed(View v){
        ParseObject object = ParseObject.createWithoutData("Supplier",
                LoginActivity.currentUser.getObjectID());

        final ParseQuery<ParseObject> orderQuery = new ParseQuery<ParseObject>("CentreOrder");
        orderQuery.include("SupplierObjectId");
        orderQuery.include("CentreStockObjectID");
        orderQuery.include("StaffObjectID");
        orderQuery.whereNotEqualTo("DeliverDate", null);
        orderQuery.whereNotEqualTo("ReceiveDate", null);
        orderQuery.whereEqualTo("SupplierObjectId", object);
        orderQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, final ParseException e) {
                if (e == null) {
                    OrderAdapter adapterOrder =
                            new OrderAdapter(SupplierOrderListActivity.this, objects);
                    lvSupplierOrderList.setAdapter(adapterOrder);
                    lvSupplierOrderList.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick
                                        (AdapterView<?> parent, View view, int position, long id) {
                                    //Send selected stock data to OrderProfileFragment
                                    Intent i = new Intent(SupplierOrderListActivity.this,
                                            SupplierOrderProfileActivity.class);

                                    i.putExtra("objectID", objects.get(position).getObjectId());
                                    i.putExtra("name", objects.get(position).
                                            getParseObject("CentreStockObjectID").getString("Name"));
                                    i.putExtra("quantity", objects.get(position).getInt("Quantity"));
                                    i.putExtra("amount", objects.get(position).getInt("Amount"));
                                    i.putExtra("orderDate", objects.get(position).getDate("OrderDate").
                                            toString());
                                    if (objects.get(position).getDate("DeliverDate") != null)
                                        i.putExtra("deliverDate", objects.get(position).
                                                getDate("DeliverDate").toString());

                                    startActivity(i);
                                }
                            });
                }
            }
        });
    }
}
