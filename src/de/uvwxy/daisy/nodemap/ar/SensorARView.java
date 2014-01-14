package de.uvwxy.daisy.nodemap.ar;

import geo.GeoObj;
import gl.Color;
import gl.GLCamera;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

import java.util.List;

import system.DefaultARSetup;
import worldData.Obj;
import worldData.World;

import com.google.common.base.Preconditions;
import commands.ui.CommandShowToast;
import components.ProximitySensor;

import de.uvwxy.daisy.proto.Messages.NodeLocationData;
import de.uvwxy.helper.ContextProxy;

public abstract class SensorARView extends DefaultARSetup {
	protected World world = null;
	protected GLFactory objectFactory;
	protected ContextProxy ctx;

	private Color antenna = new Color(0, 0, 0, 1);
	private Color box = Color.battleshipGrey();

	public void addNodeLocation(NodeLocationData nld) {
		if (world == null) {
			return;
		}

		addNode(ctx, nld, world, objectFactory, antenna, box);
	}

	private void addNode(final ContextProxy ctx, final NodeLocationData node, final World world, GLFactory objectFactory,
			Color antenna, Color box) {
		Preconditions.checkNotNull(node);
		Preconditions.checkNotNull(ctx);
		// add objects at camera height
		GeoObj g = new GeoObj(node.getLocation().getLatitude(), node.getLocation().getLongitude(), 0);

		g.setComp(Obj3DSensorNode.createMesh(antenna, box));

		g.setComp(new ProximitySensor(camera, 3f) {
			@Override
			public void onObjectIsCloseToCamera(GLCamera myCamera2, Obj obj, MeshComponent m, float currentDistance) {
				new CommandShowToast(ctx.ctx(), "Collision with " + node.getNodeId()).execute();
				// world.remove(obj);
			}

		});

		g.setOnClickCommand(new CommandShowToast(ctx.ctx(), "Tapped on node " + node.getNodeId()));
		world.add(g);

	}

	public void refreshNodes(List<NodeLocationData> nodes) {
		// TODO
	}

}
