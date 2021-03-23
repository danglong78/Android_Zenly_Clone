package ultis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import data.models.ClusterMarker;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker>
{
    private final String TAG = "ClusterManagerRenderer";

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final Context context;

    public ClusterManagerRenderer(Context context, GoogleMap googleMap,
                                    ClusterManager<ClusterMarker> clusterManager) {

        super(context, googleMap, clusterManager);

        // initialize cluster item icon generator
        this.context = context;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        imageView.setPadding(0,0,0,0);
        iconGenerator.setContentView(imageView);
    }

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
//    @Override
//    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
//        Log.d(TAG, "onBeforeClusterItemRendered: ");
//
//        StorageReference ref= FirebaseStorage.getInstance().getReference().child("avatars").child(item.getImageURL());
//        Log.d(TAG, "onBeforeClusterItemRendered: path " + ref.getDownloadUrl());
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//
//                Log.d(TAG, "onSuccess: imageView " + imageView.getDrawable());
//
//                Bitmap icon = iconGenerator.makeIcon();
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                Log.d(TAG, "onFailure: ");
//            }
//        });
//
//
////        imageView.setImageResource(item.getIconPicture());
//    }

    @Override
    protected void onClusterItemRendered(@NonNull ClusterMarker clusterItem, @NonNull Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);

        StorageReference ref= FirebaseStorage.getInstance().getReference().child("avatars").child(clusterItem.getImageURL());
        Log.d(TAG, "onClusterItemRendered: path " + ref.getDownloadUrl());

        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                Log.d(TAG, "onSuccess: imageView " + imageView.getDrawable());

                Bitmap icon = iconGenerator.makeIcon();
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                marker.setTitle(clusterItem.getTitle());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "onFailure: ");
            }
        });

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }
}
