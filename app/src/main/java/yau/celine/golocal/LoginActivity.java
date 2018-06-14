package yau.celine.golocal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import yau.celine.golocal.app.VolleySingleton;
import yau.celine.golocal.utils.SharedPrefManager;
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.User;

public class LoginActivity extends AppCompatActivity {
    private Button button_login_login;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private TextView textViewRegister;
    private String username;
    private String password;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        check if user is logged in already
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, BaseActivity.class));
            return;
        }

        editText_login_username = (EditText) findViewById(R.id.editText_login_username);
        editText_login_password = (EditText) findViewById(R.id.editText_login_password);

        button_login_login = (Button) findViewById(R.id.button_login_login);
        button_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser(){
        Map<String, String> params = new HashMap<>();
        username = editText_login_username.getText().toString();
        password = editText_login_password.getText().toString();

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

//        validate inputs
        if (TextUtils.isEmpty(username)) {
            editText_login_username.setError("Please enter username");
            editText_login_username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editText_login_password.setError("Please enter password");
            editText_login_password.requestFocus();
            return;
        }

        params.put("username", username);
        params.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URLs.URL_LOGIN, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                hideProgress();
                if (response.has("key")) {
                    try {
                        User user = new User(
                                response.getInt("user_id"),
                                response.getString("key")
                        );
//                        store user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

//                        starting second activity (main search screen)
                        finish();
                        startActivity(new Intent(getApplicationContext(), BaseActivity.class));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
                hideProgress();
                Toast.makeText(getApplicationContext(), "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void hideProgress(){
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }
}

