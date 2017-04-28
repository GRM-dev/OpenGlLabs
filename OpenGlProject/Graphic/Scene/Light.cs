using System.Drawing;
using SharpGL;

namespace SenryakuShuriken.Graphic.Scene
{
    public static class Light
    {
        static readonly float[] global_ambient = { 0.5f, 0.5f, 0.5f, 1.0f };
        static readonly float[] light0pos = { 0.0f, 5.0f, 10.0f, 1.0f };
        static readonly float[] light0ambient = { 0.2f, 0.2f, 0.2f, 1.0f };
        static readonly float[] light0diffuse = { 0.3f, 0.3f, 0.3f, 1.0f };
        static readonly float[] light0specular = { 0.8f, 0.8f, 0.8f, 1.0f };

        /// <summary>
        /// Initializes Lights
        /// </summary>
        /// <param name="gl">OpenGl instance</param>
        public static void InitLights(OpenGL gl)
        {
            //float[] lmodel_ambient = { 0.2f, 0.2f, 0.2f, 1.0f };
            //gl.LightModel(OpenGL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);

            gl.LightModel(OpenGL.GL_LIGHT_MODEL_AMBIENT, global_ambient);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_POSITION, light0pos);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_AMBIENT, light0ambient);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_DIFFUSE, light0diffuse);
            gl.Light(OpenGL.GL_LIGHT0, OpenGL.GL_SPECULAR, light0specular);
            //gl.Enable(OpenGL.GL_LIGHTING);
            gl.Enable(OpenGL.GL_LIGHT0);

            gl.ShadeModel(OpenGL.GL_SMOOTH);
        }

        public static SharpGL.SceneGraph.Lighting.Light GetLight()
        {
            var l1 = new SharpGL.SceneGraph.Lighting.Light
            {
                Ambient = Color.FromArgb((int)(global_ambient[3] * 255), (int)(global_ambient[0] * 255),
                    (int)(global_ambient[1] * 255), (int)(global_ambient[2] * 255)),
                CastShadow = true,
                Diffuse = Color.FromArgb((int)(light0diffuse[3] * 255), (int)(light0diffuse[0] * 255),
                    (int)(light0diffuse[1] * 255), (int)(light0diffuse[2] * 255)),
                Specular = Color.FromArgb((int)(light0specular[3] * 255), (int)(light0specular[0] * 255),
                    (int)(light0specular[1] * 255), (int)(light0specular[2] * 255))
            };
            return l1;
        }
    }
}
