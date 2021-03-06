package com.example.lenovo.osc.SupplierFunction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.osc.Main.LoginActivity;
import com.example.lenovo.osc.Order.Order;
import com.example.lenovo.osc.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class SupplierOrderProfileActivity extends ActionBarActivity {

    protected TextView tvOrderObjectID;
    protected TextView tvOrderName;
    protected TextView tvOrderQuantity;
    protected TextView tvOrderAmount;
    protected TextView tvOrderDate;
    protected TextView tvDeliverDate;
    protected ImageView ivSupplierOrderStockImage;
    protected Button bOk;

    private SharedPreferences prefs;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_order_profile);

        tvOrderObjectID = (TextView) findViewById(R.id.tvSupplierOrderProfileID);
        tvOrderName = (TextView) findViewById(R.id.tvSupplierOrderProfileName);
        tvOrderQuantity = (TextView) findViewById(R.id.tvSupplierOrderProfileQuantity);
        tvOrderAmount = (TextView) findViewById(R.id.tvSupplierOrderProfileAmount);
        tvOrderDate = (TextView) findViewById(R.id.tvSupplierOrderProfileOrderDate);
        tvDeliverDate = (TextView) findViewById(R.id.tvSupplierOrderProfileDeliverDate);
        ivSupplierOrderStockImage = (ImageView) findViewById(R.id.ivSupplierOrderStockImage);
        bOk = (Button) findViewById(R.id.bSupplierOrderOk);

        Intent i = getIntent();
        order = new Order(
                i.getStringExtra("objectID"),
                i.getStringExtra("name"),
                i.getIntExtra("quantity", 0),
                i.getDoubleExtra("amount", 0),
                i.getStringExtra("orderDate"),
                null
        );
        order.setDeliverDate(i.getStringExtra("deliverDate"));

        tvOrderObjectID.setText(order.getObjectId());
        tvOrderName.setText(order.getName());
        tvOrderQuantity.setText(String.valueOf(order.getQuantity()));
        tvOrderAmount.setText(String.valueOf(order.getAmount()));
        tvOrderDate.setText(order.getOrderDate());
        if (order.getDeliverDate() == null) {
            tvDeliverDate.setText("In Progress");
            bOk.setText("Deliver");
        } else
            tvDeliverDate.setText(order.getDeliverDate());

        //Load the selected stock's image.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CentreOrder");
        query.include("CentreStockObjectID");
        query.getInBackground(order.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Picasso.with(getApplication().getApplicationContext()).load(object.
                            getParseObject("CentreStockObjectID").getParseFile("Image").getUrl()).
                            noFade().into(ivSupplierOrderStockImage);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_supplier_order_profile, menu);
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

    public void update(View v){

        if (order.getDeliverDate() == null){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("CentreOrder");
            query.getInBackground(order.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, com.parse.ParseException e) {
                    if (e == null){

                        object.put("DeliverDate", new Date());
                        object.saveInBackground();
                        Toast.makeText(getApplicationContext(), "Order delivered.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        finish();
    }
}
