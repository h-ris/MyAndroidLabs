package algonquin.cst2335.xu000285;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.xu000285.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.xu000285.databinding.SentMessageBinding;
import algonquin.cst2335.xu000285.databinding.ReceiveMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    //ArrayList<ChatMessage> messages = new ArrayList<>();
    ChatRoomViewModel chatModel;
    private RecyclerView.Adapter myAdapter;
    ArrayList<ChatMessage> messagesList = new ArrayList<>();
    ChatMessageDAO myDAO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        messagesList = chatModel.messages.getValue();
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // open a database
        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        myDAO = db.cmDAO();

        if(messagesList == null){
            chatModel.messages.postValue(messagesList = new ArrayList<ChatMessage>());

            // get all messages:
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(()-> {
                List<ChatMessage> fromDatabase = myDAO.getAllMessages();
                messagesList.addAll(fromDatabase); // add previous messages
                runOnUiThread(()->{
                    binding.recycleView.setAdapter(myAdapter);
                });
            });
        }

        // send button
        binding.button.setOnClickListener(click -> {
            String input = binding.editText.getText().toString();
            int type = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage newMessage = new ChatMessage(input,currentDateandTime,type);
            messagesList.add(newMessage);

            // insert into the database
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(new Runnable() {
                @Override
                public void run() {
                    // run on a second processor:
                    newMessage.id = myDAO.insertMessage(newMessage);//<--- returns the id
                }
            });

            myAdapter.notifyItemInserted(messagesList.size()-1);
            //clear the previous text
            binding.editText.setText("");
        });

        //receive button
        binding.button2.setOnClickListener(click -> {

            String input = binding.editText.getText().toString();
            int type = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage newMessage = new ChatMessage(input,currentDateandTime,type);
            messagesList.add(newMessage);

            // insert into the database
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(new Runnable() {
                @Override
                public void run() {
                    // run on a second processor:
                    newMessage.id = myDAO.insertMessage(newMessage);//<--- returns the id
                }
            });

            myAdapter.notifyItemInserted(messagesList.size()-1);
            //clear the previous text
            binding.editText.setText("");
        });


        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            // creates a ViewHolder object which represents a single row in the list
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflate the row layout
                if (viewType==0) {
                    SentMessageBinding bindingSend = SentMessageBinding.inflate(getLayoutInflater(),parent,false);
                    return new MyRowHolder(bindingSend.getRoot());
                }else {
                    ReceiveMessageBinding bindingReceive = ReceiveMessageBinding.inflate(getLayoutInflater(),parent,false);

                    // this will initialize the row variables:
                    return new MyRowHolder(bindingReceive.getRoot());
                }
            }

            @Override
            // initializes a ViewHolder to go at the row specified by the position parameter
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                // retrieve the object that goes in row "position" in this list
                // override the text in the rows:
                ChatMessage obj = messagesList.get(position);

                holder.messageText.setText(obj.getMessage());
                holder.timeText.setText(obj.getTimeSent());
            }

            @Override
            // returns an int specifying how many items to draw.
            public int getItemCount() {
                return messagesList.size();
            }

            // return an int to indicate which layout to load.
            public int getItemViewType(int position){
                ChatMessage chatMessage = messagesList.get(position);
                if(chatMessage.getIsSentButton()==0){
                    return 0;
                } else return 1;
            }
        });

        binding.recycleView.setAdapter(myAdapter);

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
    }


    //represents everything that goes on a row in the list, maintain variables for what to set on each row
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(click -> {

                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete the message: "+ messageText.getText())
                        .setTitle("Question")
                        .setNegativeButton("No", ((dialog, clk) ->{} ))
                        .setPositiveButton("Yes",((dialog, clk) ->{

                        //delete this row
                        int position = getAbsoluteAdapterPosition();
                        ChatMessage cm = messagesList.get(position);

                        //delete from database
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(()->{
                            myDAO.deleteMessage(cm);
                            messagesList.remove(position);
                            runOnUiThread(()->
                            myAdapter.notifyItemRemoved(position));
                        });

                Snackbar.make(messageText,"Message was deleted", Snackbar.LENGTH_LONG )
                        .setAction("Undo", clk2->{
                //reinsert the message:
                Executor thrd = Executors.newSingleThreadExecutor();
                thrd.execute(()->{ myDAO.insertMessage(cm); });
                messagesList.add(position,cm);
                runOnUiThread(()->myAdapter.notifyDataSetChanged());
                })
                .show(); // show snackbar
                }))
                //show the window:
                .create().show();

            });
        }
    }
}