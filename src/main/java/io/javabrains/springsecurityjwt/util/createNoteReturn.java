package io.javabrains.springsecurityjwt.util;

import com.google.firebase.database.DataSnapshot;

import java.util.Iterator;

public class createNoteReturn {
    Boolean status;
    Note note;

    public createNoteReturn(Boolean status, DataSnapshot ss) {
        this.status = status;
        Iterator<DataSnapshot> i = ss.getChildren().iterator();
        String color = i.next().getValue().toString();
        String content = i.next().getValue().toString();
        String height = i.next().getValue().toString();
        String left = i.next().getValue().toString();
        String size = i.next().getValue().toString();
        String top = i.next().getValue().toString();
        String width = i.next().getValue().toString();
        this.note = new Note(ss.getKey(), content, size, color, top, left, width, height);
    }

    public Boolean getStatus() {
        return status;
    }

    public Note getNote() {
        return note;
    }
}
