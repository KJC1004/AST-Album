package csie.aad.ast_album.Models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.lang.ref.WeakReference;

import csie.aad.ast_album.R;
import csie.aad.ast_album.Utils.ImageUtils;

public class Stylize extends AsyncTask<Void, Void, Bitmap> {
    public static final int NUM_STYLES = 26;
    private static final String ASSET_PATH = "file:///android_asset/";
    public static final String THUMBNAIL_PATH = ASSET_PATH + "thumbnails/";
    private static final String MODEL_FILE = ASSET_PATH + "stylize_quantized.pb";
    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";
    private static boolean DEBUG = false;
    private static TensorFlowInferenceInterface inferenceInterface;

    private WeakReference<Context> mWeakContext;
    private WeakReference<ImageView> mViewport;
    private WeakReference<Button> mButtonSave;
    private int mPos;

    public Stylize(Context context, int pos) {
        Activity activity = (Activity) context;
        ImageView viewport = activity.findViewById(R.id.viewport);
        Button buttonSave = activity.findViewById(R.id.btn_save);

        mWeakContext = new WeakReference(context);
        mViewport = new WeakReference(viewport);
        mButtonSave = new WeakReference(buttonSave);
        mPos = pos;
    }

    public static void init(Context context) {
        inferenceInterface =
                new TensorFlowInferenceInterface(
                        context.getAssets(),
                        MODEL_FILE);
    }

    public static void clean() {
        inferenceInterface.close();
    }

    public static float[] getStyleVals(int i) {
        float[] vals = new float[NUM_STYLES];
        vals[i] = 0.5f;
        return vals;
    }

    public static Bitmap stylizeImage(Context context, int pos) {
        Bitmap bitmap = ImageUtils.drawableToBitmap(
                ((ImageView) ((Activity) context).findViewById(R.id.viewport)).getDrawable()
        );

        Bitmap scaled = Bitmap.createScaledBitmap(
                bitmap,
                512,
                512,
                false);

        stylize(scaled, pos);

        Bitmap result = Bitmap.createScaledBitmap(
                scaled,
                bitmap.getWidth(),
                bitmap.getHeight(),
                false);

        return result;
    }

    public static void stylize(final Bitmap bitmap, int pos) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] intValues = new int[w * h];
        float[] floatValues = new float[w * h * 3];
        float[] styleValues = getStyleVals(pos);

        bitmap.getPixels(intValues, 0, w, 0, 0, w, h);

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.0f;
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
            floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
        }

        // Copy the input data into TensorFlow.
        inferenceInterface.feed(INPUT_NODE, floatValues, 1, w, h, 3);
        inferenceInterface.feed(STYLE_NODE, styleValues, NUM_STYLES);
        // Execute the output node's dependency sub-graph.
        inferenceInterface.run(new String[]{OUTPUT_NODE}, DEBUG);
        // Copy the data from TensorFlow back into our array.
        inferenceInterface.fetch(OUTPUT_NODE, floatValues);

        for (int i = 0; i < intValues.length; ++i) {
            intValues[i] =
                    0xFF000000
                            | (((int) (floatValues[i * 3] * 255)) << 16)
                            | (((int) (floatValues[i * 3 + 1] * 255)) << 8)
                            | ((int) (floatValues[i * 3 + 2] * 255));
        }

        bitmap.setPixels(intValues, 0, w, 0, 0, w, h);
    }

    @Override
    protected void onPreExecute() {
        mViewport.get().setAlpha(0.5f);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Context context = mWeakContext.get();

        Bitmap result = null;
        try {
            result = Stylize.stylizeImage(context, mPos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        Context context = mWeakContext.get();
        ImageView viewport = mViewport.get();

        viewport.setAlpha(1.0f);

        if (result == null) {
            return;
        }

        Glide.with(context)
                .load(ImageUtils.bitmapToByte(result))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into(viewport);

        mButtonSave.get().setEnabled(true);
    }
}
