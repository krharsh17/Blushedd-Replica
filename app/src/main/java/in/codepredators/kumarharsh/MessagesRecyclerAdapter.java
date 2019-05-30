package in.codepredators.kumarharsh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder> {
    private ArrayList<Message> Data;
    private ArrayList<Message> MessageStash;
    private int shownMessagesIndex;
    private Context context;
    private RecyclerView recyclerView;
    private Bitmap Image;
    private String MessageJSONApi;

    void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MessagesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesRecyclerAdapter.ViewHolder holder, int position) {
//        holder.setIsRecyclable(false);
        Message temp = Data.get(position);
        if(temp.getMessageType()==1){
            holder.Holder.setGravity(Gravity.END);
            holder.Content.setBackground(context.getResources().getDrawable(R.drawable.round_chat_background_right));
        } else if(temp.getMessageType()==0) {
            holder.Holder.setGravity(Gravity.CENTER);
            holder.Content.setBackground(null);
        } else if (temp.getMessageType()==-1){
            holder.Holder.setGravity(Gravity.START);
            holder.Content.setBackground(context.getResources().getDrawable(R.drawable.round_chat_background_left));
        }

        if (temp.getMessageText().substring(0,5).equals("image")) {
            holder.Content.setVisibility(View.GONE);
            holder.Image.setVisibility(View.VISIBLE);
            holder.Time.setVisibility(View.GONE);
            if(Image==null){
                holder.Progress.setVisibility(View.VISIBLE);
                downloadFile(temp.getMessageText().substring(6), holder.Image, holder.Progress);
            } else {
                holder.Image.setImageBitmap(Image);
            }
        } else {
            holder.Content.setVisibility(View.VISIBLE);
            holder.Image.setVisibility(View.GONE);
            holder.setIsRecyclable(true);
            if(temp.getMessageType()==0){
                holder.Time.setVisibility(View.GONE);
                holder.Content.setTextColor(Color.parseColor("#FFFFFF"));
                holder.Content.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                holder.Time.setVisibility(View.VISIBLE);
                holder.Content.setTextColor(Color.parseColor("#000000"));
                holder.Content.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }
            holder.Content.setText(temp.getMessageText());
            holder.Time.setText(temp.getMessageTime());
        }

    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    MessagesRecyclerAdapter(Context context, RecyclerView recyclerView, String JSONApi) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.MessageJSONApi = JSONApi;
        Data = new ArrayList<>();
        initializeMessageStash();
    }

    private void initializeMessageStash() {
        shownMessagesIndex = 0;
        MessageStash = new ArrayList<>();
        if (isNetworkAvailable()) {
            new JsonTask().execute(MessageJSONApi);
        }
    }

    void AddMessage() {
        if (isNetworkAvailable()) {
            if (shownMessagesIndex >= MessageStash.size()) {
                return;
            }
            if (MessageStash.size() == 0)
                initializeMessageStash();
            ArrayList<Message> tempList = new ArrayList<>(Data);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
            Message tempMessage = MessageStash.get(shownMessagesIndex);
            tempMessage.setMessageTime(String.valueOf(df.format(Calendar.getInstance().getTime())));
            tempList.add(tempMessage);
            updateRecycler(tempList);
            recyclerView.smoothScrollToPosition(shownMessagesIndex);
            shownMessagesIndex++;
        } else {
            ((MainActivity) context).LoadNetworkUnavailable();
        }
    }

    private void updateRecycler(ArrayList<Message> messages) {
        final MessageDiffCallback diffCallback = new MessageDiffCallback(this.Data, messages);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.Data.clear();
        this.Data.addAll(messages);
        diffResult.dispatchUpdatesTo(this);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout Holder;
        TextView Content;
        TextView Time;
        ImageView Image;
        ProgressBar Progress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Holder = itemView.findViewById(R.id.MessageHolder);
            Content = itemView.findViewById(R.id.MessageText);
            Time = itemView.findViewById(R.id.MessageTime);
            Image = itemView.findViewById(R.id.MessagePhoto);
            Progress = itemView.findViewById(R.id.ProgressDownload)
;        }
    }

    @SuppressLint("StaticFieldLeak")
    class JsonTask extends AsyncTask<String, String, JSONObject> {

        protected JSONObject doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String tempLine;

                while ((tempLine = reader.readLine()) != null) {
                    buffer.append(tempLine).append("\n");
                    Log.i("DEBUG", tempLine);
                }
                try {
                    JSONObject Data = new JSONObject(buffer.toString());
                    JSONObject Messages = Data.getJSONObject("2");
                    Log.i("DEBUG", Messages.getJSONObject("1").getString("messageText"));
                    for (int i = 0; i < Messages.length(); i++) {
                        JSONObject tempMessageJSON = Messages.getJSONObject("" + i);
                        Message tempMessage = new Message(tempMessageJSON.getInt("messageType"), tempMessageJSON.getString("messageText"), i);
                        MessageStash.add(i, tempMessage);
                    }

                    return new JSONObject(buffer.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                ((TextView) ((MainActivity) context).findViewById(R.id.ReceiverName)).setText(jsonObject.getJSONObject("1").getString("0"));
                ((TextView) ((MainActivity) context).findViewById(R.id.SenderName)).setText(jsonObject.getJSONObject("1").getString("1"));
                ((Toolbar) ((MainActivity) context).findViewById(R.id.MainToolbar)).setTitle(jsonObject.getString("0"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            super.onPostExecute(jsonObject);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void downloadFile(final String url, final ImageView imageView, final ProgressBar progressBar) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Bitmap, Bitmap, Bitmap> DownloadImage = new AsyncTask<Bitmap, Bitmap, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Bitmap... integers) {
                try {
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Image = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
                    return Image;

                } catch (FileNotFoundException e) {
                    return null; // swallow a 404
                } catch (IOException e) {
                    return null; // swallow a 404
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }

        };

        DownloadImage.execute();

    }
}
