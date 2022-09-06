package sr.we.util;

import com.vaadin.flow.component.upload.Receiver;

import java.io.File;
import java.io.InputStream;

public interface UploadFieldReceiver extends Receiver {

    InputStream getContentAsStream();

    boolean isEmpty();

    long getLastFileSize();

    String getLastMimeType();

    String getLastFileName();

    byte[] getContentAsByte();

    File getFile();

    String getExtension();
}
