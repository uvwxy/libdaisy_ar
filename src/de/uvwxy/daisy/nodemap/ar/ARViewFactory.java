package de.uvwxy.daisy.nodemap.ar;

import gl.Color;
import gl.GL1Renderer;
import gl.GLFactory;

import java.util.List;

import system.ArActivity;
import system.Setup;
import worldData.World;
import android.app.Activity;
import android.util.Log;
import de.uvwxy.daisy.proto.Messages;
import de.uvwxy.daisy.proto.Messages.NodeLocationData;
import de.uvwxy.helper.ContextProxy;

public class ARViewFactory {

	public static SensorARView makeSetup(final ContextProxy ctxP, final List<Messages.NodeLocationData> locationsNode,
			final Color antenna, final Color box) {
		SensorARView defaultARSetup = new SensorARView() {

			@Override
			public void addObjectsTo(GL1Renderer renderer, World world, GLFactory objectFactory) {
				this.world = world;
				this.ctx = ctxP;
				this.objectFactory = objectFactory;
				
				for (final NodeLocationData node : locationsNode) {
					if (node == null) {
						return;
					}

					Log.i("ARVIEW", "Adding node " + node.getNodeId());
					addNodeLocation(node);

				}
			}

		};

		return defaultARSetup;
	}
	
	public static void showSetup(Activity act, Setup setup){
		ArActivity.startWithSetup(act, setup);
	}
}
