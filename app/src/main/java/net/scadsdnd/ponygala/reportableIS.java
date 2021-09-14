package net.scadsdnd.ponygala;

import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;

class reportableIS extends InputStream{

    public int bytesRead;
    private InputStream inStream;
    private int TotalSize;
    private ProgressBar outViewP;

    public reportableIS(InputStream in, ProgressBar outProg, int totalSize) {
        this.inStream = in;
        this.TotalSize = totalSize;
        this.outViewP = outProg;
        //this.outViewP.setIndeterminate(false);
        this.outViewP.setMax(totalSize);
        bytesRead=0;
    }

    public void updateProgress(int currentBytes){
        outViewP.setProgress(currentBytes);
        //Log.v("!BIS", this.TotalSize+ " - " + currentBytes);
    }

    @Override
    public synchronized int read() throws IOException {
        int bytes = inStream.read();
        this.bytesRead++;
        updateProgress(this.bytesRead);
        return bytes;
    }
/*
    @Override
    public int read(byte[] b) throws IOException {
        int bytes = inStream.read(b);
        this.bytesRead+=bytes;
        updateProgress(this.bytesRead);
        return bytes;
    }


    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        int bytes = inStream.read(b,off,len);
        this.bytesRead+=bytes;
        updateProgress(this.bytesRead);
        return bytes;
    }
*/

}
