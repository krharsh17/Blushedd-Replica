package in.codepredators.kumarharsh;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class MessageDiffCallback extends DiffUtil.Callback {

    private final ArrayList<Message> OldMessageList;
    private final ArrayList<Message> NewMessageList;

    MessageDiffCallback(ArrayList<Message> oldMessageList, ArrayList<Message> newMessageList) {
        this.OldMessageList = oldMessageList;
        this.NewMessageList = newMessageList;
    }

    @Override
    public int getOldListSize() {
        return OldMessageList.size();
    }

    @Override
    public int getNewListSize() {
        return NewMessageList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return OldMessageList.get(oldItemPosition).getMessageIndex() == NewMessageList.get(
                newItemPosition).getMessageIndex();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Message oldMessage = OldMessageList.get(oldItemPosition);
        final Message newMessage = NewMessageList.get(newItemPosition);

        return oldMessage.getMessageText().equals(newMessage.getMessageText());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}