package com.Spiros.Nodyssia.Cards;

import java.io.Serializable;

/**
 * Created by manel on 9/5/2017.
 */

public class cardObject implements Serializable {
    private String  userId,
                    name,
                    profileImageUrl,
                    age,
                    about,
                    job,
                    rating;
    public cardObject(String userId, String name, String age, String about, String job, String profileImageUrl,String rating){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.about = about;
        this.job = job;
        this.rating = rating;
    }

    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getAge(){
        return age;
    }
    public String getAbout(){
        return about;
    }
    public String getJob(){
        return job;
    }
    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public String getProfileRating(){ return rating; }

}
