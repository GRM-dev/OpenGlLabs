using SharpGL;

namespace OpenGlProject.Graphic.Scene
{
    public static class Light
    {
        /// <summary>
        /// Initializes Lights
        /// </summary>
        /// <param name="gl">OpenGl instance</param>
        public static void InitLights(OpenGL gl)
        {
            float[] global_ambient = { 0.5f, 0.5f, 0.5f, 1.0f };
            float[] light0pos = { 0.0f, 5.0f, 10.0f, 1.0f };
            float[] light0ambient = { 0.2f, 0.2f, 0.2f, 1.0f };
            float[] light0diffuse = { 0.3f, 0.3f, 0.3f, 1.0f };
            float[] light0specular = { 0.8f, 0.8f, 0.8f, 1.0f };

            float[] lmodel_ambient = { 0.2f, 0.2f, 0.2f, 1.0f };
            gl.LightModel(OpenGL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);

            gl.LightModel(OpenGL.GL_LIGHT_MODEL_AMBIENT, global_ambient);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_POSITION, light0pos);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_AMBIENT, light0ambient);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_DIFFUSE, light0diffuse);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_SPECULAR, light0specular);
            //gl.Enable(OpenGL.GL_LIGHTING);
            gl.Enable(OpenGL.GL_LIGHT0);

            gl.ShadeModel(OpenGL.GL_SMOOTH);
        }
    }
}
