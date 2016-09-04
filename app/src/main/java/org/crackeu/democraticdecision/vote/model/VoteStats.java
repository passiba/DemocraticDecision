package org.crackeu.democraticdecision.vote.model;

import java.util.HashMap;
import java.util.Map;

//@IgnoreExtraProperties
public class VoteStats {
    /* public Long voteCount = new Long( 0);
     public Long yesVoteCount=new Long(0),noVoteCount=new Long(0);*/
    public long voteCount = 0, yesVoteCount = 0, noVoteCount = 0;
    public Map<String, String> votes = new HashMap<>();
    String uid = "statistics";
    String eucontryKey;
    String eucountry;


    public VoteStats() {
    }

    public VoteStats(String country) {
        this.eucountry = country;


    }

    public synchronized void isLeavingEuCount(boolean leaveEu) {
        if (leaveEu) {
            this.yesVoteCount += 1;
        } else {
            this.noVoteCount += 1;
        }
        this.voteCount += 1;

    }

    public String getEucontryKey() {
        return eucontryKey;
    }

    public void setEucontryKey(String eucontryKey) {
        this.eucontryKey = eucontryKey;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public long getYesVoteCount() {
        return yesVoteCount;
    }

    public long getNoVoteCount() {
        return noVoteCount;
    }

    public String getEucountry() {
        return eucountry;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // [START post_to_map]@Exclude

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("eucountry", uid);
        result.put("eucountry", eucountry);
        result.put("eucontryKey", eucontryKey);
       /* result.put("voteCount", String.valueOf(voteCount));
         result.put("noVoteCount",String.valueOf(noVoteCount));
        result.put("yesVoteCount",String.valueOf(noVoteCount));*/
        result.put("voteCount", voteCount);
        result.put("noVoteCount", noVoteCount);
        result.put("yesVoteCount", noVoteCount);

        return result;
    }
}
