package au.com.holcim.holcimapp.activities;

import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import au.com.holcim.holcimapp.R;
import au.com.holcim.holcimapp.helpers.AlertDialogHelper;
import au.com.holcim.holcimapp.helpers.NavHelper;
import au.com.holcim.holcimapp.helpers.SharedPrefsHelper;
import au.com.holcim.holcimapp.network.HttpStatusException;
import butterknife.Bind;

/**
 * Created by Jovan on 21/4/17.
 */

public class BaseActivity extends AppCompatActivity {

    @Nullable
    @Bind(R.id.toolbar) public Toolbar mToolbar;

    public void handleError(Throwable t) {
        handleError(t, true, null);
    }

    public void handleError(Throwable t, boolean showInternetErrors, CoordinatorLayout coordinatorLayout) {
        if (t instanceof HttpStatusException) {
            HttpStatusException httpException = (HttpStatusException) t;
            switch(httpException.errorType) {
                case General:
                    if(showInternetErrors) {
                        if (coordinatorLayout != null) {
                            Snackbar.make(coordinatorLayout, httpException.error, Snackbar.LENGTH_LONG).show();
                        } else {
                            AlertDialogHelper.showOk(this, "Error", httpException.error);
                        }
                    } else {
                        handleCustomError(httpException.error);
                    }
                    break;
                case PinReEnterRequired:
                case PinResetRequired:
                    NavHelper.showLandingActivity(this, httpException.error);
                    break;
                case ReloginRequired:
                    SharedPrefsHelper.getInstance().removeUserCredentials();
                    NavHelper.showLandingActivity(this, httpException.error);
                    break;
            }
        } else {
            if(showInternetErrors) {
                if (coordinatorLayout != null) {
                    Snackbar.make(coordinatorLayout, "Something went wrong, please try again.", Snackbar.LENGTH_LONG).show();
                } else {
                    AlertDialogHelper.showOk(this, "Error", "Something went wrong, please try again.");
                }
            } else {
                handleCustomError("Something went wrong, please try again.");
            }
        }
    }

    public void setToolbar(String title, int image) {
        mToolbar = ((Toolbar) findViewById(R.id.toolbar));
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setLogo(image);
            getSupportActionBar().setIcon(image);
            getSupportActionBar().setTitle(title);
        }
    }

    public void setToolbar(String title, boolean showBack) {
        mToolbar = ((Toolbar) findViewById(R.id.toolbar));
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
            getSupportActionBar().setDisplayShowHomeEnabled(showBack);
            getSupportActionBar().setTitle(title);
        }
    }

    // Override to handle errors passed on by handleError when showInternetErrors == false
    public void handleCustomError(String error) {
        //To Override if needed
    }
}
