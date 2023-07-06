package algonquin.cst2335.xu000285;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import algonquin.cst2335.xu000285.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.xu000285.databinding.SentMessageBinding;
import algonquin.cst2335.xu000285.databinding.ReceiveMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    ArrayList<ChatMessage> messages = new ArrayList<>();
    ChatRoomViewModel chatModel;
    private RecyclerView.Adapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        messages = chatModel.messages.getValue();
        if(messages == null)
        {
            chatModel.messages.postValue(messages = new ArrayList<ChatMessage>());
        }


        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // send button
        binding.button.setOnClickListener(click -> {
            //messages.add(binding.editText.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            messages.add(new ChatMessage(binding.editText.getText().toString(),currentDateandTime,true));

            myAdapter.notifyItemInserted(messages.size()-1);
            //clear the previous text
            binding.editText.setText("");

        });

        //receive button
        binding.button2.setOnClickListener(click -> {
            //messages.add(binding.editText.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            messages.add(new ChatMessage(binding.editText.getText().toString(),currentDateandTime,false));

            myAdapter.notifyItemInserted(messages.size()-1);
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
                ChatMessage obj = messages.get(position);

                holder.messageText.setText(obj.getMessage());
                holder.timeText.setText(obj.getTimeSent());
            }

            @Override
            // returns an int specifying how many items to draw.
            public int getItemCount() {
                return messages.size();
            }

            // return an int to indicate which layout to load.
            public int getItemViewType(int position){
                ChatMessage chatMessage = messages.get(position);
                if(chatMessage.getIsSentButton()==true){
                    return 0;
                } else return 1;
            }
        });

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
        }
    }
}