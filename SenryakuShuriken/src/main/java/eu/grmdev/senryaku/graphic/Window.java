package eu.grmdev.senryaku.graphic;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.Main;
import lombok.Getter;
import lombok.Setter;

public class Window {
	private @Getter final String title;
	private @Getter long windowHandle;
	private @Getter @Setter boolean resized;
	private @Getter @Setter boolean vSync;
	private @Getter final WindowOptions windowOptions;
	private @Getter final Matrix4f projectionMatrix;
	private @Getter Camera camera;
	
	public Window(String title, boolean vSync, WindowOptions opts, Camera camera) {
		this.title = title;
		this.windowOptions = opts;
		this.vSync = vSync;
		this.camera = camera;
		this.resized = false;
		this.projectionMatrix = new Matrix4f();
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW"); }
		createWindow();
		setupWindowCallbacks();
		if (!windowOptions.maximized) {
			centerWindow();
		}
		
		glfwMakeContextCurrent(windowHandle);
		
		if (isVSync()) {
			glfwSwapInterval(1);
		}
		
		GL.createCapabilities();
		
		setWindowIcon();
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		if (windowOptions.showTriangles) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		if (windowOptions.cullFace) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
		}
		glfwShowWindow(windowHandle);
	}
	
	/**
	 * Centers window on monitor
	 */
	private void centerWindow() {
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(windowHandle, (vidmode.width() - windowOptions.width) / 2, (vidmode.height() - windowOptions.height) / 2);
	}
	
	/**
	 * Sets the Windows Icon of game window
	 */
	private void setWindowIcon() {
		try (InputStream in = getClass().getResourceAsStream("/images/icon.png")) {
			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);
			IntBuffer comp = BufferUtils.createIntBuffer(1);
			File tempStyleSheetDest = File.createTempFile("app_lwjgl_logo", "");
			tempStyleSheetDest.deleteOnExit();
			Files.copy(in, tempStyleSheetDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			ByteBuffer buf = stbi_load(tempStyleSheetDest.toString(), w, h, comp, 4);
			if (buf == null) { throw new IOException(stbi_failure_reason()); }
			
			int imgWidth = w.get();
			int imgHeight = h.get();
			
			try (Buffer icons = GLFWImage.malloc(1)) {
				icons.position(0).width(imgWidth).height(imgHeight).pixels(buf);
				glfwSetWindowIcon(windowHandle, icons);
				stbi_image_free(buf);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds callbacks to FramebufferSize and Key events
	 */
	private void setupWindowCallbacks() {
		glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
			windowOptions.width = width;
			windowOptions.height = height;
			this.setResized(true);
		});
		glfwSetWindowCloseCallback(windowHandle, new GLFWWindowCloseCallback() {
			
			@Override
			public void invoke(long window) {
				Main.closeApp();
			}
		});
	}
	
	private void createWindow() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		if (windowOptions.compatibleProfile) {
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
		} else {
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		}
		if (windowOptions.antialiasing) {
			glfwWindowHint(GLFW_SAMPLES, 4);
		}
		
		if (windowOptions.width == 0 || windowOptions.height == 0) {
			windowOptions.width = 100;
			windowOptions.height = 100;
		}
		int max;
		if (windowOptions.maximized) {
			max = GLFW_TRUE;
		} else {
			max = GLFW_FALSE;
		}
		glfwWindowHint(GLFW_MAXIMIZED, max);
		
		windowHandle = glfwCreateWindow(windowOptions.width, windowOptions.height, title, NULL, NULL);
		if (windowHandle == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }
	}
	
	public void update() {
		glfwSwapBuffers(windowHandle);
		glfwPollEvents();
	}
	
	public void restoreState() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		if (windowOptions.cullFace) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
		}
	}
	
	public void setWindowTitle(String title) {
		glfwSetWindowTitle(windowHandle, title);
	}
	
	public Matrix4f updateProjectionMatrix() {
		float aspectRatio = (float) windowOptions.width / (float) windowOptions.height;
		return projectionMatrix.setPerspective(Config.FOV, aspectRatio, Config.Z_NEAR, Config.Z_FAR);
	}
	
	public static Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
		float aspectRatio = (float) width / (float) height;
		return matrix.setPerspective(Config.FOV, aspectRatio, Config.Z_NEAR, Config.Z_FAR);
	}
	
	public void setClearColor(float r, float g, float b, float alpha) {
		glClearColor(r, g, b, alpha);
	}
	
	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
	}
	
	public boolean windowShouldClose() {
		return glfwWindowShouldClose(windowHandle);
	}
	
	public int getWidth() {
		return windowOptions.width;
	}
	
	public int getHeight() {
		return windowOptions.height;
	}
	
	public static class WindowOptions {
		public int width;
		public int height;
		public boolean cullFace;
		public boolean showTriangles;
		public boolean showFps;
		public boolean compatibleProfile;
		public boolean antialiasing;
		public boolean frustumCulling;
		public boolean maximized;
	}
	
}
