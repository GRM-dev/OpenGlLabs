package eu.grmdev.senryaku.graphic;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import eu.grmdev.senryaku.Game;
import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.entity.Entity;
import eu.grmdev.senryaku.core.handlers.*;
import lombok.Getter;

public class GameWindow extends Thread {
	// The window handle
	private long window;
	// init() done
	@Getter
	private boolean ready;
	// should close game
	private boolean close;
	@Getter
	private KeyboardHandler keyCallback;
	@Getter
	private MouseHandler mouseCallback;
	protected int width;
	protected int height;
	private boolean sizeChanged = true;
	@Getter
	private ShaderHandler shaderHandler;
	private Game game;
	
	public GameWindow(Game game) {
		this.game = game;
		setName("Render Thread");
		shaderHandler = new ShaderHandler();
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
	
	/**
	 * Initializes glfw and lwjgl components
	 * 
	 * @throws Exception
	 *            when there was a problem during window creation
	 */
	private void init() throws Exception {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Configure GLFW
		if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW"); }
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		
		// Create the window
		window = glfwCreateWindow(800, 800, "Senryaku Shuriken", NULL, NULL);
		if (window == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }
		
		setWindowIcon();
		
		keyCallback = new KeyboardHandler();
		glfwSetKeyCallback(window, keyCallback);
		mouseCallback = new MouseHandler();
		glfwSetCursorPosCallback(window, mouseCallback);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int w, int h) {
				if (w > 0 && h > 0) {
					sizeChanged = true;
					width = w;
					height = h;
				}
			}
		});
		
		centerWindow();
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		// Make the window visible
		glfwShowWindow(window);
		GL.createCapabilities();
		shaderHandler.init();
		shaderHandler.use(0);
		// Mark as ready
		ready = true;
	}
	
	/**
	 * Sets the Windows Icon of game window
	 */
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
	
	/**
	 * Centers game windows on monitor
	 */
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
			clear();
			if (sizeChanged) {
				onSizeChanged();
			}
			shaderHandler.use(1);
			
			glLoadIdentity();
			
			Iterator<Entity> it = game.getEntityIterator();
			while (it.hasNext()) {
				Entity entity = it.next();
				glLoadIdentity();
				entity.render(this);
				glBindVertexArray(0);
			}
			shaderHandler.use(0);
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}
	
	private void onSizeChanged() {
		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		float aspect = (float) width / height;
		glLoadIdentity();
		glOrtho(-aspect, aspect, -1, 1, -1, 1);
		sizeChanged = false;
		glMatrixMode(GL_MODELVIEW);
	}
	
	private void clear() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/**
	 * Checks if game should stop.
	 * To stop just invoke {@link #setClose()}
	 * 
	 * @return true if 'glfw close value' is true
	 */
	public boolean shouldClose() {
		if (glfwWindowShouldClose(window)) {
			close = true;
		}
		return close;
	}
	
	/**
	 * Stops and closes the game
	 */
	public void setClose() {
		glfwSetWindowShouldClose(window, true);
	}
	
	/**
	 * Terminates the game window
	 */
	private void destroyWindow() {
		Iterator<Entity> it = game.getEntityIterator();
		while (it.hasNext()) {
			Entity entity = it.next();
			entity.delete();
			it.remove();
		}
		ready = false;
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}
