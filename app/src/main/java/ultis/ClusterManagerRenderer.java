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

import java.util.Set;

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

    @Override
    protected void onClusterUpdated(@NonNull Cluster<ClusterMarker> cluster, @NonNull Marker marker) {
        super.onClusterUpdated(cluster, marker);

        Log.d(TAG, "onClusterUpdated: " + cluster.getPosition());
    }

    @Override
    protected void onBeforeClusterRendered(@NonNull Cluster<ClusterMarker> cluster, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        Log.d(TAG, "onBeforeClusterRendered: " + cluster.getPosition());
    }

    @Override
    protected void onClusterItemRendered(@NonNull ClusterMarker clusterItem, @NonNull Marker marker) {
        Log.d(TAG, "onClusterItemRendered: " + clusterItem.getPosition());

        super.onClusterItemRendered(clusterItem, marker);

        StorageReference ref= FirebaseStorage.getInstance().getReference().child("avatars").child(clusterItem.getImageURL());

        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));


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

    public void updateClusterMarker(ClusterMarker clusterMarker) {
        Marker marker = getMarker(clusterMarker);
        Log.d(TAG, "updateClusterMarker: " + marker);
        if (marker != null) {
            marker.setPosition(clusterMarker.getPosition());
        }
    }
}
