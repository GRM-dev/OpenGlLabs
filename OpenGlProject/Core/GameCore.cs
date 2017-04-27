using SenryakuShuriken.Core.Misc;
using SenryakuShuriken.Graphic.Renderers;

namespace SenryakuShuriken.Core
{
    /// <summary>
    /// Main class of our Game.
    /// Handles all main app events and contains key, mouse and other in/out handles.
    /// </summary>
    public class GameCore
    {
        private readonly MainWindow _window;

        public GameCore(MainWindow window)
        {
            _window = window;
            Instance = this;
            Logic = new CoreLogic(Window.WindowContext);
            SetupKeyboard();
        }

        /// <summary>
        /// Attaches key handlers from KeyboardHandler class to Key Events
        /// </summary>
        private void SetupKeyboard()
        {
            KeyHandler = new KeyboardHandler();
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

        #region Start & Stop App
        public void Start()
        {
            Logic.Start();
        }

        public void Stop()
        {
            Logic.Stop();
        }
        #endregion

        /// <summary>
        /// Called by Graphic loop.
        /// Iterates through Renderers and renders all objects
        /// </summary>
        public void Draw()
        {
            foreach (var r in BasicRenderer.Renderers)
            {
                r.RenderAll();
            }
        }

        #region Properties
        public KeyboardHandler KeyHandler { get; private set; }
        public CoreLogic Logic { get; }
        public static GameCore Instance { get; private set; }
        public MainWindow Window => _window;
        #endregion
    }
}
