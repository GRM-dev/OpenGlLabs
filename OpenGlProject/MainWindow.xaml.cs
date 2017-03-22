using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using OpenGlProject.Core;
using SharpGL;
using SharpGL.SceneGraph;

namespace OpenGlProject
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        private MainAppContext _appContext;

        public MainWindow()
        {
            AppContext = new MainAppContext();
            AppContext.PropertyChanged+=AppContext_OnPropertyChanged;
            DataContext = AppContext;
            InitializeComponent();
            Closing += (sender, args) => { OnAppClose(); };
            Logic = new CoreLogic(AppContext);
        }

        private void AppContext_OnPropertyChanged(object o, PropertyChangedEventArgs propertyChangedEventArgs)
        {

        }

        private void OnAppClose()
        {
            Logic?.Stop();
        }

        private void OpenGLControl_OpenGLInitialized(object sender, OpenGLEventArgs args)
        {
            Gl = args.OpenGL;
            Gl.Enable(OpenGL.GL_DEPTH_TEST);
            if (!OpenGlInitialized)
            {
                Logic.Start();
            }
            OpenGlInitialized = true;
        }

        private void OpenGLControl_OpenGLDraw(object sender, OpenGLEventArgs args)
        {
            Gl.Clear(OpenGL.GL_COLOR_BUFFER_BIT | OpenGL.GL_DEPTH_BUFFER_BIT);
            //  Reset the modelview matrix.
            Gl.LoadIdentity();
            //  Move the geometry into a fairly central position.
            Gl.Translate(-1.5f, 0.0f, -6.0f);
            //  First, rotate the modelview matrix.
            Gl.Rotate(AppContext.RotatePyramid, 0.0f, 1.0f, 0.0f);

            //  Draw a pyramid. Start drawing triangles.
            Gl.Begin(OpenGL.GL_TRIANGLES);

            Gl.Color(1.0f, 0.0f, 0.0f);
            Gl.Vertex(0.0f, 1.0f, 0.0f);
            Gl.Color(0.0f, 1.0f, 0.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);
            Gl.Color(0.0f, 0.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);

            Gl.Color(1.0f, 0.0f, 0.0f);
            Gl.Vertex(0.0f, 1.0f, 0.0f);
            Gl.Color(0.0f, 0.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);
            Gl.Color(0.0f, 1.0f, 0.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);

            Gl.Color(1.0f, 0.0f, 0.0f);
            Gl.Vertex(0.0f, 1.0f, 0.0f);
            Gl.Color(0.0f, 1.0f, 0.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);
            Gl.Color(0.0f, 0.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);

            Gl.Color(1.0f, 0.0f, 0.0f);
            Gl.Vertex(0.0f, 1.0f, 0.0f);
            Gl.Color(0.0f, 0.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Color(0.0f, 1.0f, 0.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);

            Gl.End();

            //  Reset the modelview.
            Gl.LoadIdentity();

            //  Move into a more central position.
            Gl.Translate(1.5f, 0.0f, -7.0f);

            //  Rotate the cube.
            Gl.Rotate(AppContext.Rquad, 1.0f, 1.0f, 1.0f);

            //  Provide the cube colors and geometry.
            Gl.Begin(OpenGL.GL_QUADS);

            Gl.Color(0.0f, 1.0f, 0.0f);
            Gl.Vertex(1.0f, 1.0f, -1.0f);
            Gl.Vertex(-1.0f, 1.0f, -1.0f);
            Gl.Vertex(-1.0f, 1.0f, 1.0f);
            Gl.Vertex(1.0f, 1.0f, 1.0f);

            Gl.Color(1.0f, 0.5f, 0.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);

            Gl.Color(1.0f, 0.0f, 0.0f);
            Gl.Vertex(1.0f, 1.0f, 1.0f);
            Gl.Vertex(-1.0f, 1.0f, 1.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);

            Gl.Color(1.0f, 1.0f, 0.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Vertex(-1.0f, 1.0f, -1.0f);
            Gl.Vertex(1.0f, 1.0f, -1.0f);

            Gl.Color(0.0f, 0.0f, 1.0f);
            Gl.Vertex(-1.0f, 1.0f, 1.0f);
            Gl.Vertex(-1.0f, 1.0f, -1.0f);
            Gl.Vertex(-1.0f, -1.0f, -1.0f);
            Gl.Vertex(-1.0f, -1.0f, 1.0f);

            Gl.Color(1.0f, 0.0f, 1.0f);
            Gl.Vertex(1.0f, 1.0f, -1.0f);
            Gl.Vertex(1.0f, 1.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, 1.0f);
            Gl.Vertex(1.0f, -1.0f, -1.0f);

            Gl.End();

            //  Flush OpenGL.
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

        public OpenGL Gl { get; set; }
        public bool OpenGlInitialized { get; private set; }
        public CoreLogic Logic { get; }

        public MainAppContext AppContext
        {
            get { return _appContext; }
            set { _appContext = value; }
        }
    }
}
