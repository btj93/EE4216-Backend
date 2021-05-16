package io.javabrains.springsecurityjwt.util;


public class Note {
    String noteId;
    String content;
    String size;
    String color;
    String top;
    String left;
    String width;
    String height;

    public Note(String noteId, String content, String size, String color, String top, String left, String width, String height) {
        this.noteId = noteId;
        this.content = content;
        this.size = size;
        this.color = color;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getContent() {
        return content;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getHeight() {
        return height;
    }

    public String getLeft() {
        return left;
    }

    public String getTop() {
        return top;
    }

    public String getWidth() {
        return width;
    }
}
