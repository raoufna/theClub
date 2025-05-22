package com.unimib.wardrobe.ui.welcome.fragments;

import static com.unimib.wardrobe.util.Constants.USER_COLLISION_ERROR;
import static com.unimib.wardrobe.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.model.User;
import com.unimib.wardrobe.repository.user.IUserRepository;
import com.unimib.wardrobe.ui.welcome.viewmodel.UserViewModel;
import com.unimib.wardrobe.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText editTextEmail, editTextPassword, editTextName;


    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

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
                    Navigation.findNavController(v).navigate(R.id.action_SignupFragment_to_mainActivity);
                    if(editTextName.getText().toString().isEmpty()){
                    }

                    if (!userViewModel.isAuthenticationError()) {
                        userViewModel.getUserMutableLiveData(editTextEmail.getText().toString(), editTextPassword.getText().toString(), false).observe(
                                getViewLifecycleOwner(), result -> {
                                    if (result.isSuccess()) {
                                        User user = ((Result.UserSuccess) result).getData();
                                        //saveLoginData(email, password, user.getIdToken());
                                        userViewModel.setAuthenticationError(false);
                                    } else {
                                        userViewModel.setAuthenticationError(true);
                                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                getErrorMessage(((Result.Error) result).getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        userViewModel.getUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), false);
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

                userViewModel.setAuthenticationError(true);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.error_email_login, Snackbar.LENGTH_SHORT).show();
            }

        });
        Button textButtonlog = view.findViewById(R.id.textButtonlog);
        textButtonlog.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_SignupFragment_to_loginFragment);
        });

        return view;
    }

    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_collision_user);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    /**
     * Checks if the email address has a correct format.
     * @param email The email address to be validated
     * @return true if the email address is valid, false otherwise
     */
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }

}







/*public class SignupFragment extends Fragment {
    private TextInputEditText editTextEmail, editTextPassword, editTextName;

    public SignupFragment() {
        // Required empty public constructor
    }


    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
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
                    Navigation.findNavController(v).navigate(R.id.action_SignupFragment_to_mainActivity);
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
            Navigation.findNavController(v).navigate(R.id.action_SignupFragment_to_loginFragment);
        });
        }
    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);
    }
    private boolean isPasswordOk(String password){
        return password.length() > 7;
    }
    }

*/

