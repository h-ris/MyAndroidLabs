package algonquin.cst2335.xu000285;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage {

    @ColumnInfo(name="Message")
    String message;

    @ColumnInfo(name="TimeSent")
    String timeSent;

    @ColumnInfo(name="IsSentButton")
    int isSentButton;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    public ChatMessage(){};

    ChatMessage(String m, String t, int sent){
        this.message = m;
        this.timeSent = t;
        this.isSentButton = sent;
    }

    public String getMessage(){
        return message;
    }

    public String getTimeSent(){
        return timeSent;
    }

    public int getIsSentButton(){
        return isSentButton;
    }
}