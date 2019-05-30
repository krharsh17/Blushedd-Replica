package in.codepredators.kumarharsh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragmentManager extends Fragment {
    private MessagesRecyclerAdapter Adapter;


    ChatFragmentManager(MessagesRecyclerAdapter Adapter){
        this.Adapter = Adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container, false);
        RecyclerView recycler = view.findViewById(R.id.ChatRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        Adapter.setRecyclerView(recycler);
        recycler.setAdapter(Adapter);
        recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if(!(rv.getScrollState()==RecyclerView.SCROLL_STATE_DRAGGING))
                    if (e.getAction() == MotionEvent.ACTION_UP)
                        Adapter.AddMessage();
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });
        return view;
    }

}
