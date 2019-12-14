package com.google.vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

public class VisionApplication {
	static int height;
	static String filename;

	public static void main(String[] args) throws Exception {
		
		Files.walk(Paths.get("F:\\PP Training\\DocClassifier\\Test\\")).forEach(img -> {
			
			try {
				BufferedImage bimg = ImageIO.read(new File(img.toAbsolutePath().toString()));
				height = bimg.getHeight();
				detectText(img.toAbsolutePath().toString(), System.out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
//		filename = "F:\\PP Training\\DocClassifier\\Dummy-PO_02_shadows_out_norm.jpg";
		
//		detectText(filename, System.out);

	}

	public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
		List<AnnotateImageRequest> requests = new ArrayList<>();

		ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

		Image img = Image.newBuilder().setContent(imgBytes).build();
		img.getSerializedSize();
		Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();
			/*FileInputStream fi = new FileInputStream(new File("F:\\googleResp.txt"));
			ObjectInputStream oi = new ObjectInputStream(fi);
			AnnotateImageResponse res = (AnnotateImageResponse) oi.readObject();
			oi.close();*/
			AnnotateImageResponse res = responses.get(0);
			List<EntityAnnotation> list = res.getTextAnnotationsList().stream().filter(a -> {
				return a.getBoundingPoly().getVertices(0).getY() <= (height*0.3);
			}).collect(Collectors.toList())/*.stream().filter(a -> {
				return a.getDescription().matches("Standard|474165");
			}).collect(Collectors.toList())*/;
			
			// For full list of available annotations, see
			// http://g.co/cloud/vision/docs
			boolean flag = true;
			Map<String, Integer> map = new HashMap<>();
			for (EntityAnnotation annotation : list) {
				String key = annotation.getDescription().toLowerCase();
				if(map.containsKey(key)){
					map.put(key, map.get(key)+1);
				} else {
					map.put(key, 1);
				}
				
				
//				if (annotation.getDescription().equalsIgnoreCase("Standard")) {
//					/*if(flag){
//						flag =  !flag;
//						continue;
//					}*/
//					BoundingPoly poly = annotation.getBoundingPoly();
//					Vertex topLeft = poly.getVertices(0);
//					Vertex topRight = poly.getVertices(1);
//					Vertex bottomRight = poly.getVertices(2);
//					Vertex bottomLeft = poly.getVertices(3);
//
//					float yRange = bottomLeft.getY() - topLeft.getY();
//					float xRange = topRight.getX() - topLeft.getX();
//
//					float nextTopLeftXMaxStart = bottomLeft.getX() + 10;
//					float nextTopLeftYMaxStart = bottomLeft.getY() + 10;
//					float nextTopRightXMaxStart = bottomRight.getX() + 10;
//					float nextTopRightYMaxStart = bottomRight.getY() + 10;
//
//					for (EntityAnnotation next : list) {
//						if(next.equals(annotation)) continue;
//						BoundingPoly polyNext = next.getBoundingPoly();
//						Vertex topLeftNext = polyNext.getVertices(0);
//						if (Math.abs(topLeftNext.getX() - topLeft.getX()) > 200
//								&& Math.abs(topLeftNext.getY() - topLeft.getY()) > 200) {
//							continue;
//						}
//						Vertex topRightNext = polyNext.getVertices(1);
//						Vertex bottomLeftNext = polyNext.getVertices(2);
//						Vertex bottomRightNext = polyNext.getVertices(3);
//						
//						boolean topLeftXInRange = Math.abs((topLeftNext.getX()+5) - nextTopLeftXMaxStart) <=15; 
//						boolean topLeftYInRange = Math.abs((topLeftNext.getY()+5) - nextTopLeftYMaxStart) <=15 ;
//						boolean topRightXInRange = Math.abs((topRightNext.getX()+5) - nextTopRightXMaxStart) <=15; 
//						boolean topRightYInRange = Math.abs((topRightNext.getY()+5)- nextTopRightYMaxStart) <=15;
//						boolean wordHeightInRange = Math.abs((bottomLeftNext.getY() - topLeftNext.getY())- yRange) <=10;
//						boolean wordLengthInRange = Math.abs((topRightNext.getY() - topLeftNext.getY())- yRange) <=10;
//						
//						if(topLeftXInRange && topLeftYInRange && topRightXInRange && topRightYInRange){
//							System.out.println(next.getDescription());
//						}
//						
//					}
//
//				}
//
			}
			String path = "";
			if(map.containsKey("purchase")){
				path = "F:\\Output\\Purchases\\";
			} else{
				path = "F:\\Output\\Invoices\\";
			}
			FileUtils.copyFileToDirectory(new File(filePath), new File(path));
	}
	}
}
