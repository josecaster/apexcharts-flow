package sr.we.util;

import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

@SuppressWarnings("serial")
public class FileBuffer extends MultiFileMemoryBuffer /*implements MultiFileReceiver*/ {

//    private String mimeType;
//    private String extension;
//    private String fileName;
//    private File file;
//
//    public FileBuffer() {
//        super();
//    }
//
//    /**
//     * //     * @see com.vaadin.ui.Upload.Receiver#receiveUpload(String, String)
//     */
//    @Override
//    public OutputStream receiveUpload(String filename, String MIMEType) {
//        fileName = filename;
//        mimeType = MIMEType;
//        extension = FilenameUtils.getExtension(filename);
//        try {
//            if (file != null && file.exists()) {
//                try {
//                    Files.deleteIfExists(file.toPath());
//                } catch (IOException | SecurityException e) {
//                    //
//                }
//            }
//
//            // Path path = Files.createTempFile("import-template", null);
//            // file = path.toFile();
//            TemporaryFileFactory tempFileFactory = new TemporaryFileFactory();
//
//            file = tempFileFactory.createFile(filename/*, mimeType*/);
//            FileUtils.touch(file);
//            // Encryptor.encryptFile(source, file);
//            //
//            return new FileOutputStream(file);
////			CipherOutputStream cipherOutputStream = new CipherOutputStream(
////					new FileOutputStream(file),
////					Encryptor.getCipher(Cipher.DECRYPT_MODE));
////			return cipherOutputStream;
//            // new FileOutputStream(file);
//        } catch (final IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
////    @Override
//    public InputStream getContentAsStream() {
//        if (file != null) {
//            try {
//                return new FileInputStream(getFile());
//            } catch (final FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            return null;
//        }
//    }
//
////    @Override
//    public boolean isEmpty() {
//        return file == null || !file.exists();
//    }
//
////    @Override
//    public long getLastFileSize() {
//        return file == null ? 0 : file.length();
//    }
//
////    @Override
//    public String getLastMimeType() {
//        return mimeType;
//    }
//
////    @Override
//    public String getLastFileName() {
//        return fileName;
//    }
//
////    @Override
//    public File getFile() {
//        return file;
//    }
//
////    @Override
//    public byte[] getContentAsByte() {
//        try {
//            return IOUtils.toByteArray(getContentAsStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public String getExtension() {
//        return extension;
//    }

}
