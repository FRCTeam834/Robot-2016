package org.usfirst.frc.team834.robot;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openkinect.freenect.Context;
import org.openkinect.freenect.DepthFormat;
import org.openkinect.freenect.DepthHandler;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.Freenect;
import org.openkinect.freenect.Resolution;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.MatchShapeResult;
import com.ni.vision.NIVision.MorphologyMethod;
import com.ni.vision.NIVision.Point;
import com.ni.vision.NIVision.RawData;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ShapeMode;
import com.ni.vision.NIVision.ShapeReport;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

import edu.wpi.first.wpilibj.CameraServer;
//using cameraserver2 for line 227 fix
//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;

public enum KinectVision {
	INSTANCE;
	
	public static final double KINCET_HALF_FOV = 28.5;
	public static final double GOAL_VALIDITY_TIME = 1.0, GOAL_SCORE_THRESHOLD = 500.0;
	public static final int ANGLE_ERROR_SAMPLE_COUNT = 5;
	public static final int GOAL_SIDE_STRIPE_COLUMNS = 5;
	
	public static final int DASHBOARD_FRAME_UPDATE_PERIOD = 100;
	
	static {
		NativeLibrary.addSearchPath("nivision", "/usr/local/natinst/lib");
		NativeLibrary instance = NativeLibrary.getInstance("nivision");
		Native.register(instance);
	}
	
	private Context kinectContext;
	private Device kinect;
	
	private Image videoImage, videoProcessed, depthImage;
	private Image dashboardImage, goalPattern;
	
	private volatile double goalScore, degreesToGoal;
	private volatile double[] angleErrors = new double[ANGLE_ERROR_SAMPLE_COUNT];
	private volatile int nextAngleErrorIndex = 0;
	private volatile double lastGoalFoundTime = Double.NaN;
	private Rectangle goalBoundingBox = new Rectangle(), depthGoalBoundingBox = new Rectangle();
	private volatile double goalDistanceMM = Double.NaN;
	
	private final List<GoalListener> listeners = new LinkedList<>();
	
	private Thread dashboardThread = null;
	
