package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import data.models.Message;
import data.models.User;
import ultis.DateFormatter;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseMessageViewHolder> {
    private static final int VIEW_TYPE_INCOMING_MESSAGE = 0x00;
    private static final int VIEW_TYPE_OUTCOMING_MESSAGE = 0x01;
    private static final int VIEW_TYPE_DATE_HEADER = 0x02;
    private final String senderId;
    private StorageReference ref;
    private FirebaseStorage storage;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;


    private final List<Wrapper> items;

    public ChatAdapter(String senderId) {
        this.senderId = senderId;
        this.items = new ArrayList<>();
//            User x= new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null);
//            User y= new User(null,"Ho Dai Tri","1234","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null);
//            items.add(new Wrapper(new Message(x,"1","Hello", Timestamp.now())));
//            items.add(new Wrapper(new Message(y,"1","Hi", Timestamp.now())));
    }

    public ChatAdapter(Context context, String senderId) {
        this.senderId = senderId;
        this.items = new ArrayList<>();
        this.context = context;
    }

    public void deleteAll(){
        items.clear();
    }

    @NonNull
    @Override
    public BaseMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_INCOMING_MESSAGE: {
                return getHolder(parent, R.layout.left_chat_message_layout, IncomingMessageViewHolder.class);
            }
            case VIEW_TYPE_OUTCOMING_MESSAGE: {
                return getHolder(parent, R.layout.right_chat_message_layout, OutcomingMessageViewHolder.class);
            }

            case VIEW_TYPE_DATE_HEADER: {
                return getHolder(parent, R.layout.item_date_header, DateHeaderViewHolder.class);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMessageViewHolder holder, int position) {
        Wrapper wrapper = items.get(position);

        if (wrapper.item instanceof Message) {
            ((BaseMessageViewHolder) holder).isSelected = wrapper.isSelected;
            //            holder.itemView.setOnLongClickListener(getMessageLongClickListener(wrapper));
            //            holder.itemView.setOnClickListener(getMessageClickListener(wrapper));
        }

        holder.onBind(wrapper.item,context);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Wrapper wrapper = items.get(position);
        if (wrapper.item instanceof Message) {
            Message message = (Message) wrapper.item;
            if (message.getSenderID().equals(senderId)) {
                return VIEW_TYPE_OUTCOMING_MESSAGE;
            } else {
                return VIEW_TYPE_INCOMING_MESSAGE;
            }
        } else {
            return VIEW_TYPE_DATE_HEADER;
        }
    }

    private <HOLDER extends BaseMessageViewHolder>
    BaseMessageViewHolder getHolder(ViewGroup parent, @LayoutRes int layout, Class<HOLDER> holderClass) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        try {
            Constructor<HOLDER> constructor = holderClass.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            HOLDER holder = constructor.newInstance(v);
            return holder;
        } catch (Exception e) {
            return null;
        }
    }

    public void addToStart(Message message, boolean scroll) {
        boolean isNewMessageToday = !isPreviousSameDate(0, message.getTime().toDate());
        if (isNewMessageToday) {
            items.add(0, new Wrapper<>(message.getTime().toDate()));
        }
        Wrapper<Message> element = new Wrapper<>(message);
        items.add(0, element);
        notifyItemRangeInserted(0, isNewMessageToday ? 2 : 1);
        if (layoutManager != null && scroll) {
            layoutManager.scrollToPosition(0);
        }
    }

    public void addToEnd(List<Message> messages, boolean reverse) {
        if (reverse) Collections.reverse(messages);

        if (!items.isEmpty()) {
            int lastItemPosition = items.size() - 1;
            Date lastItem = (Date) items.get(lastItemPosition).item;
            if (DateFormatter.isSameDay(messages.get(0).getTime().toDate(), lastItem)) {
                items.remove(lastItemPosition);
                notifyItemRemoved(lastItemPosition);
            }
        }

        int oldSize = items.size();
        generateDateHeaders(messages);
        notifyItemRangeInserted(oldSize, items.size() - oldSize);
    }

    private void generateDateHeaders(List<Message> messages) {
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            this.items.add(new Wrapper<>(message));
            if (messages.size() > i + 1) {
                Message nextMessage = messages.get(i + 1);
                if (!DateFormatter.isSameDay(message.getTime().toDate(), nextMessage.getTime().toDate())) {
                    this.items.add(new Wrapper<>(message.getTime().toDate()));
                }
            } else {
                this.items.add(new Wrapper<>(message.getTime().toDate()));
            }
        }
    }

    private boolean isPreviousSameDate(int position, Date dateToCompare) {
        if (items.size() <= position) return false;
        if (items.get(position).item instanceof Message) {
            Date previousPositionDate = ((Message) items.get(position).item).getTime().toDate();
            return DateFormatter.isSameDay(dateToCompare, previousPositionDate);
        } else return false;
    }

    private boolean isPreviousSameAuthor(DocumentReference id, int position) {
        int prevPosition = position + 1;
        if (items.size() <= prevPosition) return false;

        if (items.get(prevPosition).item instanceof Message) {
            return ((Message) items.get(prevPosition).item).getSenderID().equals(id);
        } else return false;
    }

    private void recountDateHeaders() {
        List<Integer> indicesToDelete = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            Wrapper wrapper = items.get(i);
            if (wrapper.item instanceof Date) {
                if (i == 0) {
                    indicesToDelete.add(i);
                } else {
                    if (items.get(i - 1).item instanceof Date) {
                        indicesToDelete.add(i);
                    }
                }
            }
        }

        Collections.reverse(indicesToDelete);
        for (int i : indicesToDelete) {
            items.remove(i);
            notifyItemRemoved(i);
        }
    }

    void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public static abstract class BaseMessageViewHolder extends RecyclerView.ViewHolder {

        private boolean isSelected;

        /**
         * Callback for implementing images loading in message list
         */
        //        protected ImageLoader imageLoader;
        public BaseMessageViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Make message unselected
         *
         * @return weather is item selected.
         */
        public boolean isSelected() {
            return isSelected;
        }

        public void onBind(Object object,Context context) {

        }


    }

    public static class IncomingMessageViewHolder extends BaseMessageViewHolder {

        protected ViewGroup bubble;
        protected TextView text;
        protected TextView time;
        protected ImageView userAvatar;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            time = (TextView) itemView.findViewById(R.id.messageTime);
            userAvatar = (ImageView) itemView.findViewById(R.id.avatar);
        }

        @Override
        public void onBind(Object object,Context context) {
            Message message= (Message) object;
            bubble.setSelected(isSelected());
            text.setText(message.getMess());
            time.setText(DateFormatter.format(message.getTime().toDate(), DateFormatter.Template.TIME));
            String sender = message.getSenderID();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(sender);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String avtURL = documentSnapshot.getData().get("avataURL").toString();
                    boolean isAvatarExists =!avtURL.isEmpty();
                    if (isAvatarExists ) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatars").child(avtURL);
                        if (ref != null) {
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageURL = uri.toString();
                                Glide.with(context)
                                        .load(imageURL)
                                        .into(userAvatar);
                            });
                        }
                    }
                }
            });
        }

    }

    public static class OutcomingMessageViewHolder
            extends BaseMessageViewHolder {

        protected ViewGroup bubble;
        protected TextView text;
        protected TextView time;
        protected ImageView userAvatar;

        public OutcomingMessageViewHolder(View itemView) {
            super(itemView);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            time = (TextView) itemView.findViewById(R.id.messageTime);
            userAvatar = (ImageView) itemView.findViewById(R.id.avatar);
        }

        @Override
        public void onBind(Object object,Context context) {
            Message message = (Message) object;
            bubble.setSelected(isSelected());
            text.setText(message.getMess());
            time.setText(DateFormatter.format(message.getTime().toDate(), DateFormatter.Template.TIME));


        }


    }

    public static class DateHeaderViewHolder extends BaseMessageViewHolder {

        protected TextView text;
        protected String dateFormat;

        public DateHeaderViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.messageText);
            dateFormat = DateFormatter.Template.STRING_MONTH.get();
        }

        @Override
        public void onBind(Object object,Context context) {
            Date date = (Date) object;
            Log.d("DATE",date.toString());
            text.setText(DateFormatter.format(date, dateFormat));
        }
    }

    private class Wrapper<DATA> {
        boolean isSelected;
        private final DATA item;

        Wrapper(DATA item) {
            this.item = item;
        }
    }
}
