package eu.grmdev.senryaku.graphic;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.handlers.KeyboardHandler;
import eu.grmdev.senryaku.core.handlers.MouseHandler;
import lombok.Getter;

public class GameWindow extends Thread {
	// The window handle
	private long window;
	// init() done
	@Getter
	private boolean ready;
	// should close game
	private boolean close;
	private KeyboardHandler keyCallback;
	private MouseHandler mouseCallback;
	
	public GameWindow() {
		setName("Render Thread");
	}
	
	@Override
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		try {
			init();
			System.out.println("Start Render Thread");
			loop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("Stop Render Thread");
			destroyWindow();
			Main.setGameClosed();
		}
	}
	
	private void init() throws Exception {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Configure GLFW
		if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW"); }
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		// Create the window
		window = glfwCreateWindow(800, 800, "Senryaku Shuriken", NULL, NULL);
		if (window == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }
		
		setWindowIcon();
		
		keyCallback = new KeyboardHandler();
		glfwSetKeyCallback(window, keyCallback);
		mouseCallback = new MouseHandler();
		glfwSetCursorPosCallback(window, mouseCallback);
		
		centerWindow();
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		// Make the window visible
		glfwShowWindow(window);
		// Mark as ready
		ready = true;
		GL.createCapabilities();
	}
	
	private void setWindowIcon() {
		try {
			File f = new File(getClass().getResource("/images/icon.png").toURI());
			if (!f.exists()) throw new IOException();
			
			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);
			IntBuffer comp = BufferUtils.createIntBuffer(1);
			ByteBuffer buf = STBImage.stbi_load(f.toString(), w, h, comp, 4);
			
			if (buf == null) { throw new IOException(STBImage.stbi_failure_reason()); }
			
			int imgWidth = w.get();
			int imgHeight = h.get();
			
			try (Buffer icons = GLFWImage.malloc(1)) {
				icons.position(0).width(imgWidth).height(imgHeight).pixels(buf);
				glfwSetWindowIcon(window, icons);
				STBImage.stbi_image_free(buf);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void centerWindow() {
		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);
			
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically
	}
	
	private void loop() {
		while (!shouldClose()) {
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	public boolean shouldClose() {
		if (glfwWindowShouldClose(window)) {
			close = true;
		}
		return close;
	}
	
	private void destroyWindow() {
		ready = false;
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
}
