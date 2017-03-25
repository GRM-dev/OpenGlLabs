using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
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
using OpenGlProject.Graphic.ViewModel;
using SharpGL;
using SharpGL.SceneGraph;

namespace OpenGlProject
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            AppContext = new MainAppContext();
            AppContext.PropertyChanged += AppContext_OnPropertyChanged;
            DataContext = AppContext;
            InitializeComponent();
            Closing += (sender, args) => { OnAppClose(); };
            Core = new AppCore(this);
        }

        private void AppContext_OnPropertyChanged(object o, PropertyChangedEventArgs args)
        {
            if (args.PropertyName == "Tps")
            {
                Dispatcher.BeginInvoke(DispatcherPriority.Normal, new Action(() =>
                    {
                        TbDebug.Text = AppContext.Debug;
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
            Gl.Enable(OpenGL.GL_DEPTH_TEST);
            if (!OpenGlInitialized)
            {
                Core.Start();
            }
            OpenGlInitialized = true;
        }

        private void OpenGLControl_OpenGLDraw(object sender, OpenGLEventArgs args)
        {
            Gl.ClearColor(0.05f, 0.1f, 0.5f, 0f);
            Gl.Clear(OpenGL.GL_COLOR_BUFFER_BIT | OpenGL.GL_DEPTH_BUFFER_BIT | OpenGL.GL_STENCIL_BUFFER_BIT);
            Core.Draw();
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
        public bool OpenGlInitialized { get; private set; }
        public AppCore Core { get; }
        public MainAppContext AppContext { get; }
    }
}
