package algonquin.cst2335.xu000285;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDAO {

    @Insert
    long insertMessage(ChatMessage m);

    @Query("Select * from ChatMessage")
    List<ChatMessage> getAllMessages();

    @Delete
    int deleteMessage(ChatMessage m);


}