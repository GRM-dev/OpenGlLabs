using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core;
using SharpGL;
using SharpGL.SceneGraph.Shaders;

namespace OpenGlProject.Graphic.Scene
{
    /// <summary>
    /// All OpenGl objects container for displaying
    /// </summary>
    public class Scene
    {
        private readonly string strVertexShader = "../../../Shaders/fs_4.glsl";
        private readonly string strFragmentShader = "../../../Shaders/vs_4.glsl";

        private readonly GameCore _core;

        public Scene(GameCore core, OpenGL gl)
        {
            Gl = gl;
            _core = core;
        }

        public void Init()
        {
            Gl.Enable(OpenGL.GL_DEPTH_TEST);
            Gl.ClearDepth(1.0);
            Light.InitLights(Gl);

            var shaderList = new List<Shader>
            {
                CreateShader(OpenGL.GL_VERTEX_SHADER, strVertexShader),
                CreateShader(OpenGL.GL_FRAGMENT_SHADER, strFragmentShader)
                /*CreateShader(OpenGL.GL_VERTEX_SHADER, ""),
                CreateShader(OpenGL.GL_FRAGMENT_SHADER, "")*/
            };

            Program.CreateInContext(Gl);
            foreach (var shader in shaderList)
            {
                Program.AttachShader(shader);
            }
            Program.Link();
            GCamera = new GameCamera();
            _core.Start();
            GCamera.AssignCameraKeys();
        }

        /// <summary>
        /// Creates Shader object from file
        /// </summary>
        /// <param name="eShaderType">Shader Type: Vertex or Fragment</param>
        /// <param name="strShaderFile">Shader File</param>
        /// <returns></returns>
        private Shader CreateShader(uint eShaderType, string strShaderFile)
        {
            string strFileData = null;
            if (!string.IsNullOrWhiteSpace(strShaderFile))
            {
                strFileData = File.ReadAllText(strShaderFile);
            }
            Shader shader;
            if (eShaderType == OpenGL.GL_VERTEX_SHADER)
            {
                shader = new VertexShader();
                shader.CreateInContext(Gl);
                if (string.IsNullOrWhiteSpace(strFileData))
                {
                    var source = "void main()" + Environment.NewLine +
                                 "{" + Environment.NewLine +
                                 "gl_Position = ftransform();" + Environment.NewLine +
                                 "}" + Environment.NewLine;
                    shader.SetSource(source);
                }
            }
            else if (eShaderType == OpenGL.GL_FRAGMENT_SHADER)
            {
                shader = new FragmentShader();
                shader.CreateInContext(Gl);
                if (string.IsNullOrWhiteSpace(strFileData))
                {
                    shader.SetSource("void main()" + Environment.NewLine +
                                     "{" + Environment.NewLine +
                                     "gl_FragColor = vec4(0.4,0.4,0.8,1.0);" + Environment.NewLine +
                                     "}" + Environment.NewLine);
                }
            }
            else { throw new ArgumentException("Wrong shader type"); }

            if (!string.IsNullOrWhiteSpace(strShaderFile))
            {
                shader.SetSource(strFileData);
            }

            shader.Compile();
            return shader;
        }

        /// <summary>
        /// OpenGL Graphic Main Loop.
        /// Called each frame
        /// </summary>
        public void Draw()
        {
            GCamera.Project(Gl);

            Gl.ClearColor(0.05f, 0.1f, 0.5f, 0f);
            Gl.Clear(OpenGL.GL_COLOR_BUFFER_BIT | OpenGL.GL_DEPTH_BUFFER_BIT);

            Program.Push(Gl, null);

            _core.Draw();

            #region gAxis
            Gl.LoadIdentity();
            Gl.Color(0.3, 0.3, 0.3);
            Gl.Begin(OpenGL.GL_LINES);
            for (var p = -100.0; p <= 100.0; p = p + 5.0)
            {
                Gl.Vertex(-100.0f, 0.0f, p);
                Gl.Vertex(100.0f, 0.0f, p);
                Gl.Vertex(p, 0.0f, -100.0);
                Gl.Vertex(p, 0.0f, 100.0);
            }
            Gl.End();
            #endregion

            #region sAxis
            Gl.LoadIdentity();
            Gl.Begin(OpenGL.GL_LINES);
            Gl.Color(1.0, 0.0, 0.0); // os X w kolorze czerwonym
            Gl.Vertex(0.0, 0.0, 0.0);
            Gl.Vertex(10.0, 0.0, 0.0);
            Gl.Color(0.0, 1.0, 0.0); // os Y w kolorze zielonym
            Gl.Vertex(0.0, 0.0, 0.0);
            Gl.Vertex(0.0, 10.0, 0.0);
            Gl.Color(0.0, 0.0, 1.0); // os Z w kolorze niebieskim
            Gl.Vertex(0.0, 0.0, 0.0);
            Gl.Vertex(0.0, 0.0, 10.0);
            Gl.End();
            #endregion

            Program.Pop(Gl, null);
            Gl.Flush();
        }

        #region Properties
        public OpenGL Gl { get; private set; }
        public ShaderProgram Program { get; } = new ShaderProgram();
        public GameCamera GCamera { get; private set; }
        #endregion
    }
}
