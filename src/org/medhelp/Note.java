package org.medhelp;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 6/1/15
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Note {

    public String noteAuthor;
    public String noteDate;

    public String getNoteAuthor() {
        return noteAuthor;
    }

    public void setNoteOriginator(String noteAuthor) {
        this.noteAuthor = noteAuthor;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String printToFile() {
        String fileText = "";

        fileText = fileText + noteAuthor + "," +
                              noteDate.replace(",","") + "\n";

        return fileText;

    }
}
