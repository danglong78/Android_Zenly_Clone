package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.study.android_zenly.R;

import java.lang.reflect.Constructor;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import data.models.Message;
import data.models.User;
import ultis.DateFormatter;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseMessageViewHolder>{
        private static final int VIEW_TYPE_INCOMING_MESSAGE = 0x00;
        private static final int VIEW_TYPE_OUTCOMING_MESSAGE = 0x01;
        private static final int VIEW_TYPE_DATE_HEADER = 0x02;
        private String senderId;
        private StorageReference ref;
        private FirebaseStorage storage;
        private RecyclerView.LayoutManager layoutManager;


        private List<Wrapper> items;

        public ChatAdapter(String senderId) {
            this.senderId = senderId;
            this.items = new ArrayList<>();
            User x= new User(null,"Dang Minh Hoang Long","123","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null);
            User y= new User(null,"Ho Dai Tri","1234","0e9748c8-8978-11eb-8dcd-0242ac130003.png",null,null,null);
            items.add(new Wrapper(new Message(x,"1","Hello", Timestamp.now())));
            items.add(new Wrapper(new Message(y,"1","Hi", Timestamp.now())));
        }

        @NonNull
        @Override
        public BaseMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case VIEW_TYPE_INCOMING_MESSAGE:
                {
                    return  getHolder(parent,R.layout.left_chat_message_layout,IncomingMessageViewHolder.class);
                }
                case VIEW_TYPE_OUTCOMING_MESSAGE:
                {
                    return  getHolder(parent,R.layout.right_chat_message_layout,OutcomingMessageViewHolder.class);
                }

                case VIEW_TYPE_DATE_HEADER:
                {
                    return getHolder(parent,R.layout.item_date_header,DateHeaderViewHolder.class);
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

            holder.onBind(wrapper.item);
        }


        @Override
        public int getItemCount() {
            return items.size();
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
        private boolean isPreviousSameDate(int position, Date dateToCompare) {
            if (items.size() <= position) return false;
            if (items.get(position).item instanceof Message) {
                Date previousPositionDate = ((Message) items.get(position).item).getTime().toDate();
                return DateFormatter.isSameDay(dateToCompare, previousPositionDate);
            } else return false;
        }
        private boolean isPreviousSameAuthor(String id, int position) {
            int prevPosition = position + 1;
            if (items.size() <= prevPosition) return false;

            if (items.get(prevPosition).item instanceof Message) {
                return ((Message) items.get(prevPosition).item).getSender().getUID().contentEquals(id);
            } else return false;
        }
        @Override
        public int getItemViewType(int position) {
            Wrapper wrapper = items.get(position);
            if (wrapper.item instanceof Message) {
                Message message = (Message) wrapper.item;
                if (message.getSender().getUID().equals(senderId)) {
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
    void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

        private class Wrapper<DATA> {
            private DATA item;
            boolean isSelected;

            Wrapper(DATA item) {
                this.item = item;
            }
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

            public void onBind(Object object){

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


            public void onBind(Message message) {
                bubble.setSelected(isSelected());
                text.setText(message.getMess());
                time.setText(DateFormatter.format(message.getTime().toDate(), DateFormatter.Template.TIME));

                boolean isAvatarExists =message.getSender().getAvatar() != null && !message.getSender().getAvatarURL().isEmpty();
                userAvatar.setVisibility(isAvatarExists ? View.VISIBLE : View.GONE);
                if (isAvatarExists ) {
    //                imageLoader.loadImage(userAvatar, message.getUser().getAvatar());
                }
            }

        }
        public static class OutcomingMessageViewHolder
                extends BaseMessageViewHolder  {

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
            public void onBind(Object object) {
                Message message = (Message) object;
                bubble.setSelected(isSelected());
                text.setText(message.getMess());
                time.setText(DateFormatter.format(message.getTime().toDate(), DateFormatter.Template.TIME));

                if (userAvatar != null) {
                    boolean isAvatarExists = message.getSender().getAvatar() != null && !message.getSender().getAvatarURL().isEmpty();
                    userAvatar.setVisibility(isAvatarExists ? View.VISIBLE : View.GONE);
                    if (isAvatarExists) {
    //                    StorageReference ref= FirebaseStorage.getInstance().getReference().child("avatars").child(list.get(position).getAvatarURL());
    //                    ref.getDownloadUrl().addOnSuccessListener(uri->{
    //                        String imageURL= uri.toString();
    //                        Glide.with(context)
    //                                .load(imageURL)
    //                                .into(holder.getAvatar());

    //                    });
                    }
                }
            }


        }
        public static class DateHeaderViewHolder extends BaseMessageViewHolder{

            protected TextView text;
            protected String dateFormat;

            public DateHeaderViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.messageText);
            }
            @Override
            public void onBind(Object object) {
                Date date= (Date) object;
                text.setText(DateFormatter.format(date, dateFormat));
            }
        }
}
