using OpenGlProject.Core.Misc;
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
            Logic = new CoreLogic(Window.WindowContext);
            Window.KeyUp += (sender, args) =>
            {
                KeyHandler.OnKeyPressed(this, KeyTypeEvent.UP, args.Key);
                args.Handled = true;
            };
            Window.KeyDown += (sender, args) =>
            {
                KeyHandler.OnKeyPressed(this, KeyTypeEvent.DOWN, args.Key);
                args.Handled = true;
            };
        }

        public void Start()
        {
            Logic.Start();
        }

        public void Draw()
        {
            foreach (var r in BasicRenderer.Renderers)
            {
                Window.Gl.LoadIdentity();
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
