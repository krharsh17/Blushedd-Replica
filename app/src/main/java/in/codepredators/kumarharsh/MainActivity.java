package in.codepredators.kumarharsh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FrameLayout Frame;
    ChatFragmentManager Chat;
    NetworkUnavailableFragmentManager NetworkUnavailable;
    MessagesRecyclerAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Frame = findViewById(R.id.Frame);
        NetworkUnavailable = new NetworkUnavailableFragmentManager(this);

        if (isNetworkAvailable()) {
            Adapter = new MessagesRecyclerAdapter(this, null, "https://api.jsonbin.io/b/5cee649e11b2240b3a916edc/1");
            Chat = new ChatFragmentManager(Adapter);
            getSupportFragmentManager().beginTransaction().add(R.id.Frame, Chat).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().add(R.id.Frame, NetworkUnavailable).commit();

            findViewById(R.id.ReceiverName).setVisibility(View.GONE);
            findViewById(R.id.SenderName).setVisibility(View.GONE);
            ((Toolbar)findViewById(R.id.MainToolbar)).setTitle("Network not available");
        }

        setSupportActionBar((Toolbar)findViewById(R.id.MainToolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void LoadChat(){
        if(Chat == null){
            Adapter = new MessagesRecyclerAdapter(this, null, "https://api.jsonbin.io/b/5cee649e11b2240b3a916edc/1");
            Chat = new ChatFragmentManager(Adapter);
        }
        if(findViewById(R.id.SenderName).getVisibility()==View.GONE){
            findViewById(R.id.ReceiverName).setVisibility(View.VISIBLE);
            findViewById(R.id.SenderName).setVisibility(View.VISIBLE);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.Frame, Chat).commit();
    }

    void LoadNetworkUnavailable(){
        getSupportFragmentManager().beginTransaction().replace(R.id.Frame, NetworkUnavailable).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
