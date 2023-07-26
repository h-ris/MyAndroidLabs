package algonquin.cst2335.xu000285;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    RecyclerView recyclerView;
    Toolbar myToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //recyclerView = binding.recycleView;
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        messagesList = chatModel.messages.getValue();
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myToolBar = binding.myToolbar;


        // add a tool bar
        setSupportActionBar(myToolBar);


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

        chatModel.selectedMessage.observe(this, (newValue) -> {
            MessageDetailsFragment chatFragment = new MessageDetailsFragment( newValue );

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentLocation, chatFragment)
                    .commit();
        });
    }


    //represents everything that goes on a row in the list, maintain variables for what to set on each row

    ChatMessage selected;
    int position;
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(click -> {

                position = getAbsoluteAdapterPosition();
                selected = messagesList.get(position);

                chatModel.selectedMessage.postValue(selected);

            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String message = "Version 1.0, created by Huixin Xu";
        if ( item.getItemId() == R.id.item_delete){

            AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
            builder.setMessage("Do you want to delete the message? ")
                    .setTitle("Question")
                    //Show "Yes / No" button, click "Yes" to delete the message
                    .setNegativeButton("No", ((dialog, clk) ->{} ))
                    .setPositiveButton("Yes",((dialog, clk) ->{


                        //delete from database
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(()->{
                            // Background thread
                            myDAO.deleteMessage(selected);
                            messagesList.remove(position);

                            runOnUiThread(()->
                                    // On UI thread update the recyclerView
                                    myAdapter.notifyItemRemoved(position));
                        });

                        Snackbar.make(recyclerView,"Message was deleted", Snackbar.LENGTH_LONG )
                                .setAction("Undo", clk2->{
                                    Executor thrd = Executors.newSingleThreadExecutor();
                                    thrd.execute(()->{ myDAO.insertMessage(selected); });
                                    messagesList.add(position,selected);
                                    runOnUiThread(()->myAdapter.notifyDataSetChanged());
                                })
                                .show();

                    } ))
                    //show the window:
                    .create().show();
        }
        else if (item.getItemId() == R.id.item_about){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        return true;
    }
}