	private DepthHandler depthHandler = new DepthHandler() {
		@Override
		public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
			if (mode == null || !mode.isValid()) return;
			int width = mode.getWidth();
			int height = mode.getHeight();
			
			NIVision.imaqArrayToImage(depthImage, new RawData(frame), width, height);
			
			boolean goalFound;
			synchronized (KinectVision.this) {
				if ((goalFound = isGoalValid())) {
					goalBoundingBox.copyToRectangle(depthGoalBoundingBox);
				}
			}
			if (goalFound) {
				long distanceSum = 0, pixelCount = 0;
				
				int goalTop = Math.max(depthGoalBoundingBox.top, 0);
				int goalBottom = Math.min(depthGoalBoundingBox.top + depthGoalBoundingBox.height, height);
				int goalLeftStripeStart = Math.max(depthGoalBoundingBox.left - GOAL_SIDE_STRIPE_COLUMNS, 0);
				int goalLeftStripeEnd = Math.max(depthGoalBoundingBox.left, 0);
				int goalRightStripeStart = Math.min(depthGoalBoundingBox.left + depthGoalBoundingBox.width, width);
				int goalRightStripeEnd = Math.min(depthGoalBoundingBox.left + depthGoalBoundingBox.width + GOAL_SIDE_STRIPE_COLUMNS, width);
				
				for (int row = goalTop; row < goalBottom; row++) {
					frame.position(Short.BYTES * (row * width + goalLeftStripeStart));
					for (int col = goalLeftStripeStart; col < goalLeftStripeEnd; col++) {
						int pixelDistance = frame.getShort() & 0xFFFF;
						if (pixelDistance > 0) {
							distanceSum += pixelDistance;
							pixelCount++;
						}
					}
					frame.position(Short.BYTES * (row * width + goalRightStripeStart));
					for (int col = goalRightStripeStart; col < goalRightStripeEnd; col++) {
						int pixelDistance = frame.getShort() & 0xFFFF;
						if (pixelDistance > 0) {
							distanceSum += pixelDistance;
							pixelCount++;
						}
					}
				}
				frame.rewind();
				
				goalDistanceMM = (double) distanceSum / (double) pixelCount;
			}
			
			multiplyConstant(depthImage, depthImage, 5);
			NIVision.imaqCopyRect(dashboardImage, depthImage, new Rect(0, 0, height, width), new Point(640, 0));
		}
	};
	
	private VideoHandler videoHandler = new VideoHandler() {
		@Override
		public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
			if (mode == null || !mode.isValid()) return;
			int width = mode.getWidth();
			int height = mode.getHeight();
			
			NIVision.imaqArrayToImage(videoImage, new RawData(frame), width, height);

			//NIVision.imaqThreshold(videoImage, videoImage, 3 << 8, 1 << 10, 0, 0);
			NIVision.imaqGrayMorphology(videoImage, videoImage, MorphologyMethod.CLOSE, null);
			cast(videoProcessed, videoImage, ImageType.IMAGE_U8, 2);
			NIVision.imaqThreshold(videoProcessed, videoProcessed, 3 << 6, 1 << 8, 1, 1);
			//NIVision.imaqThreshold(videoProcessed, videoProcessed, 1, 1 << 8, 1, 1);
			//NIVision.imaqThreshold(videoImage, videoImage, 150, 255, 1, 255);
			//NIVision.imaqMorphology(videoImage, videoImage, MorphologyMethod.DILATE, null);
			//NIVision.imaqDuplicate(processed, image);
			
			MatchShapeResult shapeResult = NIVision.imaqMatchShape(videoProcessed, videoProcessed, goalPattern, 1, 1, 0.5);
			ShapeReport[] shapeReports = shapeResult.array;
			if (shapeReports.length > 0) {
				ShapeReport max = null;
				for (int i = 0; i < shapeReports.length; i++) {
					if (shapeReports[i].score < GOAL_SCORE_THRESHOLD) continue;
					max = (max == null || shapeReports[i].size > max.size) ? shapeReports[i] : max;
				}
				if (max != null) {
					synchronized (KinectVision.this) {
						goalScore = max.score;
						double imageCenter = (double) width / 2.0;
						degreesToGoal = (double) (max.centroid.x - imageCenter) / imageCenter * KINCET_HALF_FOV;
						goalBoundingBox.copyFromRect(max.coordinates);
						angleErrors[nextAngleErrorIndex] = degreesToGoal;
						lastGoalFoundTime = Timer.getFPGATimestamp();
					}

					NIVision.imaqDrawShapeOnImage(videoImage, videoImage, new Rect(max.centroid.y - 2, max.coordinates.left, 5, max.coordinates.width), DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, (1 << 9));
					NIVision.imaqDrawShapeOnImage(videoImage, videoImage, new Rect(max.coordinates.top, max.centroid.x - 2, max.coordinates.height, 5), DrawMode.PAINT_VALUE, ShapeMode.SHAPE_RECT, (1 << 9));
					//NIVision.imaqDrawLineOnImage(videoImage, videoImage, DrawMode.DRAW_VALUE, new Point(max.coordinates.left, max.centroid.y), new Point(max.coordinates.left + max.coordinates.width, max.centroid.y), (1 << 10) - 1);
					//NIVision.imaqDrawLineOnImage(videoImage, videoImage, DrawMode.DRAW_VALUE, new Point(max.centroid.x, max.coordinates.top), new Point(max.centroid.x, max.coordinates.top + max.coordinates.height), (1 << 10) - 1);
				} else {
					synchronized (KinectVision.this) {
						angleErrors[nextAngleErrorIndex] = KINCET_HALF_FOV;
					}
				}
			} else {
				synchronized (KinectVision.this) {
					angleErrors[nextAngleErrorIndex] = KINCET_HALF_FOV;
				}
			}
			nextAngleErrorIndex = (nextAngleErrorIndex + 1) % ANGLE_ERROR_SAMPLE_COUNT;
			
			for (GoalListener listener : listeners) listener.handleGoal(isGoalValid(), degreesToGoal, goalDistanceMM);

			//NIVision.imaqMathTransform(image, image, MathTransformMethod.TRANSFORM_SQRT, 0, 255, 0, null);
			//cast(image, image, ImageType.IMAGE_U16, 0);
			//multiplyConstant(image, image, 256);
			multiplyConstant(videoImage, videoImage, 1 << 6);
			NIVision.imaqCopyRect(dashboardImage, videoImage, new Rect(0, 0, height, width), new Point(0, 0));
		}
	};
	
	public void init() {
		Arrays.fill(angleErrors, KINCET_HALF_FOV);
		
		videoImage = NIVision.imaqCreateImage(ImageType.IMAGE_U16, 8);
		videoProcessed = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 8);
		depthImage = NIVision.imaqCreateImage(ImageType.IMAGE_U16, 0);		
		
		dashboardImage = NIVision.imaqCreateImage(ImageType.IMAGE_U16, 0);
		NIVision.imaqSetImageSize(dashboardImage, 1280, 500);
		goalPattern = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		NIVision.imaqReadFile(goalPattern, "/usr/local/nivision/GoalTemplate.png");
		
		kinectContext = Freenect.createContext();
		int deviceCount = kinectContext.numDevices();
		System.out.println("Kinect context opened, device count = " + deviceCount);
		if (deviceCount > 0) {
			kinect = kinectContext.openDevice(0);
			System.out.println("Kinect opened");
			
			kinect.setIRBrightness(50);
			
			kinect.setDepthFormat(DepthFormat.MM, Resolution.MEDIUM);
			kinect.setVideoFormat(VideoFormat.IR_10BIT, Resolution.MEDIUM);
			kinect.startDepth(depthHandler);
			kinect.startVideo(videoHandler);
			System.out.println("Kinect capture started");
		} else {
			System.out.println("No Kinects found");
		}
	}
	
	public double getGoalScore() { return goalScore; }
	public double getDegreesToGoal() { return degreesToGoal; }
	public synchronized double getAverageAngleError() {
		double sum = 0.0;
		for (int i = 0; i < ANGLE_ERROR_SAMPLE_COUNT; i++) sum += angleErrors[i];
		return sum / (double) ANGLE_ERROR_SAMPLE_COUNT;
	}
	public double getTimeSinceGoalFound() { return Timer.getFPGATimestamp() - lastGoalFoundTime; }
	public double getGoalDistance() { return goalDistanceMM; }
	
	public boolean isGoalValid() {
		return getTimeSinceGoalFound() < GOAL_VALIDITY_TIME && goalScore > GOAL_SCORE_THRESHOLD;
	}
	
	public void startDashboardFeed() {
		stopDashboardFeed();
		dashboardThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					long start = System.currentTimeMillis();
					CameraServer.getInstance().setImage(dashboardImage);
					long end = System.currentTimeMillis();
					long remaining = DASHBOARD_FRAME_UPDATE_PERIOD - (end - start);
					if (remaining > 0) {
						try {
							Thread.sleep(remaining);
						} catch (InterruptedException e) { }
					} else {
						Thread.yield();
					}
				}
			}
		});
		dashboardThread.start();
	}
	
	public void stopDashboardFeed() {
		if (dashboardThread != null) {
			dashboardThread.interrupt();
		}
	}
	
	public void addGoalListener(GoalListener listener) { listeners.add(listener); }
	public void removeGoalListener(GoalListener listener) { listeners.remove(listener); }
	
	public static interface GoalListener {
		void handleGoal(boolean isValid, double degreesToGoal, double distance);
	}
	
	public static class Rectangle {
		public volatile int top, left, width, height;
		
		public void copyFromRect(Rect r) {
			top = r.top;
			left = r.left;
			width = r.width;
			height = r.height;
		}
		public void copyToRectangle(Rectangle r) {
			r.top = top;
			r.left = left;
			r.width = width;
			r.height = height;
		}
	}
	
	private static void cast(Image dest, Image source, ImageType type, int shift) {
		imaqCast(new Pointer(dest.getAddress()), new Pointer(source.getAddress()), type.getValue(), new Pointer(0), shift);
	}
	private static void divideConstant(Image dest, Image source, float value) {
		imaqDivideConstant(new Pointer(dest.getAddress()), new Pointer(source.getAddress()), Float.floatToRawIntBits(value));
	}
	private static void multiplyConstant(Image dest, Image source, float value) {
		imaqMultiplyConstant(new Pointer(dest.getAddress()), new Pointer(source.getAddress()), Float.floatToRawIntBits(value));
	}
	private static native void imaqCast(Pointer dest, Pointer source, int type, Pointer lookup, int shift);
	private static native void imaqDivideConstant(Pointer dest, Pointer source, long value);
	private static native void imaqMultiplyConstant(Pointer dest, Pointer source, long value);
}
