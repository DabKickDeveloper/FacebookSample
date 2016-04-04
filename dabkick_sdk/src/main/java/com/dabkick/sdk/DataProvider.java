package com.dabkick.sdk;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class DataProvider {

    public boolean position;
    public String message, emojiName, profilePic, friendName;
    public int emojisPosition;
    public int count;
    public String dummyText;
    public float alpha;

    public DataProvider(String dummyText){
        super();
        this.dummyText = dummyText;

    }
    public DataProvider(boolean position, String message) {
        super();
        this.position = position;
        this.message = message;
    }
    public DataProvider(boolean position, String message, String friendName, String profilePic) {
        super();
        this.position = position;
        this.message = message;
        this.friendName = friendName;
        this.profilePic = profilePic;
    }
    public DataProvider(boolean position, String emojiName, int emojisPosition) {
        super();
        this.position = position;
        this.emojiName = emojiName;
        this.emojisPosition = emojisPosition;
    }
    public DataProvider(boolean position, String emojiName, int emojisPosition, int count) {
        super();
        this.position = position;
        this.emojiName = emojiName;
        this.emojisPosition = emojisPosition;
        this.count = count;
    }

    public DataProvider(boolean position, String emojiName, int emojisPosition, String friendName, String profilePic) {
        super();
        this.position = position;
        this.emojiName = emojiName;
        this.emojisPosition = emojisPosition;
        this.friendName = friendName;
        this.profilePic = profilePic;
    }
    public DataProvider(boolean position, String emojiName, int emojisPosition, int count, String friendName, String profilePic) {
        super();
        this.position = position;
        this.emojiName = emojiName;
        this.emojisPosition = emojisPosition;
        this.count = count;
        this.friendName = friendName;
        this.profilePic = profilePic;
    }
}