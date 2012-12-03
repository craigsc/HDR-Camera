package com.craigsc.hdr;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HDRCamera extends Activity implements SurfaceHolder.Callback, OnClickListener {
    private Camera camera;
    private static final String TAG = "HDR CAMERA";
    private boolean previewRunning = false;
    private OrientationEventListener listener;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.camera_surface);
        SurfaceView sv = (SurfaceView) findViewById(R.id.camera_surface);
        sv.getHolder().addCallback(this);
        sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        findViewById(R.id.camera_button).setOnClickListener(this);
        findViewById(R.id.camera_button).getBackground().setAlpha(125);
        loadGalleryPreview();
        listener = new OrientationManager(this);
        listener.enable();
    }
    
    private void loadGalleryPreview() {
    	Cursor c = MediaStore.Images.Thumbnails.query(getContentResolver(),
    			MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
    			new String[]{MediaStore.Images.Thumbnails.IMAGE_ID});
    	if (c.moveToLast()) {
    		int id = c.getInt(c.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
    		((ImageView)findViewById(R.id.gallery_preview)).setImageURI(
    				Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, id + ""));
    	} else {
    		findViewById(R.id.gallery_preview).getBackground().setAlpha(0);
    	}
    }
    
	public void onClick(View view) {
		
		
		/*Parameters p = camera.getParameters();
		if (p.getMinExposureCompensation() == 0 && p.getMaxExposureCompensation() == 0) {
			//exposure compensation not supported on this device so fake the hdr
		} else {
			//take -2 EV, 0 EV, and +2 EV exposure shots in rapid succession
			int low = (int) (-2.0 / p.getExposureCompensationStep());
			int high = (int) (2.0 / p.getExposureCompensationStep());
			
		}
		/*camera.setParameters(camera.getParameters().setExposureCompensation(-2))*/
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		if (camera.getParameters().getExposureCompensationStep() != 0) {
			
		}
	}
    
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (previewRunning) {
			camera.stopPreview();
		}
		//ensure preview size is set appropriately
		Parameters p = camera.getParameters();
		p.setPreviewSize(width, height);
		camera.setParameters(p);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.e(TAG, "FOOK");
			e.printStackTrace();
		}
		camera.startPreview();
		previewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		previewRunning = false;
		camera.release();
	}
	
	private final class OrientationManager extends OrientationEventListener {
		private float curOrientation = 0.0f;
		public OrientationManager(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			if (orientation <= 45 || orientation >= 315) {
				if (curOrientation != 270.0f) {
					float target = curOrientation == 0.0f ? -90.0f : 270.0f;
					Animation a = new RotateAnimation(curOrientation, target, 
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					a.setFillAfter(true);
					a.setDuration(500);
					ImageButton ib = (ImageButton) findViewById(R.id.camera_button);
					ib.clearAnimation();
					ib.startAnimation(a);
				}
				curOrientation = 270.0f;
			} else if (orientation >= 45 && orientation <= 135) {
				if (curOrientation != 180.0f) {
					Animation a = new RotateAnimation(curOrientation, 180.0f, 
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					a.setFillAfter(true);
					a.setDuration(500);
					ImageButton ib = (ImageButton) findViewById(R.id.camera_button);
					ib.clearAnimation();
					ib.startAnimation(a);;
				}
				curOrientation = 180.0f;
			} else if (orientation >= 135 && orientation <= 225) {
				if (curOrientation != 90.0f) {
					Animation a = new RotateAnimation(curOrientation, 90.0f, 
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					a.setFillAfter(true);
					a.setDuration(500);
					ImageButton ib = (ImageButton) findViewById(R.id.camera_button);
					ib.clearAnimation();
					ib.startAnimation(a);
				}
				curOrientation = 90.0f;
			} else if (orientation >= 225 && orientation <= 315) {
				if (curOrientation != 0.0f) {
					float target = curOrientation == 270.0f ? 360.0f : 0.0f;
					Animation a = new RotateAnimation(curOrientation, target, 
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					a.setFillAfter(true);
					a.setDuration(500);
					ImageButton ib = (ImageButton) findViewById(R.id.camera_button);
					ib.clearAnimation();
					ib.startAnimation(a);
				}
				curOrientation = 0.0f;
			}
		}
	}
}