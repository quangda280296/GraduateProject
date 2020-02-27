package vn.quang.graduateproject.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import vn.quang.graduateproject.R;
import vn.quang.graduateproject.adapter.PhotoPlaceAdapter;
import vn.quang.graduateproject.model.Analysis;
import vn.quang.graduateproject.model.Photo;
import vn.quang.graduateproject.utils.PackageManagerUtils;

import static vn.quang.graduateproject.Config.ANDROID_CERT_HEADER;
import static vn.quang.graduateproject.Config.ANDROID_PACKAGE_HEADER;
import static vn.quang.graduateproject.Config.CLOUD_VISION_API_KEY;
import static vn.quang.graduateproject.Config.MAX_DIMENSION;
import static vn.quang.graduateproject.Config.MAX_LABEL_RESULTS;
import static vn.quang.graduateproject.activity.MainActivity.camera;
import static vn.quang.graduateproject.activity.MainActivity.cameraAdapter;
import static vn.quang.graduateproject.activity.MainActivity.photoList;

public class AnalysisActivity extends AppCompatActivity {

    private static final String TAG = AnalysisActivity.class.getSimpleName();
    private static PhotoPlaceAdapter placeAdapter;
    private static RecyclerView recycler;
    private boolean isNew = true;
    private TextView lbl_noti;
    private ImageView img_detail;

    public static Vision.Images.Annotate prepareAnnotationRequest(Context context, Bitmap bitmap, String type) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
            /**
             * We override this so we can inject important identifying fields into the HTTP
             * headers. This enables use of a restricted cloud platform API key.
             */
            @Override
            protected void initializeVisionRequest(VisionRequest<?> visionRequest) throws IOException {
                super.initializeVisionRequest(visionRequest);

                String packageName = context.getPackageName();
                visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                String sig = PackageManagerUtils.getSignature(context.getPackageManager(), packageName);
                visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
            }
        };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {
            {
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                // Add the image
                Image base64EncodedImage = new Image();
                // Convert the bitmap to a JPEG
                // Just in case it's a format that Android understands but Cloud Vision
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes);
                annotateImageRequest.setImage(base64EncodedImage);

                // add the features we want
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature labelDetection = new Feature();
                    labelDetection.setType(type);
                    labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                    add(labelDetection);
                }});

                // Add the list of one thing to the request
                add(annotateImageRequest);
            }
        });

        Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    public static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        lbl_noti = findViewById(R.id.lbl_noti);
        img_detail = findViewById(R.id.img_detail);

        recycler = findViewById(R.id.recycler);
        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);

        // Setting the LayoutManager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setNestedScrollingEnabled(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        String URL = "";

        if (bundle.containsKey("new")) {
            isNew = false;
        }

        if (bundle.containsKey("uri")) {
            URL = bundle.getString("uri");
            Uri uri = Uri.parse(URL);
            uploadImage(uri);
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                /*String[] m_data = {MediaStore.Images.Media.DATE_TAKEN};
                Cursor c = getContentResolver().query(uri, m_data, null, null, MediaStore.Images.Media.TITLE + " ASC");

                c.moveToFirst();
                Date date = new Date(c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)));*/

                Date date = Calendar.getInstance().getTime();

                if (isNew) {
                    camera.insert("", date.toString(), uri.toString());

                    Photo photo = new Photo();
                    photo.date = date;
                    photo.uri = uri;
                    photoList.add(photo);
                    cameraAdapter.notifyDataSetChanged();
                }

                // scale the image to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), MAX_DIMENSION);
                callCloudVision(bitmap);

                img_detail.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Analysis picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Analysis picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        lbl_noti.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask
                    (this, prepareAnnotationRequest(getApplicationContext(), bitmap, "LANDMARK_DETECTION"), getApplicationContext(), bitmap, 2);
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
        }
    }

    public static class LableDetectionTask extends AsyncTask {
        private final WeakReference<AnalysisActivity> mActivityWeakReference;
        List<Analysis> list = new ArrayList<>();
        private Vision.Images.Annotate mRequest;
        private Context context;
        private Bitmap bitmap;
        private int kind;

        public LableDetectionTask(AnalysisActivity activity, Vision.Images.Annotate annotate, Context context, Bitmap bitmap, int kind) {
            this.mActivityWeakReference = new WeakReference<>(activity);
            this.mRequest = annotate;
            this.context = context;
            this.bitmap = bitmap;
            this.kind = kind;
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                BatchAnnotateImagesResponse response = mRequest.execute();
                List<EntityAnnotation> labels = null;

                if (kind == 1)
                    labels = response.getResponses().get(0).getLabelAnnotations();
                else if (kind == 2)
                    labels = response.getResponses().get(0).getLandmarkAnnotations();

                if (labels == null) {
                    Log.d("Errorrrrrrrrrrrrrr no", "Alert here");
                    return null;
                }

                for (EntityAnnotation label : labels) {
                    list.add(new Analysis((label.getScore() * 100) + "%:", label.getDescription()));
                }

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            AnalysisActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {

                if (list.size() == 0) {
                    // Do the real work in an async task, because we need to use the network anyway
                    try {
                        /*List<Analysis> list = new ArrayList<>();
                        placeAdapter = new PhotoPlaceAdapter(list, activity);
                        placeAdapter.updateList(list);*/

                        AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask
                                (activity, prepareAnnotationRequest(context, bitmap, "LABEL_DETECTION"), context, bitmap, 1);
                        labelDetectionTask.execute();
                        Log.d("Okkkkkkkkee", list.size() + "");
                    } catch (IOException e) {
                        Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                    }
                    return;
                }

                TextView lbl_noti = activity.findViewById(R.id.lbl_noti);
                lbl_noti.setText(R.string.I_found_these_things);

                placeAdapter = new PhotoPlaceAdapter(list, activity);
                recycler.setAdapter(placeAdapter);

                Log.d("Okkkkkkkkee", list.size() + "");
            }
        }
    }
}
