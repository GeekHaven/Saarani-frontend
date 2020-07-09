package com.example.calendarapp;

public class ListItems {
    private String name;
    private String desc;
    private String byName;
    private String date;
    private String time;
    private String venue;
    private String marker;
    private String eventId;
    private String photoUrl;
    private boolean showMenu;

    public ListItems( String name,String desc,String byName,String date,String time,String venue,String marker,String eventId){
        this.name=name;
        this.desc=desc;
        this.byName=byName;
        this.date=date;
        this.time=time;
        this.venue=venue;
        this.marker=marker;
        this.eventId=eventId;
        this.showMenu=false;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
    public boolean isShowMenu() {
        return showMenu;
    }

    public String getName(){
        return name;
    }
    public String getDesc(){
        return desc;
    }
    public String getByName(){return byName;}
    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
    public String getVenue(){
        return venue;
    }
    public String getMarker(){return marker;}
    public String getEventId(){return eventId;}
}