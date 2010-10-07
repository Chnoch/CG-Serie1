import jrtr.*;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.vecmath.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and shows a
 * rotating cube.
 */
public class simple {
    static RenderPanel renderPanel;
    static RenderContext renderContext;
    static SimpleSceneManager sceneManager;
    static Shape shape,shape2,shape3;
    static float angle;


    /**
     * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to provide
     * a call-back function for initialization.
     */
    public final static class SimpleRenderPanel extends GLRenderPanel {
        /**
         * Initialization call-back. We initialize our renderer here.
         * @param r
         *            the render context that is associated with this render
         *            panel
         */
        public void init(RenderContext r) {
            renderContext = r;
            renderContext.setSceneManager(sceneManager);

            // Register a timer task
            Timer timer = new Timer();
            angle = 0.01f;
            timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
        }
    }

    /*
    	/**
    	 * A timer task that generates an animation. This task triggers
    	 * the redrawing of the 3D scene every time it is executed.
    	 */
    public static class AnimationTask extends TimerTask {
        public void run() {
            // Update transformation
            Matrix4f t = shape.getTransformation();
            Matrix4f rotX = new Matrix4f();
            rotX.rotX(angle);
            Matrix4f rotY = new Matrix4f();
            rotY.rotY(angle);
//            t.mul(rotX);
            t.mul(rotY);
            shape.setTransformation(t);
            
            Matrix4f t2 =  shape2.getTransformation();
            Matrix4f rotY2 = new Matrix4f();
            Matrix4f rotX2 = new Matrix4f();
            Matrix4f rotZ2 = new Matrix4f();
            rotX2.rotX(angle);
            rotY2.rotY(angle);
            rotZ2.rotZ(angle);
//            t2.mul(rotX2);
//            t2.mul(rotY2);
            rotY2.mul(t2);
            rotY2.mul(rotX2);
//            t2.mul(rotZ2);
            shape2.setTransformation(rotY2);
            
            Matrix4f t3 =  shape3.getTransformation();
            Matrix4f rotY3 = new Matrix4f();
            Matrix4f rotX3 = new Matrix4f();
            Matrix4f rotZ3 = new Matrix4f();
            rotX3.rotX(angle);
            rotY3.rotY(angle);
            rotZ3.rotZ(angle);
//            t3.mul(rotX3);
            rotY3.mul(t3);
//            t3.mul(rotZ3);
            rotY3.mul(rotX3);
//            rotX3.mul(t3);
//            rotZ3.mul(t3);
            shape3.setTransformation(rotY3);

            // Trigger redrawing of the render window
            renderPanel.getCanvas().repaint();
        }
    }

