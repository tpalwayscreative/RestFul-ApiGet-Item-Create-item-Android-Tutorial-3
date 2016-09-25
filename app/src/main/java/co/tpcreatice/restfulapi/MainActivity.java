package co.tpcreatice.restfulapi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pre ;
    private SharedPreferences.Editor editor ;
    private ListView lv ;
    private EditText edtHome ;
    private ImageButton btnAction ;
    private ArrayAdapter<Product> adapter ;
    private ArrayList<Product> listProduct ;
    private String apiKey = "";
    private  String TAG = "show";
    private String items = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv_Home);
        edtHome = (EditText) findViewById(R.id.edt_Home);
        btnAction = (ImageButton) findViewById(R.id.btnAction);
        listProduct = new ArrayList<>();
        adapter = new ArrayAdapter<Product>(MainActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,listProduct);
        lv.setAdapter(adapter);

        try {

            pre = getSharedPreferences("store",MODE_PRIVATE);
            String name =  pre.getString("name",null);
            apiKey = pre.getString("apiKey",null);
            if (name.equals(""))
            {
                Intent i = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(i);
            }
            else
            {
                /*Excuse ListView Here */

                new AsyncTaskProduct().execute("http://tpalwayscreative.esy.es/task_manager/v1/tasks");

            }
        }
        catch (Exception e){

            Log.d(TAG,e.getMessage());

             Intent i = new Intent(getApplicationContext(),SignInActivity.class);
             startActivity(i);
        }

        edtHome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {

                    items = edtHome.getText().toString();

                    if(!items.equals("")){
                        new AsyncAddItems().execute("http://tpalwayscreative.esy.es/task_manager/v1/tasks");
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Please enter item here",Toast.LENGTH_SHORT).show();
                    }
                    edtHome.setText("");

                    return true;
                }
                return false;
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                items = edtHome.getText().toString();

                if(!items.equals("")){
                    new AsyncAddItems().execute("http://tpalwayscreative.esy.es/task_manager/v1/tasks");
                }
                else{
                    Toast.makeText(MainActivity.this,"Please enter item here",Toast.LENGTH_SHORT).show();
                }
                edtHome.setText("");


            }
        });


    }

    /* This is get product item from service */

    public class AsyncTaskProduct extends AsyncTask<String,String,String> {

        private ProgressDialog pDialog ;
        public AsyncTaskProduct(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(MainActivity.this.getResources().getString(R.string.txt_Show));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {


            RequestParams params = new RequestParams();
            params.add("task","");

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization",apiKey);
            client.get(strings[0], new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);

                    try {

                        JSONObject json = new JSONObject(content);

                        if (!json.getBoolean("error"))
                        {
                            JSONArray jsonArray = json.getJSONArray("tasks");

                            for(int i = 0 ; i < jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                int id = object.getInt("id");
                                String task = object.getString("task");
                                int status = object.getInt("status");
                                String created_at = object.getString("created_at");
                                Product product = new Product();
                                product.setId(id);
                                product.setTask(task);
                                product.setStatus(status);
                                product.setCreated_at(created_at);
                                listProduct.add(product);

                            }

                        }
                        else
                        {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  Toast.makeText(getApplicationContext(),"No Found", Toast.LENGTH_SHORT).show();
                              }
                          });
                        }

                    }
                    catch(final JSONException e){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                @Override
                public void onFailure(Throwable error, final String content) {
                    super.onFailure(error);

                            try {

                                JSONObject json = new JSONObject(content);
                                Toast.makeText(MainActivity.this,json.toString(),Toast.LENGTH_SHORT).show();
                            }
                            catch (JSONException e){

                            }
                }

                @Override
                public void onStart() {
                    super.onStart();

                }

                @Override
                public void onFinish() {
                    super.onFinish();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();
                            pDialog.dismiss();

                        }
                    });

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


  /* This is get product item from service */

    public class AsyncAddItems extends AsyncTask<String,String,String> {

        private ProgressDialog pDialog ;
        public AsyncAddItems(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(MainActivity.this.getResources().getString(R.string.txt_Show));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {


            RequestParams params = new RequestParams();
            params.add("task",items);

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization",apiKey);
            client.post(strings[0], params,new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);

                    try {

                        JSONObject json = new JSONObject(content);

                        if (!json.getBoolean("error"))
                        {
                           int task_id = json.getInt("task_id");
                           Product product = new Product();
                            product.setId(task_id);
                            product.setTask(items);
                            listProduct.add(product);

                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"No Found", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                    catch(final JSONException e){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                @Override
                public void onFailure(Throwable error, final String content) {
                    super.onFailure(error);

                    try {

                        JSONObject json = new JSONObject(content);
                        Toast.makeText(MainActivity.this,json.toString(),Toast.LENGTH_SHORT).show();
                    }
                    catch (JSONException e){

                    }
                }

                @Override
                public void onStart() {
                    super.onStart();

                }

                @Override
                public void onFinish() {
                    super.onFinish();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();
                            pDialog.dismiss();

                        }
                    });

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }



}
