using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows;
using System.Windows.Threading;
using SenryakuShuriken.Core;
using SenryakuShuriken.Graphic.ViewModel;
using SharpGL.SceneGraph;
using Scene = SenryakuShuriken.Graphic.Scene.Scene;

namespace SenryakuShuriken
{
    /// <summary>
    /// Contains OpenGL Window and handles its events.
    /// Graphic Main Loop
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        public MainWindow()
        {
            WindowContext = new MainWindowContext();
            WindowContext.PropertyChanged += AppContext_OnPropertyChanged;
            DataContext = WindowContext;
            InitializeComponent();
            Closing += (sender, args) => { Core.Stop(); };
            Core = new GameCore(this);
        }

        /// <summary>
        /// Called when some property in WPF Context changed
        /// </summary>
        /// <param name="o"></param>
        /// <param name="args"></param>
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

        #region OpenGl Window
        /// <summary>
        /// Called on OpenGL Window Creation
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OpenGLControl_OpenGLInitialized(object sender, OpenGLEventArgs args)
        {
            var gl = args.OpenGL;
            Scene = new Scene(Core, gl);
            if (!OpenGlInitialized)
            {
                Scene.Init();
            }
            OpenGlInitialized = true;
        }

        /// <summary>
        /// OpenGL Graphic Draw event handler.
        /// Called each frame
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OpenGLControl_OpenGLDraw(object sender, OpenGLEventArgs args)
        {
            Scene.Draw();
        }

        private void OpenGLControl_OnResized(object sender, OpenGLEventArgs args)
        {
            Scene.GCamera.OnResized(args.OpenGL);
        }
        #endregion

        #region Properties
        public Scene Scene { get; set; }
        public bool OpenGlInitialized { get; private set; }
        public GameCore Core { get; }
        public MainWindowContext WindowContext { get; }
        #endregion
    }
}
