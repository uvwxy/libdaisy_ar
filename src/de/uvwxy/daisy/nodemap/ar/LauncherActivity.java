package de.uvwxy.daisy.nodemap.ar;

import geo.GeoObj;
import gl.Color;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import system.ArActivity;
import system.DefaultARSetup;
import system.Setup;
import worldData.Obj;
import worldData.World;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import commands.ui.CommandShowToast;
import components.ProximitySensor;

import de.uvwxy.sensors.location.GPSWIFIReader;
import de.uvwxy.sensors.location.LocationReader.LocationResultCallback;
import de.uvwxy.sensors.location.LocationReader.LocationStatusCallback;

/**
 * Modification and distribution is not permitted. Usage, even in parts, in
 * projects other than this file is not permitted.
 * 
 * @author Paul Smith (C) 2012
 * 
 */
public class LauncherActivity extends Activity {
	LauncherActivity main = this; // current fix to access "this" in sWS(..)

	private Button btnTest1 = null;

	// 10 points in front of "Eingang Sued"
	private double[] ptsLat = { 50.77812038156778, 50.77816924141844, 50.778153110659986, 50.778140019025855,
			50.778133940765905, 50.77812505715379, 50.77813721367521, 50.77816762324891, 50.77816101097765,
			50.778151753796315 };
	private double[] ptsLon = { 6.06076366817506, 6.060734091075534, 6.060769583594967, 6.0607799355797995,
			6.060756643613923, 6.060726327086908, 6.060718563098282, 6.060764838881854, 6.0607386961723,
			6.060707324920837 };

	private World worldNodes = null;
	private int collectedpoints = 0;

	private Color antenna = new Color(0, 0, 0, 1);
	private Color box = Color.battleshipGrey();

	Setup defaultARSetup = new DefaultARSetup() {

		@Override
		public void addObjectsTo(GL1Renderer renderer, World world, GLFactory objectFactory) {
			worldNodes = world;
			for (int i = 0; i < ptsLat.length; i++) {
				// add objects at camera height
				GeoObj g = new GeoObj(ptsLat[i], ptsLon[i], 0);

				g.setComp(Obj3DSensorNode.createMesh(antenna, box));

				g.setComp(new ProximitySensor(camera, 3f) {
					@Override
					public void onObjectIsCloseToCamera(GLCamera myCamera2, Obj obj, MeshComponent m,
							float currentDistance) {
						collectedpoints++;
						new CommandShowToast(main, "" + (ptsLat.length - collectedpoints) + " objects remain..")
								.execute();
						worldNodes.remove(obj);
					}

				});

				world.add(g);

			}
		}

	};

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view.equals(btnTest1)) {
				initARView();
			}
		}

	};

	private GPSWIFIReader locationReader;

	private LocationStatusCallback cbStatus = new LocationStatusCallback() {

		@Override
		public void status(Location l) {

		}

	};

	private LocationResultCallback cbResult = new LocationResultCallback() {

		@Override
		public void result(Location l) {
			if (l == null) {
				return;
			}
			if (worldNodes == null) {
				return;
			}
			Log.i("ARVIEW", "Adding location to " + l.getLatitude() + "/" + l.getLongitude());
			GeoObj g = new GeoObj(l.getLatitude(), l.getLongitude(), 0);
			g.setComp(Obj3DSensorNode.createMesh(antenna, box));

			g.setComp(new ProximitySensor(worldNodes.getMyCamera(), 3f) {
				@Override
				public void onObjectIsCloseToCamera(GLCamera myCamera2, Obj obj, MeshComponent m, float currentDistance) {
					collectedpoints++;
					new CommandShowToast(main, "" + (ptsLat.length - collectedpoints) + " objects remain..").execute();
					worldNodes.remove(obj);
				}
			});
			worldNodes.add(g);

		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnTest1 = (Button) findViewById(R.id.btnTest1);
		btnTest1.setOnClickListener(onClickListener);

		locationReader = new GPSWIFIReader(this, -1, 10, cbStatus, cbResult, true, true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		locationReader.startReading();
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationReader.stopReading();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initARView() {
		ArActivity.startWithSetup(main, defaultARSetup);
	}
}