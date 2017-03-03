package com.model.generator.world.world_objects;

import com.model.utils.Position;
import com.model.utils.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 22.02.2017.
 */
public class Edge {
	private Position point1;
	private Position point2;
	private List<Vector> vectors;
	private List<Integer> dynamics;
	private List<Mountain> mountains;
	private int pikeHeight;
	private Vector offsetVector;
	private boolean isWorldBorder;

	public Edge(Position point1, Position point2) {
		this.point1 = point1;
		this.point2 = point2;
		vectors = new ArrayList<>();
		dynamics = new ArrayList<>();
		mountains = new ArrayList<>();
	}

	public Position getPoint1() {
		return point1;
	}

	public void setPoint1(Position point1) {
		this.point1 = point1;
	}

	public Position getPoint2() {
		return point2;
	}

	public void setPoint2(Position point2) {
		this.point2 = point2;
	}

	@Override
	public String toString() {
		return "Edge{" +
				"point1=" + point1.toString() +
				", point2=" + point2.toString() +
				'}';
	}

	public List<Vector> getVectors() {
		return vectors;
	}

	/**
	 * add vector projection to edge
	 * @param vector non-projected vector
	 */
	public void addVector(Vector vector) {
		// positive is enclosing
		dynamics.add(getDistance(new Position(vector.getX(), vector.getY(), 0)) -
				getDistance(vector.getEndPoint()));
		vectors.add(vector);
	}

	private int getDistance(Position pos) {
		int value = Math.abs((point2.getY() - point1.getY()) * pos.getX() -
				(point2.getX() - point1.getX()) * pos.getY() +
				point2.getX() * point1.getY() - point2.getY() * point1.getX());
		value = (int) Math.round((float) value / Math.sqrt(Math.pow(point2.getY() - point1.getY(), 2) +
				Math.pow(point2.getX() - point1.getX(), 2)));
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Edge edge = (Edge) o;

		if (point1 == null && edge.point1 == null && point2 == null && edge.point2 == null) return true;
		if (point1 == null && edge.point1 == null && point2.equals(edge.point2)) return true;
		if (point2 == null && edge.point2 == null && point1.equals(edge.point1)) return true;
		if (point1 == null && edge.point2 == null && point2.equals(edge.point1)) return true;
		if (point2 == null && edge.point1 == null && point1.equals(edge.point2)) return true;
		if (point2.equals(edge.point1) && point1.equals(edge.point2)) return true;
		if (point1.equals(edge.point1) && point2.equals(edge.point2)) return true;
		return false;
	}

	@Override
	public int hashCode() {
		int result = point1 != null ? point1.hashCode() : 0;
		result = 31 * result + (point2 != null ? point2.hashCode() : 0);
		return result;
	}

	public List<Mountain> getMountains() {
		return mountains;
	}

	public void addMountain(Mountain mountain) {
		mountains.add(mountain);
	}

	public List<Integer> getDynamics() {
		return dynamics;
	}

	public int getPikeHeight() {
		return pikeHeight;
	}

	public void setPikeHeight(int pikeHeight) {
		this.pikeHeight = pikeHeight;
	}

	public Vector getOffsetVector() {
		return offsetVector;
	}

	public void setOffsetVector(Vector offsetVector) {
		this.offsetVector = offsetVector;
	}

	public boolean isWorldBorder() {
		return isWorldBorder;
	}

	public void setWorldBorder(boolean worldBorder) {
		isWorldBorder = worldBorder;
	}
}