package com.unimib.wardrobe;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;

    public static final String TAG = LoginActivity.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.email);
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);
        editTextPassword = findViewById(R.id.password);
        TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);

        Button loginButton = findViewById(R.id.filledButton);
        loginButton.setOnClickListener(view -> {
            if(isEmailOk(editTextEmail.getText().toString())){
                emailInputLayout.setError(null);
                emailInputLayout.setHintTextColor(null);

                if (isPasswordOk(editTextPassword.getText().toString())) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setHintTextColor(null);
                }else{
                    passwordInputLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    passwordInputLayout.setError(" ");
                    passwordInputLayout.setErrorIconDrawable(null);
                    passwordInputLayout.setHintTextColor(getResources().getColorStateList(R.color.md_theme_error));
                    editTextPassword.setError("la password deve avere almeno 8 caratteri");
                }

            }else{
                emailInputLayout.setError(" ");
                emailInputLayout.setErrorIconDrawable(null);
                emailInputLayout.setHintTextColor(getResources().getColorStateList(R.color.md_theme_error));
                editTextEmail.setError("l'email non è valida");
            }
        });

    }
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }
}