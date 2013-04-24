/*
 * Created on Nov 10, 2005
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package ray.accel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.surface.Surface;


/**
 * @author arbree
 * Nov 10, 2005
 * BoundingVolume.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class BoundingVolume implements AccelerationStructure {
	
	/** The maximum number of surfaces in a leaf node */
	public static final int MAX_SURFACES_PER_LEAF = 10;
	
	/** The bounding box of this volume */
	protected final AxisAlignedBoundingBox box = new AxisAlignedBoundingBox();
	
	/** The surfaces contained in this bounding volume */
	protected ArrayList<Surface> surfaces = new ArrayList<Surface>();
	
	/** The children bounding volumes of this node */
	protected BoundingVolume left = null;
	protected BoundingVolume right = null;
	
	/** The depth of this node */
	protected final int depth;
	
	/**
	 * Private constructor used by split()
	 */
	private BoundingVolume(int inDepth) {
		depth = inDepth;
	}
	
	public AxisAlignedBoundingBox getBoundingBox() {
		return box;
	}
	
	/** Construct a bounding volume for the given surfaces, subdividing if necessary.
	 * @param inSurfaces
	 */
	public BoundingVolume(ArrayList<Surface> inSurfaces) {
		
		depth = 0;
		
		//Add all the input surfaces to ourselves
		surfaces.addAll(inSurfaces);
		growToHold();
		
		System.out.println("Volume contains "+inSurfaces.size()+" objects.");
		
		//Divide if necessary
		split();
		
		System.out.println(box);
		
	}
	
	/**
	 * Grow the bounding volume to hold all the objects it encloses.
	 */
	private void growToHold() {
		
		//Grow our bounding box
		for (Iterator<Surface> iter = surfaces.iterator(); iter.hasNext();) {
			Surface currSurface = (Surface) iter.next();
			currSurface.addToBoundingBox(box);
		}
	}
	
	/**
	 * Split this bounding volume into two children
	 */
	private void split() {
		
		// If we are small enough, stop
		if(surfaces.size() < MAX_SURFACES_PER_LEAF) {
			surfaces.trimToSize();
			return;
		}
		
		//Create children
		left = new BoundingVolume(depth + 1);
		right = new BoundingVolume(depth + 1);
		
		//Break box along longest axis
		int axis = box.longestAxis();//depth % 3;
		Comparator<Surface> compare = null;
		switch(axis) {
		case AxisAlignedBoundingBox.X:
			compare = Surface.X_COMPARE;
			break;
		case AxisAlignedBoundingBox.Y:
			compare = Surface.Y_COMPARE;
			break;
		case AxisAlignedBoundingBox.Z:
			compare = Surface.Z_COMPARE;
			break;
		}
		
		//Sort the surfaces
		Collections.sort(surfaces, compare);
		
		//Put each half in the children
		ArrayList<Surface> leftList = new ArrayList<Surface>();
		List<Surface> firstHalf = surfaces.subList(0, surfaces.size()/2);
		leftList.addAll(firstHalf);
		firstHalf.clear();
		
		//Set the object lists and clear ours
		left.surfaces = leftList;
		right.surfaces = this.surfaces;
		this.surfaces = null;
		
		//Grow children to fit
		left.growToHold();
		right.growToHold();
		
		left.split();
		right.split();
		
	}
	
	/**
	 * Set outRecord to the first intersection of ray with this bounding volume. Return true
	 * if there was an intersection and false otherwise. If no intersection was
	 * found outRecord is unchanged.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intesect
	 * @return true if and intersection is found.
	 */
	public boolean getFirstIntersection(IntersectionRecord outRecord, Ray ray) {
		
		//Check that the ray intersects the box
		if(!box.intersect(ray))
			return false;
		
		//If we are a leaf, intersect our objects
		if(left == null && right == null) {
			
			//Find the first intersect by testing all surfaces
			double bestT = Double.MAX_VALUE;
			IntersectionRecord workRec = new IntersectionRecord();
			for (Iterator<Surface> iter = surfaces.iterator(); iter.hasNext();) {
				Surface currSurface = (Surface) iter.next();
				if(currSurface.intersect(workRec, ray) && workRec.t < bestT) {
					outRecord.set(workRec);
					bestT = workRec.t;
				}
			}
			
			return bestT != Double.MAX_VALUE;
			
		}
		
		//Check the left child
		IntersectionRecord leftRecord = new IntersectionRecord();
		if(left != null && left.getFirstIntersection(leftRecord, ray)) {
			
			//Shorten ray to hit point
			ray.end = leftRecord.t;
			
			//Check right child, the intersection must be closer than left
			IntersectionRecord rightRecord = new IntersectionRecord();
			if(right != null && right.getFirstIntersection(rightRecord, ray))
				outRecord.set(rightRecord);
			else outRecord.set(leftRecord);
			
			return true;
			
		}
		
		//Otherwise return the result of the right child
		return right != null && right.getFirstIntersection(outRecord, ray);
		
	}
	
	public boolean getAnyIntersection(IntersectionRecord outRecord, Ray ray) {
		//could do this more efficiently.
		return getFirstIntersection(outRecord, ray);
	}
	
}
