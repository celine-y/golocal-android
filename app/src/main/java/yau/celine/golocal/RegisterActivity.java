package yau.celine.golocal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import yau.celine.golocal.app.SharedPrefManager;
import yau.celine.golocal.utils.URLs;
import yau.celine.golocal.utils.objects.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText editText_register_fullname,
            editText_register_username, editText_register_password1,
            editText_register_password2;
    private RadioGroup radioTypeGroup;
    private Button button_register_register;
    private TextView textViewLogin;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        check if user logged in already
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, BaseActivity.class));
            return;
        }

        editText_register_fullname = findViewById(R.id.editText_register_fullname);
        editText_register_username = findViewById(R.id.editText_register_username);
        editText_register_password1 = findViewById(R.id.editText_register_password1);
        editText_register_password2 = findViewById(R.id.editText_register_password2);
        radioTypeGroup = findViewById(R.id.radioType);

        button_register_register = findViewById(R.id.button_register_register);
        button_register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        textViewLogin = findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void registerUser() {
        final String fullname = editText_register_fullname.getText().toString().trim();
        final String username = editText_register_username.getText().toString().trim();
        final String password1 = editText_register_password1.getText().toString().trim();
        final String password2 = editText_register_password2.getText().toString().trim();

        final int customerType = R.id.radioButtonCustomer;

        Map<String, String> params = new HashMap<>();

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

//        Validations
        if(TextUtils.isEmpty(fullname)) {
            editText_register_fullname.setError("Please enter your name");
            editText_register_fullname.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(username)) {
            editText_register_username.setError("Please enter username");
            editText_register_username.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password1)) {
            editText_register_password1.setError("Please enter password");
            editText_register_password1.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password2)) {
            editText_register_password2.setError("Please confirm password");
            editText_register_password2.requestFocus();
            return;
        }

        if(!password1.matches(password2)){
            editText_register_password2.setError("Passwords do not match");
            editText_register_password2.requestFocus();
            return;
        }

        params.put("full_name", fullname);
        params.put("username", username);
        params.put("password1", password1);
        params.put("password2", password2);

        if(radioTypeGroup.getCheckedRadioButtonId() == customerType){
            params.put("is_customer", "true");
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URLs.URL_REGISTER, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                if (response.has("key")) {
                    try {
                        User user = new User(response);
//                        store user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

//                        starting second activity (main search screen)
                        finish();
                        startActivity(new Intent(getApplicationContext(), BaseActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: "+error.getMessage());
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }
}