    /**
     * A mouse listener for the main window of this application. This can be
     * used to process mouse events.
     */
    public static class SimpleMouseListener implements MouseListener {
        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }
    }

    public static Shape makeCylinder(int resolution, float x, float y, float z, float scale) {
        float cylinder[], c[];
        int indices[];
        double angle = (Math.PI * 2) / resolution;
        cylinder = new float[2 * 3 * resolution];
        // top
        int a = -1;
        for (int i = 0; i < resolution; i++) {
            cylinder[++a] = scale*(float) Math.cos(i * angle)+x;
            cylinder[++a] = scale*(float) Math.sin(i * angle)+y;
            cylinder[++a] = scale+z;
        }

        // bottom
        for (int i = 0; i < resolution; i++) {
            cylinder[++a] = scale*(float) Math.cos(i * angle)+x;
            cylinder[++a] = scale*(float) Math.sin(i * angle)+y;
            cylinder[++a] = -1*scale+z;
        }

        // colors
        c = new float[2 * 3 * resolution];
        a = -1;
        for (int i = 0; i < resolution; i++) {
            c[++a] = 1;
            c[++a] = 1;
            c[++a] = 1;

            c[++a] = 0;
            c[++a] = 0;
            c[++a] = 0;
        }

        // 2*3*(resolution-2) (top and bottom) + 6*resolution (sides)
        indices = new int[12 * resolution - 12];
        a = -1;

        // top
        for (int i = 0; i < resolution - 2; i++) {
            indices[++a] = 0;
            indices[++a] = (i + 1);
            indices[++a] = (i + 2);
        }

        // bottom
        for (int i = resolution; i < 2 * resolution - 2; i++) {
            indices[++a] = resolution;
            indices[++a] = (i + 2);
            indices[++a] = (i + 1);
        }

        // sides
        for (int i = 0; i < resolution - 1; i++) {
            indices[++a] = i;
            indices[++a] = resolution + i;
            indices[++a] = resolution + i + 1;

            indices[++a] = i;
            indices[++a] = resolution + i + 1;
            indices[++a] = (i + 1) % resolution;

        }
        // correction for last side
        indices[++a] = resolution - 1;
        indices[++a] = 2 * resolution - 1;
        indices[++a] = resolution;

        indices[++a] = resolution - 1;
        indices[++a] = resolution;
        indices[++a] = 0;

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = new VertexData(cylinder.length / 3);
        vertexData.addElement(cylinder, VertexData.Semantic.POSITION, 3);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);

        // The triangles (three vertex indices for each triangle)
        /*int indices[] = {0,2,3, 0,1,2,            // front face
                         4,6,7, 4,5,6,          // left face
                         8,10,11, 8,9,10,       // back face
                         12,14,15, 12,13,14,    // right face
                         16,18,19, 16,17,18,    // top face
                         20,22,23, 20,21,22};   // bottom face
        */
        vertexData.addIndices(indices);

        // Make a scene manager and add the object
        return new Shape(vertexData);
    }

    public static Shape makeBall(int resolution) {
        float c[], ball[];
        int indices[];
        
        double phi = (Math.PI * 2) / resolution;
        double theta = (Math.PI) / 2*resolution;
        ball = new float[(2 * (resolution - 2) * 3 * resolution) + 3
                * resolution + 6];
        // top
        int a = -1;
        for (int i = 0; i < ball.length / 3; i++) {
            ball[++a] = (float) (Math.cos(i * phi) * Math.sin(i * theta));
            ball[++a] = (float) (Math.sin(i * phi) * Math.sin(i * theta));
            ball[++a] = (float) Math.cos(i * theta);
        }

        // colors
        c = new float[2 * 3 * resolution];
        a = -1;
        for (int i = 0; i < resolution; i++) {
            c[++a] = 1;
            c[++a] = 1;
            c[++a] = 1;

            c[++a] = 0;
            c[++a] = 0;
            c[++a] = 0;
        }

        // 2*3*(resolution-2) (top and bottom) + 6*resolution (sides)
        indices = new int[12 * resolution - 12];
        a = -1;

        // top
        for (int i = 0; i < resolution - 2; i++) {
            indices[++a] = 0;
            indices[++a] = (i + 1);
            indices[++a] = (i + 2);
        }

        // bottom
        for (int i = resolution; i < 2 * resolution - 2; i++) {
            indices[++a] = resolution;
            indices[++a] = (i + 2);
            indices[++a] = (i + 1);
        }

        // sides
        for (int i = 0; i < resolution - 1; i++) {
            indices[++a] = i;
            indices[++a] = resolution + i;
            indices[++a] = resolution + i + 1;

            indices[++a] = i;
            indices[++a] = resolution + i + 1;
            indices[++a] = (i + 1) % resolution;

        }
        // correction for last side
        indices[++a] = resolution - 1;
        indices[++a] = 2 * resolution - 1;
        indices[++a] = resolution;

        indices[++a] = resolution - 1;
        indices[++a] = resolution;
        indices[++a] = 0;
        
        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = new VertexData(ball.length / 3);
        vertexData.addElement(ball, VertexData.Semantic.POSITION, 3);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);

        // The triangles (three vertex indices for each triangle)
        /*int indices[] = {0,2,3, 0,1,2,            // front face
                         4,6,7, 4,5,6,          // left face
                         8,10,11, 8,9,10,       // back face
                         12,14,15, 12,13,14,    // right face
                         16,18,19, 16,17,18,    // top face
                         20,22,23, 20,21,22};   // bottom face
        */
        vertexData.addIndices(indices);

        // Make a scene manager and add the object
        return new Shape(vertexData);
    }

    /**
     * The main function opens a 3D rendering window, constructs a simple 3D
     * scene, and starts a timer task to generate an animation.
     */
    public static void main(String[] args) {

        Shape leftWheel= makeCylinder(10, 1.5f,0,0, 0.5f);
        Shape rightWheel = makeCylinder(10, 4.5f,0,0, 0.5f);
        Shape mainCylinder = makeCylinder(4, 3,0,0, 1f);
        Shape ball = makeBall(4);

        shape = mainCylinder;
        shape2 = leftWheel;
        shape3 = rightWheel;
        // Make a scene manager and add the object
        sceneManager = new SimpleSceneManager();
        sceneManager.addShape(shape);
        sceneManager.addShape(shape2);
        sceneManager.addShape(shape3);

        // Make a render panel. The init function of the renderPanel
        // (see above) will be called back for initialization.
        renderPanel = new SimpleRenderPanel();

        // Make the main window of this application and add the renderer to it
        JFrame jframe = new JFrame("Cylinder");
        jframe.setSize(500, 500);
        jframe.setLocationRelativeTo(null); // center of screen
        jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas
        // into a JFrame
        // window

        // Add a mouse listener
        jframe.addMouseListener(new SimpleMouseListener());

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true); // show window
    }
}

// Make a simple geometric object: a cube

// The vertex positions of the cube
/*float v[] = { -1,-1,1, 1,-1,1, 1,1,1, -1,1,1, // front face
        -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, // left face
        1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, // back face
        1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, // right face
        1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top face
        -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom face
 */
// The vertex colors
/*float c[] = {1,0,0, 1,0,0, 1,0,0, 1,0,0,
             0,1,0, 0,1,0, 0,1,0, 0,1,0,
             1,0,0, 1,0,0, 1,0,0, 1,0,0,
             0,1,0, 0,1,0, 0,1,0, 0,1,0,
             0,0,1, 0,0,1, 0,0,1, 0,0,1,
             0,0,1, 0,0,1, 0,0,1, 0,0,1};
*/

// The triangles (three vertex indices for each triangle)
/*int indices[] = {0,2,3, 0,1,2,            // front face
                 4,6,7, 4,5,6,          // left face
                 8,10,11, 8,9,10,       // back face
                 12,14,15, 12,13,14,    // right face
                 16,18,19, 16,17,18,    // top face
                 20,22,23, 20,21,22};   // bottom face
*/
