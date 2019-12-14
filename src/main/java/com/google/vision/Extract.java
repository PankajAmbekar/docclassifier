package com.google.vision;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Vertex;

public class Extract {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		FileInputStream fi = new FileInputStream(new File("F:\\googleResp.txt"));
		ObjectInputStream oi = new ObjectInputStream(fi);
		AnnotateImageResponse res = (AnnotateImageResponse) oi.readObject();
		oi.close();
		extract(res);
	}

	static void extract(AnnotateImageResponse res) {

		List<EntityAnnotation> list = res.getTextAnnotationsList()
				/*
				 * .stream().filter(a -> { return
				 * a.getBoundingPoly().getVertices(0).getY() <= (height*0.3);
				 * }).collect(Collectors.toList())
				 */.stream().filter(a -> {
					return a.getDescription().matches("Standard|474165");
				}).collect(Collectors.toList());

		for (EntityAnnotation annotation : list) {
			if (annotation.getDescription().equalsIgnoreCase("Standard")) {
				BoundingPoly poly = annotation.getBoundingPoly();
				Vertex topLeft = poly.getVertices(0);
				Vertex topRight = poly.getVertices(1);
				Vertex bottomRight = poly.getVertices(2);
				Vertex bottomLeft = poly.getVertices(3);
				
				
				

			}

		}

	}

}
