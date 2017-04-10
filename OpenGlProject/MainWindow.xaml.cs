using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;
using OpenGlProject.Core;
using OpenGlProject.Graphic.Scene;
using OpenGlProject.Graphic.ViewModel;
using SharpGL;
using SharpGL.Enumerations;
using SharpGL.SceneGraph;
using SharpGL.SceneGraph.Cameras;
using SharpGL.SceneGraph.Primitives;
using SharpGL.SceneGraph.Shaders;
using Cube = OpenGlProject.Core.Objects.Cube;

namespace OpenGlProject
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private readonly string strVertexShader = "../../../Shaders/fs_4.glsl";
        private readonly string strFragmentShader = "../../../Shaders/vs_4.glsl";

        public MainWindow()
        {
            WindowContext = new MainWindowContext();
            WindowContext.PropertyChanged += AppContext_OnPropertyChanged;
            DataContext = WindowContext;
            InitializeComponent();
            Closing += (sender, args) => { OnAppClose(); };
            Core = new AppCore(this);
        }

        private void AppContext_OnPropertyChanged(object o, PropertyChangedEventArgs args)
        {
            var list = new List<string> { "Tps", "RenderingObjects" };
            if (list.Contains(args.PropertyName))
            {
                Dispatcher.BeginInvoke(DispatcherPriority.Normal, new Action(() =>
                    {
                        TbDebug.Text = WindowContext.Debug;
                    }
                ));
            }
        }

        private void OnAppClose()
        {
            Core.Stop();
        }

        private void OpenGLControl_OpenGLInitialized(object sender, OpenGLEventArgs args)
        {
            Gl = args.OpenGL;
            if (!OpenGlInitialized)
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
                GCamera=new GameCamera();
                Core.Start();
            }
            OpenGlInitialized = true;
            var c = new Cube(2);
            c.Rotation.Rx += 20f;
            c.Rotation.Ry += 30f;
        }

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

        private void OpenGLControl_OpenGLDraw(object sender, OpenGLEventArgs args)
        {
            Gl.MatrixMode(MatrixMode.Modelview);
            Gl.ClearColor(0.05f, 0.1f, 0.5f, 0f);
            Gl.Clear(OpenGL.GL_COLOR_BUFFER_BIT | OpenGL.GL_DEPTH_BUFFER_BIT);
            GCamera.Cam.TransformProjectionMatrix(Gl);
            Gl.Translate(0.0f, 0.0f, -6.0f);

            Program.Push(Gl, null);
            Gl.Rotate(3.0f, 0.0f, 1.0f, 0.0f);

            Core.Draw();
            Program.Pop(Gl, null);
            Gl.Flush();
        }

        private void OpenGLControl_OnResized(object sender, OpenGLEventArgs args)
        {
            // Load and clear the projection matrix.
            Gl.MatrixMode(OpenGL.GL_PROJECTION);
            Gl.LoadIdentity();

            // Perform a perspective transformation
            Gl.Perspective(45.0f, (float)Gl.RenderContextProvider.Width /
                                  (float)Gl.RenderContextProvider.Height,
                0.1f, 100.0f);
            // Load the modelview.
            Gl.MatrixMode(OpenGL.GL_MODELVIEW);
        }

        public OpenGL Gl { get; private set; }
        public ShaderProgram Program { get; } = new ShaderProgram();
        public GameCamera GCamera { get; private set; }
        public bool OpenGlInitialized { get; private set; }
        public AppCore Core { get; }
        public MainWindowContext WindowContext { get; }
    }
}
