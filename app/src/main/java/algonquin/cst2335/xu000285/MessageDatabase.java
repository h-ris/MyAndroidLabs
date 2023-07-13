package algonquin.cst2335.xu000285;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// returns the DAO for interacting with this database
@Database(entities = {ChatMessage.class}, version=1)
public abstract class MessageDatabase extends RoomDatabase {
    public abstract ChatMessageDAO cmDAO();
}
