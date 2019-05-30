package in.codepredators.kumarharsh;

class Message {
    private int MessageType; // -1 for left, 0 for center and 1 for right
    private String MessageText; // To store the message
    private int MessageIndex; // To store the index of the message in the list
    private String MessageTime;

    int getMessageType() {
        return MessageType;
    }

    String getMessageText() {
        return MessageText;
    }

    int getMessageIndex() {
        return MessageIndex;
    }

    String getMessageTime(){
        return MessageTime;
    }

    void setMessageTime(String messageTime){
        MessageTime = messageTime;
    }

    Message(int MessageType, String MessageText, int MessageIndex){
        this.MessageIndex = MessageIndex;
        this.MessageText = MessageText;
        this.MessageType = MessageType;
    }
}
