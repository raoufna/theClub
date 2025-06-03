package com.unimib.wardrobe.ui.welcome.fragments;

import static com.unimib.wardrobe.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.wardrobe.util.Constants.INVALID_USER_ERROR;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.model.User;
import com.unimib.wardrobe.repository.user.IUserRepository;
import com.unimib.wardrobe.ui.home.MainActivity;
import com.unimib.wardrobe.ui.welcome.LoginActivity;
import com.unimib.wardrobe.ui.welcome.viewmodel.UserViewModel;
import com.unimib.wardrobe.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#} factory method to
 * create an instance of this fragment.
 */

public class LoginFragment extends Fragment {

    private final static String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;

    public LoginFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                activityResult -> {
                    if (activityResult.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential =
                                    oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                // 1) Prepara il credential Firebase
                                AuthCredential firebaseCredential =
                                        GoogleAuthProvider.getCredential(idToken, null);

                                // 2) Esegui il login su Firebase
                                FirebaseAuth.getInstance()
                                        .signInWithCredential(firebaseCredential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // → Qui hai finalmente il login Google + Firebase
                                                goToNextPage(requireView());
                                            } else {
                                                Snackbar.make(
                                                        requireView(),
                                                        "Autenticazione Google fallita: " +
                                                                task.getException().getMessage(),
                                                        Snackbar.LENGTH_SHORT
                                                ).show();
                                            }
                                        });
                            }
                        } catch (ApiException e) {
                            Snackbar.make(
                                    requireView(),
                                    getString(R.string.error_unexpected),
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );

    }


    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void goToNextPage(View view) {
            startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (userViewModel.getLoggedUser() != null) {
            goToNextPage(view);
        }

        editTextEmail = view.findViewById(R.id.email);
        TextInputLayout passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        editTextPassword = view.findViewById(R.id.password);
        TextInputLayout emailInputLayout = view.findViewById(R.id.emailInputLayout);

        Button SignupButton = view.findViewById(R.id.SignupButton);
        SignupButton.setOnClickListener(vv -> {
            Navigation.findNavController(vv).navigate(R.id.action_loginFragment_to_SignupFragment);
        });

        Button loginButton = view.findViewById(R.id.filledButton);
        Button loginGoogleButton = view.findViewById(R.id.SignupButtonWithGoogle);

        loginButton.setOnClickListener(v -> {
            if(isEmailOk(editTextEmail.getText().toString())){
                emailInputLayout.setError(null);
                emailInputLayout.setHintTextColor(null);

                if (isPasswordOk(editTextPassword.getText().toString())) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setHintTextColor(null);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Login riuscito, vai a MainActivity
                                    goToNextPage(v);
                                } else {
                                    // Login fallito, mostra errore
                                    String errorMessage = getErrorMessage(task.getException());
                                    Snackbar.make(v, errorMessage, Snackbar.LENGTH_SHORT).show();
                                }
                            });
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

        loginGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {

                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);

                    }


                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }

                }));


    }

    private boolean isEmailOk(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordOk(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 8;
    }
    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            return "Account non trovato.";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Password errata.";
        }
        return "Errore durante il login.";
    }
}
























/*public class LoginFragment extends Fragment {
    private TextInputEditText editTextEmail, editTextPassword;
    private SignInClient oneTapClient;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextEmail = view.findViewById(R.id.email);
        TextInputLayout passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        editTextPassword = view.findViewById(R.id.password);
        TextInputLayout emailInputLayout = view.findViewById(R.id.emailInputLayout);

        Button SignupButton = view.findViewById(R.id.SignupButton);
        SignupButton.setOnClickListener(vv -> {
            Navigation.findNavController(vv).navigate(R.id.action_loginFragment_to_signinFragment);
        });

        Button loginButton = view.findViewById(R.id.filledButton);
        loginButton.setOnClickListener(v -> {
            if(isEmailOk(editTextEmail.getText().toString())){
                emailInputLayout.setError(null);
                emailInputLayout.setHintTextColor(null);

                if (isPasswordOk(editTextPassword.getText().toString())) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setHintTextColor(null);
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
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
}*/