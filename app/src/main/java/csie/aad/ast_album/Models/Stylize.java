package csie.aad.ast_album.Models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import csie.aad.ast_album.R;
import csie.aad.ast_album.Utils.ImageUtils;

public class Stylize {
    private static final String ASSET_PATH = "file:///android_asset/";
    private static final String MODEL_FILE = ASSET_PATH + "stylize_quantized.pb";
    public static final String THUMBNAIL_PATH = ASSET_PATH + "thumbnails/";

    private static boolean DEBUG = false;
    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";
    public static final int NUM_STYLES = 26;

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

        stylize(context, scaled, pos);

        Bitmap result = Bitmap.createScaledBitmap(
                scaled,
                bitmap.getWidth(),
                bitmap.getHeight(),
                false);

        return result;
    }

    public static void stylize(Context context, final Bitmap bitmap, int pos) {
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

        TensorFlowInferenceInterface inferenceInterface =
                new TensorFlowInferenceInterface(
                        context.getAssets(),
                        MODEL_FILE);

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
}
