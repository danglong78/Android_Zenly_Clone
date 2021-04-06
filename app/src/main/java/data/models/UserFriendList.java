package data.models;

public class UserFriendList extends User{
    protected String tag;

    public UserFriendList(User u){
        this.UID = u.UID;
        this.name = u.name;
        this.dob = u.dob;
        this.avatarURL = u.avatarURL;
        this.phone = u.phone;
        this.avatar = u.avatar;
        this.conversation = u.conversation;
        this.tag = "";
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public String getTag(){
        return this.tag;
    }
}
