﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Windows;
using System.Windows.Threading;
using OpenGlProject.Core;
using OpenGlProject.Graphic.Scene;
using OpenGlProject.Graphic.ViewModel;
using SharpGL;
using SharpGL.SceneGraph;
using SharpGL.SceneGraph.Shaders;

namespace OpenGlProject
{
    /// <summary>
    /// Contains OpenGL Window and handles its events.
    /// Graphic Main Loop
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
                GCamera = new GameCamera();
                Core.Start();
                GCamera.AssignCameraKeys();
            }
            OpenGlInitialized = true;
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
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OpenGLControl_OpenGLDraw(object sender, OpenGLEventArgs args)
        {
            GCamera.Project(Gl);

            Gl.ClearColor(0.05f, 0.1f, 0.5f, 0f);
            Gl.Clear(OpenGL.GL_COLOR_BUFFER_BIT | OpenGL.GL_DEPTH_BUFFER_BIT);

            Program.Push(Gl, null);

            Core.Draw();

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

        private void OpenGLControl_OnResized(object sender, OpenGLEventArgs args)
        {
            GCamera.OnResized(args.OpenGL);
        }
        #endregion

        #region Properties
        public OpenGL Gl { get; private set; }
        public ShaderProgram Program { get; } = new ShaderProgram();
        public GameCamera GCamera { get; private set; }
        public bool OpenGlInitialized { get; private set; }
        public GameCore Core { get; }
        public MainWindowContext WindowContext { get; }
        #endregion
    }
}
