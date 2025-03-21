package com.unimib.wardrobe.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.text.Editable;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.ui.home.MainActivity;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private static final String TAG2 = LoginActivity.class.getSimpleName();

    public static final String TAG = LoginActivity.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

      /*  FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.i(TAG2,user+"");

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Non serve implementare
            }
            @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Quando l'utente inizia a digitare, riattiva l'icona
                if (passwordInputLayout.getEndIconMode() != TextInputLayout.END_ICON_PASSWORD_TOGGLE) {
                    passwordInputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Non serve implementare
            }
        });
*/
    }
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }
}