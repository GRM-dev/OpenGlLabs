using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Graphic.Renderers;

namespace OpenGlProject.Core
{
    public class AppCore
    {
        private readonly MainWindow _window;

        public AppCore(MainWindow window)
        {
            _window = window;
            Instance = this;
            KeyHandler = new KeyboardHandler();
            Logic = new CoreLogic(Window.AppContext);
            Window.KeyUp += KeyHandler.KeyUp;
            Window.KeyDown += KeyHandler.KeyDown;
        }

        public void Start()
        {
            Logic.Start();
        }

        public void Draw()
        {
            foreach (var r in BasicRenderer.Renderers)
            {
                r.RenderAll();
            }
        }

        public void Stop()
        {
            Logic.Stop();
        }

        public KeyboardHandler KeyHandler { get; }

        public CoreLogic Logic { get; }
        public static AppCore Instance { get; private set; }

        public MainWindow Window => _window;
    }
}
