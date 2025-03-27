package com.unimib.wardrobe.ui.welcome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.wardrobe.R;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SigninFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SigninFragment extends Fragment {
    private TextInputEditText editTextEmail, editTextPassword, editTextName;

    public SigninFragment() {
        // Required empty public constructor
    }


    public static SigninFragment newInstance(String param1, String param2) {
        SigninFragment fragment = new SigninFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        editTextEmail = view.findViewById(R.id.email);
        TextInputLayout passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        editTextPassword = view.findViewById(R.id.password);
        TextInputLayout emailInputLayout = view.findViewById(R.id.emailInputLayout);
        editTextName = view.findViewById(R.id.name);
        TextInputLayout nameInputLayout = view.findViewById(R.id.nameinputLayout);

        Button SignupButton = view.findViewById(R.id.SignupButton);
        SignupButton.setOnClickListener(v -> {
            if(isEmailOk(editTextEmail.getText().toString())){
                emailInputLayout.setError(null);
                emailInputLayout.setHintTextColor(null);

                if (isPasswordOk(editTextPassword.getText().toString())) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setHintTextColor(null);
                    Navigation.findNavController(v).navigate(R.id.action_signinFragment_to_mainActivity);
                    if(editTextName.getText().toString().isEmpty()){

                    }
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
        Button textButtonlog = view.findViewById(R.id.textButtonlog);
        textButtonlog.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signinFragment_to_loginFragment);
        });
        }
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }
    }



