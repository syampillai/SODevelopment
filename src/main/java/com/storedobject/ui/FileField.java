package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.tools.JavaTool;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.FailedEvent;
import com.vaadin.flow.component.upload.Upload;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileField extends AbstractObjectField<StreamData> {

    private static final int BUTTON_SIZE = 32;
    private final static ObjectField.Type[] ALL_TYPES = new ObjectField.Type[0];
    private final Image image = new Image();
    private final Video video = new Video();
    private final Audio audio = new Audio();
    private final ButtonLayout contentBox;
    private final ButtonLayout buttonBox;
    private final ImageButton remove;
    private ImageButton upload;
    private ImageButton link;
    private ImageButton download;
    private final ImageButton downloadHidden;
    private ImageButton captureVideo;
    private ImageButton captureAudio;
    private ImageButton captureImage;
    private final ImageButton uploadCancel;
    private ImageButton viewContent;
    private final ImageButton playAudio;
    private final ImageButton playVideo;
    private ArrayList<String> mimeTypes;
    private final ObjectField.Type[] types;
    private StreamSaver saver;
    private String filename;
    private Application application;
    private boolean required = false, mediaPreview = true;
    private int maxFileSize = 10000000;

    public FileField() {
        this(ALL_TYPES);
    }

    public FileField(String caption) {
        this(caption, ALL_TYPES);
    }

    public FileField(ObjectField.Type... types) {
        this(null, types);
    }

    public FileField(String label, ObjectField.Type... types) {
        super(StreamData.class,false);
        if(types != null && types.length == 1 && types[0] == ObjectField.Type.AUTO) {
            types = null;
        }
        image.setHeight("150px");
        image.setVisible(false);
        video.setVisible(false);
        audio.setVisible(false);
        this.types = types == null || types.length == 0 ? ALL_TYPES : types;
        contentBox = new ButtonLayout() {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(true);
            }
        };
        contentBox.add(image, video, audio);
        buttonBox = new ButtonLayout();
        contentBox.add(buttonBox);
        ClickHandler clicked = new Clicked();
        if(containsAny(ObjectField.Type.FILE, ObjectField.Type.IMAGE, ObjectField.Type.AUDIO, ObjectField.Type.VIDEO)) {
            upload = new ImageButton("Upload file", VaadinIcon.UPLOAD, clicked).withBox(BUTTON_SIZE);
            link = new ImageButton("Link to ...", VaadinIcon.LINK, clicked).withBox(BUTTON_SIZE);
            link.setVisible(false);
        }
        if(contains(ObjectField.Type.STILL_CAMERA)) {
            captureImage = new ImageButton("Take photo", VaadinIcon.CAMERA, clicked).withBox(BUTTON_SIZE);
        }
        if(contains(ObjectField.Type.VIDEO_CAMERA)) {
            captureVideo = new ImageButton("Shoot video", VaadinIcon.MOVIE, clicked).withBox(BUTTON_SIZE);
        }
        if(contains(ObjectField.Type.MIC)) {
            captureAudio = new ImageButton("Record audio", VaadinIcon.MICROPHONE, clicked).withBox(BUTTON_SIZE);
        }
        remove = new ImageButton("Remove", VaadinIcon.CLOSE, clicked).withBox(BUTTON_SIZE);
        remove.setVisible(false);
        uploadCancel = new ImageButton("Cancel", VaadinIcon.CLOSE, clicked).withBox(BUTTON_SIZE);
        if(onlyPlusOne(ObjectField.Type.IMAGE, ObjectField.Type.STILL_CAMERA)) {
            mimes().add("image/*");
            hide(captureAudio);
            hide(captureVideo);
        } else if(onlyPlusOne(ObjectField.Type.AUDIO, ObjectField.Type.MIC)) {
            mimes().add("audio/*");
            hide(captureImage);
            hide(captureVideo);
        } else if(onlyPlusOne(ObjectField.Type.VIDEO, ObjectField.Type.VIDEO_CAMERA)) {
            mimes().add("video/*");
            hide(captureImage);
            hide(captureAudio);
        } else if(this.types.length > 0 && !contains(ObjectField.Type.FILE)) {
            if(containsAny(ObjectField.Type.IMAGE, ObjectField.Type.STILL_CAMERA)) {
                mimes().add("image/*");
            }
            if(containsAny(ObjectField.Type.AUDIO, ObjectField.Type.MIC)) {
                mimes().add("audio/*");
            }
            if(containsAny(ObjectField.Type.VIDEO, ObjectField.Type.VIDEO_CAMERA)) {
                mimes().add("video/*");
            }
        }
        detailComponent.setVisible(false);
        viewContent = new ImageButton("View", VaadinIcon.EYE, clicked).withBox(BUTTON_SIZE);
        playAudio = new ImageButton("Play", VaadinIcon.VOLUME_UP, clicked).withBox(BUTTON_SIZE);
        playVideo = new ImageButton("Play", VaadinIcon.MOVIE, clicked).withBox(BUTTON_SIZE);
        download = new ImageButton("Download", VaadinIcon.DOWNLOAD, clicked).withBox(BUTTON_SIZE);
        downloadHidden = download;
        setLabel(label);
        setValue((StreamData) null);
    }

    private void hide(ImageButton b) {
        if(b != null) {
            b.setVisible(false);
        }
    }

    public void allowLinking() {
        if(link != null) {
            link.setVisible(true);
        }
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    private boolean containsAny(ObjectField.Type... types) {
        if(this.types.length == 0) {
            return true;
        }
        for(ObjectField.Type t: types) {
            for(ObjectField.Type type: this.types) {
                if(t == type) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean contains(ObjectField.Type... types) {
        if(this.types.length == 0) {
            return true;
        }
        boolean found;
        for(ObjectField.Type t: types) {
            found = false;
            for(ObjectField.Type type: this.types) {
                if(t == type) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }

    private boolean only(ObjectField.Type... types) {
        if(this.types.length == 0) {
            return false;
        }
        return this.types.length == types.length && contains(types);
    }

    private boolean onlyPlusOne(ObjectField.Type... types) {
        if(only(types)) {
            return true;
        }
        return only(types[0]) || only(types[1]);
    }

    private Application getApplication() {
        if(application == null) {
            application = Application.get();
        }
        return application;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(application == null) {
            getApplication();
        }
    }

    public static <O extends StoredObject> boolean canCreate(Class<O> objectClass) {
        return ObjectGetField.canCreate(objectClass);
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
        remove.setVisible(!this.required);
    }

    @Override
    public final boolean isRequired() {
        return required;
    }

    @Override
    protected Component createPrefixComponent() {
        return contentBox;
    }

    @Override
    protected StreamData generateModelValue() {
        return getValue();
    }

    @Override
    protected void setPresentationValue(StreamData value) {
        super.setPresentationValue(value);
        display(value);
    }

    @Override
    protected void setModelValue(StreamData value, boolean fromClient) {
        super.setModelValue(value, fromClient);
        display(value);
    }

    private void mediaOff() {
        mediaOff(null);
    }

    private void mediaOff(Component except) {
        if(image != except) {
            image.setVisible(false);
            image.clear();
        }
        if(video != except) {
            video.setVisible(false);
            video.clear();
        }
        if(audio != except) {
            audio.setVisible(false);
            audio.clear();
        }
        if(except != null) {
            except.setVisible(mediaPreview);
        }
        detailComponent.setVisible(!mediaPreview || except == null);
    }

    private void display(StreamData value) {
        if(value != null) {
            setCached(value);
        }
        if(value == null) {
            mediaOff();
        } else {
            if(value.isImage()) {
                mediaOff(image);
                image.setSource(new DBResource(value));
            } else if(value.isVideo()) {
                mediaOff(video);
                video.setSource(new DBResource(value));
            } else if(value.isAudio()) {
                mediaOff(audio);
                audio.setSource(new DBResource(value));
            } else {
                mediaOff();
            }
            if(value.isMedia()) {
                if(!mediaPreview) {
                    super.setPresentationValue(value);
                }
            } else {
                super.setPresentationValue(value);
            }
        }
        redraw(value);
    }

    @Override
    public void setValue(StreamData value) {
        super.setValue(value);
        display(value);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        redraw();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        redraw();
    }

    private void redraw() {
        redraw(getValue());
    }

    private void redraw(StreamData value) {
        if(buttonBox == null) {
            return;
        }
        boolean active = !isReadOnly() && isEnabled();
        buttonBox.removeAll();
        boolean added = false;
        ImageButton extra = addExtraButton(value);
        if(extra != null) {
            added = true;
            buttonBox.add(extra);
        }
        if(active) {
            buttonBox.add(upload, captureImage, captureVideo, captureAudio, link);
            if(!isRequired()) {
                buttonBox.add(remove);
            }
            added = true;
        }
        if(value != null) {
            if(!value.isMedia()) {
                buttonBox.add(viewContent);
                added = true;
                buttonBox.add(download);
            } else if(!mediaPreview) {
                if(value.isAudio()) {
                    buttonBox.add(playAudio);
                } else if(value.isVideo()) {
                    buttonBox.add(playVideo);
                } else { // Image
                    buttonBox.add(viewContent);
                }
                added = true;
                buttonBox.add(download);
            } else {
                if(active && download != null) {
                    buttonBox.add(download);
                }
            }
        }
        buttonBox.setVisible(added);
    }

    protected ImageButton addExtraButton(StreamData value) {
        return null;
    }

    private String caption() {
        String label = getInternalLabel();
        if(label == null || label.isEmpty()) {
            label = getLabel();
        }
        return label == null || label.isEmpty() ? "File" : label;
    }

    private class Clicked implements ClickHandler {

        @Override
        public void clicked(Component component) {
            if (component == viewContent || component == playAudio || component == playVideo || component == download) {
                StreamData sd = getObject();
                if (sd != null) {
                    if (component == download) {
                        Application.get().download(sd);
                    } else {
                        Application.get().view(caption(), sd);
                    }
                }
                return;
            }
            if (isReadOnly()) {
                return;
            }
            if (component == upload) {
                buttonBox.removeAll();
                buttonBox.add(uploadCancel);
                Upload u = new Upload(FileField.this::receiveUpload);
                u.setMaxFiles(1);
                u.setMaxFileSize(maxFileSize);
                if(mimeTypes != null && mimeTypes.size() > 0) {
                    u.setAcceptedFileTypes(String.join(",", mimeTypes));
                }
                buttonBox.add(u);
                u.addFailedListener(FileField.this::uploadFailed);
                u.addSucceededListener(e -> uploadSucceeded());
                return;
            }
            if (component == uploadCancel) {
                if (saver != null) {
                    saver.cancel();
                }
                redraw();
                return;
            }
            if (component == remove) {
                buttonBox.remove(remove);
                setModelValue(null, true);
                return;
            }
            if(component == captureImage) {
                new ImageForm(getValue()).execute(getApplication().getActiveView());
                return;
            }
            if(component == captureVideo) {
                new VideoForm(getValue()).execute(getApplication().getActiveView());
                return;
            }
            if(component == captureAudio) {
                new AudioForm(getValue()).execute(getApplication().getActiveView());
                return;
            }
            if (component == link) {
                new FileField.LinkForm().execute(Application.get().getViewFor(contentBox));
            }
        }
    }
    
    private String allowed() {
        StringBuilder s = new StringBuilder();
        if(containsAny(ObjectField.Type.IMAGE, ObjectField.Type.STILL_CAMERA)) {
            s.append("Images");
        }
        if(containsAny(ObjectField.Type.AUDIO, ObjectField.Type.MIC)) {
            if(s.length() > 0) {
                s.append(", ");
            }
            s.append("Audio Files");
        }
        if(containsAny(ObjectField.Type.VIDEO, ObjectField.Type.VIDEO_CAMERA)) {
            if(s.length() > 0) {
                s.append(", ");
            }
            s.append("Video Files");
        }
        return "Allows only - " + s;
    }

    private List<String> mimes() {
        if (mimeTypes == null) {
            mimeTypes = new ArrayList<>();
        }
        return mimeTypes;
    }

    public void addContentType(String... contentType) {
        if (contentType == null || contentType.length == 0) {
            return;
        }
        if (types.length > 1 && types[0] != ObjectField.Type.FILE) {
            throw new SORuntimeException(allowed());
        }
        mimes();
        for(String ct: contentType) {
            if(ct != null) {
                mimeTypes.add(ct);
            }
        }
    }

    public void removeContentType(String contentType) {
        if (types.length > 1 && types[0] != ObjectField.Type.FILE) {
            throw new SORuntimeException(allowed());
        }
        if (mimeTypes != null) {
            mimeTypes.remove(contentType);
        }
    }

    public boolean isContentTypeAllowed(String contentType) {
        if (mimeTypes == null) {
            return true;
        }
        for (String m : mimeTypes) {
            if(m.endsWith("*")) {
                m = m.substring(0, m.length() - 1);
            }
            if (contentType.startsWith(m)) {
                return true;
            }
        }
        return false;
    }

    public boolean isImage() {
        return contains(ObjectField.Type.IMAGE, ObjectField.Type.STILL_CAMERA);
    }
    
    public boolean isAudio() {
        return contains(ObjectField.Type.AUDIO, ObjectField.Type.MIC);
    }
    
    public boolean isVideo() {
        return contains(ObjectField.Type.VIDEO, ObjectField.Type.VIDEO_CAMERA);
    }

    public void disallowLinking() {
        if (link != null && link.getParent().isPresent()) {
            buttonBox.remove(link);
            link = null;
        }
    }

    public void disallowDownload() {
        if(download != null && download.getParent().isPresent()) {
            buttonBox.remove(download);
        }
        download = null;
    }

    public void allowDownload() {
        if(download == null) {
            download = downloadHidden;
            if(application != null) {
                redraw();
            }
        }
    }

    public void disallowView() {
        if(viewContent != null && viewContent.getParent().isPresent()) {
            buttonBox.remove(viewContent);
        }
        viewContent = null;
    }

    private void uploadSucceeded() {
        redraw();
        if(saver == null) {
            return;
        }
        try {
            saver.join();
        } catch (InterruptedException ignored) {
        }
        if(saver.isCancelled()) {
            Application.message("Upload cancelled");
        } else {
            setInvalid(false);
            if(types.length == 0) {
                Application.message("Upload completed");
            }
        }
    }

    private void uploadFailed(FailedEvent event) {
        redraw();
        if(isContentTypeAllowed(event.getMIMEType())) {
            Application.error(event.getReason());
        } else {
            Application.error("Invalid file type rejected");
        }
    }

    private OutputStream receiveUpload(String filename, String mimeType) {
        this.filename = filename;
        saver = null;
        if(!isContentTypeAllowed(mimeType)) {
            Application.error("Invalid file type rejected");
            return IO.nullOutput();
        }
        Transaction tran;
        try {
            tran = Application.get().getTransactionManager().createTransaction();
            StreamData sd = new StreamData(mimeType);
            sd.setTransaction(tran);
            saver = new StreamSaver(sd);
            return saver.getOutputPipe();
        } catch(Exception e) {
            Application.error(e);
        }
        return IO.nullOutput();
    }

    public String getFileName() {
        return filename;
    }

    public void setMediaPreview(boolean preview) {
        this.mediaPreview = preview;
    }

    private class StreamSaver extends Thread implements StreamDataProvider {

        private final StreamData sd;
        private final PipedOutputStream outPipe;
        private final PipedInputStream inPipe;
        private boolean cancelled = false, saved = false;

        protected StreamSaver(StreamData sd) throws Exception {
            this.sd = sd;
            sd.setStreamDataProvider(this);
            outPipe = new PipedOutputStream();
            inPipe = new PipedInputStream(outPipe);
            start();
        }

        public OutputStream getOutputPipe() {
            return outPipe;
        }

        public void cancel() {
            if(!saved) {
                cancelled = true;
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void run() {
            Transaction tran = sd.getTransaction();
            try {
                sd.save();
                tran.commit();
                if(!cancelled) {
                    saved = true;
                    getApplication().access(() -> setModelValue(sd,true));
                }
            } catch (Exception e) {
                getApplication().access(() -> Application.error(e));
                if(tran != null) {
                    try {
                        tran.rollback();
                    } catch(Exception ignore) {
                    }
                }
            }
        }

        @Override
        public InputStream getStream(StreamData streamData) {
            return inPipe;
        }
    }

    private abstract class AbstractMediaForm extends DataForm {

        File file;
        String mimeType;
        private StreamData sd;
        boolean recorded;

        public AbstractMediaForm(String caption, StreamData sd) {
            super(caption);
            this.sd = sd;
            setButtonsAtTop(true);
        }

        @Override
        protected HasComponents createLayout() {
            return new Div();
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setText("Save");
            ok.setVisible(false);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            getApplication().startPolling(this);
        }

        @Override
        public void clean() {
            getApplication().stopPolling(this);
            super.clean();
        }

        @Override
        protected boolean process() {
            if(sd == null) {
                sd = new StreamData();
            }
            sd.setStreamDataProvider(new FileDataProvider(file));
            sd.setContentType(mimeType);
            try {
                FileField.this.getApplication().getTransactionManager().transact(t -> sd.save(t));
                getApplication().access(() -> setModelValue(sd, true));
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            } catch (Exception error) {
                getApplication().access(() -> Application.error(error));
            }
            return true;
        }

        void streamFinished() {
        }

        void streamAborted() {
        }

        class MediaSaver implements MediaCapture.DataReceiver {

            private OutputStream out;
            private final String fileExt;

            private MediaSaver(String fileExt) {
                this.fileExt = fileExt;
            }

            @Override
            public OutputStream getOutputStream(String mimeType) {
                int p = mimeType.indexOf(';');
                if(p > 0) {
                    mimeType = mimeType.substring(0, p);
                }
                AbstractMediaForm.this.mimeType = mimeType;
                if(file == null) {
                    try {
                        file = File.createTempFile("socamera", fileExt, new File(JavaTool.getSourceDirectory()));
                        file.deleteOnExit();
                    } catch (IOException e) {
                        file = null;
                    }
                }
                if(file != null && out == null) {
                    try {
                        out = IO.getOutput(file);
                        return out;
                    } catch (IOException e) {
                        out = null;
                    }
                }
                return null;
            }

            @Override
            public void finished() {
                close();
                recorded = true;
                getApplication().access(() -> {
                    ok.setVisible(true);
                    streamFinished();
                });
            }

            @Override
            public void aborted() {
                close();
                recorded = false;
                getApplication().access(AbstractMediaForm.this::streamAborted);
            }

            void close() {
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException ignored) {
                    } finally {
                        out = null;
                    }
                }
            }
        }
    }

    private class ImageForm extends AbstractMediaForm {

        private ImageButton shoot;
        private ImageButton preview;
        private Image image;
        private VideoCapture video;

        public ImageForm(StreamData sd) {
            super("Take Picture", sd);
        }

        @Override
        protected void buildFields() {
            image = new Image();
            add(image);
            video = new VideoCapture(image);
            video.getElement().removeAttribute("controls");
            add(video);
            video.addStatusChangeListener(v -> getApplication().access(() -> shoot.setVisible(v.isPreviewing())));
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            shoot = new ImageButton("Shoot", VaadinIcon.CAMERA,this).withBox(BUTTON_SIZE);
            preview = new ImageButton("Show video preview for retake", VaadinIcon.MOVIE,this).withBox(BUTTON_SIZE);
            buttonPanel.add(shoot, preview);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            clicked(preview);
        }

        @Override
        public void clicked(Component c) {
            if(c == shoot) {
                video.savePicture(new MediaSaver(".jpg"));
                return;
            }
            if(c == preview) {
                preview.setVisible(false);
                shoot.setVisible(false);
                hide(image);
                show(video);
                video.preview();
                return;
            }
            super.clicked(c);
        }

        private void hide(Component c) {
            c.getElement().getStyle().set("display", "none");
        }

        private void show(Component c) {
            c.getElement().getStyle().set("display", "block");
        }

        @Override
        void streamFinished() {
            super.streamFinished();
            hide(video);
            show(image);
            video.stopDevice();
            shoot.setVisible(false);
            preview.setVisible(true);
        }

        @Override
        void streamAborted() {
            super.streamAborted();
            clicked(preview);
        }
    }

    private abstract class MediaForm extends AbstractMediaForm {

        MediaCapture media;
        private ImageButton startRecording, stopRecording, view;

        public MediaForm(String caption, StreamData sd) {
            super(caption, sd);
        }

        @Override
        protected void buildFields() {
            media = createDevice();
            media.addStatusChangeListener(m -> getApplication().access(() -> {
                startRecording.setVisible(media.isPreviewing() || recorded);
                stopRecording.setVisible(media.isRecording());
            }));
            add((Component)media);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            view = createViewButton().withBox(BUTTON_SIZE);
            startRecording = new ImageButton("Record", createRecordIcon(),this).withBox(BUTTON_SIZE);
            stopRecording = new ImageButton("Stop recording", VaadinIcon.STOP,this);
            stopRecording.setStyle("color", Application.COLOR_ERROR);
            stopRecording.withBox(BUTTON_SIZE);
            buttonPanel.add(view, startRecording, stopRecording);
        }

        abstract VaadinIcon createRecordIcon();

        abstract MediaCapture createDevice();

        abstract ImageButton createViewButton();

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            view.setVisible(false);
            stopRecording.setVisible(false);
        }

        @Override
        public void clicked(Component c) {
            if(c == startRecording) {
                ok.setVisible(false);
                startRecording.setVisible(false);
                media.startRecording(new MediaSaver(".webm"));
                recorded = false;
                view.setVisible(false);
                return;
            }
            if(c == stopRecording) {
                stopRecording.setVisible(false);
                media.stopRecording();
                return;
            }
            if(c == view) {
                view.setVisible(false);
                media.setSource(new FileResource(file, mimeType));
                return;
            }
            super.clicked(c);
        }

        @Override
        void streamFinished() {
            super.streamFinished();
            startRecording.setVisible(true);
            media.clear();
            view.setVisible(true);
        }

        @Override
        void streamAborted() {
            super.streamAborted();
            startRecording.setVisible(true);
        }
    }

    private class VideoForm extends MediaForm {

        public VideoForm(StreamData sd) {
            super("Shoot Video", sd);
        }

        @Override
        MediaCapture createDevice() {
            return new VideoCapture();
        }

        @Override
        ImageButton createViewButton() {
            return new ImageButton("View", VaadinIcon.EYE,this);
        }

        @Override
        VaadinIcon createRecordIcon() {
            return VaadinIcon.MOVIE;
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            ((VideoCapture)media).preview();
        }
    }

    private class AudioForm extends MediaForm {

        public AudioForm(StreamData sd) {
            super("Record Audio", sd);
        }

        @Override
        MediaCapture createDevice() {
            return new AudioCapture();
        }

        @Override
        ImageButton createViewButton() {
            return new ImageButton("Play", VaadinIcon.VOLUME_UP,this);
        }

        @Override
        VaadinIcon createRecordIcon() {
            return VaadinIcon.MICROPHONE;
        }
    }

    private class LinkForm extends DataForm {

        private TextField address;

        public LinkForm() {
            super(caption());
        }

        @Override
        protected void buildFields() {
            StreamData sd = getObject();
            if(sd != null && !sd.isLink()) {
                addField(new CompoundField("Warning:", new ELabel("Current content will be overwritten!",
                        Application.COLOR_ERROR)));
            }
            address = new TextField("Link");
            address.setMaxLength(400);
            address.setClearButtonVisible(true);
            setRequired(address);
            if(sd != null && sd.isLink()) {
                address.setValue(sd.getLink());
            }
            addField(address);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            ((Dialog)getComponent()).setWidth("50em");
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            Transaction tran = null;
            String a = address.getValue().trim();
            if(a.isEmpty() || (!a.startsWith("http://") && !a.startsWith("https://") && !a.startsWith("db:"))) {
                warning("Invalid link address");
                return false;
            }
            try {
                String ct;
                if(a.startsWith("db:")) {
                    StreamData sd = StreamData.getViaLink(a);
                    if(sd == null) {
                        error("Invalid DB link");
                        return false;
                    }
                    try {
                        sd.getContent().close();
                    } catch (IOException ignored) {
                    }
                    ct = sd.getMimeType();
                } else {
                    URL url = new URL(a);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Connection", "close");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setConnectTimeout(30 * 1000);
                    conn.setReadTimeout(30 * 1000);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    ct = conn.getContentType();
                    conn.disconnect();
                }
                if (ct == null) {
                    error("Unable to determine content type!");
                    return false;
                }
                if (!isContentTypeAllowed(ct)) {
                    error("Invalid file type rejected");
                    return false;
                }
                tran = Application.get().getTransactionManager().createTransaction();
                StreamData sd = new StreamData("l:" + ct);
                sd.setStreamDataProvider(streamData -> new ByteArrayInputStream(a.getBytes(StandardCharsets.UTF_8)));
                sd.save(tran);
                tran.commit();
                setModelValue(sd, true);
                return true;
            } catch(Exception e) {
                if(tran != null) {
                    tran.rollback();
                }
                error(e);
            }
            return true;
        }
    }
}