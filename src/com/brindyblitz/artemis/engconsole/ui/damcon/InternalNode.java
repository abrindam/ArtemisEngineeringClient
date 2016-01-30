package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import net.dhleong.acl.vesseldata.VesselNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.media.j3d.*;
import javax.vecmath.*;

public class InternalNode extends InternalSelectable {
    private VesselNode vesselNode;

    public InternalNode(VesselNode vessel_node) {
        alpha = 0.4f;

        this.vesselNode = vessel_node;

        this.branchGroup = new BranchGroup();

        Vector3f pos = new Vector3f(-vessel_node.getX(), vessel_node.getY(), vessel_node.getZ());
        pos.scale(SCALE);

        sphere = new Sphere(RADIUS, appearanceFromHealthPercentage());
        sphere.getShape(Sphere.BODY).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3f(Internal.internalPositionToWorldSpace(vessel_node)));

        TransformGroup tg = new TransformGroup();
        tg.setTransform(transform);
        tg.addChild(this.sphere);

        this.branchGroup.addChild(tg);

        Shape3D shape = this.sphere.getShape(Sphere.BODY);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setPickable(shape);
    }

    @Override
    public void updateHealth(float pct) {
        super.updateHealth(pct);
        sphere.setAppearance(appearanceFromHealthPercentage());
    }

    @Override
    protected boolean visible() {
        return isSystemNode();
    }

    private boolean isSystemNode() {
        return this.vesselNode.getSystem() != null;
    }

    @Override
    public String toString() {
        return "Node: " + this.vesselNode;
    }


    // TODO: Billboards
    // Icons for systems would be really nice
    //////////////////////
    // WIP: Billboards? //
    //////////////////////
    private InternalNode(Vector3d position, int IDONTWORKYET) {
        if (true) {
            throw new NotImplementedException();
        }

        // Billboards...
        // http://www.java2s.com/Code/Java/3D/Thisapplicationdemonstratestheuseofabillboardnode.htm
        // http://www.java2s.com/Code/Java/3D/Createsasimplerotatingscenethatincludestwotextbillboards.htm

        /////

        ColorCube color_cube = new ColorCube(0.05d);
        branchGroup = new BranchGroup();
        branchGroup.addChild(color_cube);

        //////

        Transform3D xform = new Transform3D();
        xform.set(position);

        TransformGroup xformGroup = new TransformGroup();
        xformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        xformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        xformGroup.setTransform(xform);

        ColorCube rightCube = new ColorCube();
        xformGroup.addChild(rightCube);

        /*Appearance app = new Appearance();
        Color3f ambientColour = new Color3f(1.0f, 1.0f, 0.0f);
        Color3f emissiveColour = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f diffuseColour = new Color3f(1.0f, 1.0f, 0.0f);
        float shininess = 20.0f;
        app.setMaterial(new Material(ambientColour, emissiveColour,
                diffuseColour, specularColour, shininess));
        Box leftCube = new Box(1.0f, 1.0f, 1.0f, app);
        xformGroup.addChild(leftCube);*/

        Billboard billboard = new Billboard(xformGroup, Billboard.ROTATE_ABOUT_AXIS, new Vector3f(0f, 1f, 0f));

        branchGroup = new BranchGroup();
        branchGroup.addChild(billboard);
    }
}