package in.codepredators.kumarharsh;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class NetworkUnavailableFragmentManager extends Fragment {

    private Context context;

    NetworkUnavailableFragmentManager(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_network_unavailable, container, false);
        Button retry = view.findViewById(R.id.RetryButton);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    ((MainActivity)context).LoadChat();
                }
            }
        });
        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
