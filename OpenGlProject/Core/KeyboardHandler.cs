using System;
using System.Collections.Concurrent;
using System.Windows.Forms;
using System.Windows.Input;
using OpenGlProject.Core.Misc;
using OpenGlProject.Core.Objects;
using Application = System.Windows.Application;

namespace OpenGlProject.Core
{
    public delegate void OnGlobalKeyPressed(GlKeyEventArgs args);

    /// <summary>
    /// Handles all global keyboard events and invokes glObject ones
    /// </summary>
    public class KeyboardHandler
    {
        public KeyboardHandler()
        {
            Instance = this;
            KeyDown += args =>
            {
                if (args.Key == Key.Escape)
                {
                    var dialogResult = MessageBox.Show("Are you sure you want to exit?", "Exit", MessageBoxButtons.YesNo, MessageBoxIcon.Question, MessageBoxDefaultButton.Button1);
                    if (dialogResult == DialogResult.Yes)
                    {
                        Application.Current.Shutdown();
                    }
                }
            };
            KeyDown += args =>
            {
                if (args.Key != Key.C) { return; }
                var cube = new Cube(2);
                cube.KeyDown += (sender, eventArgs) =>
                {
                    if (eventArgs.Key == Key.J)
                    {
                        cube.Transformation.RotateX += 1f * eventArgs.Delta;
                    }
                    if (eventArgs.Key == Key.L)
                    {
                        cube.Transformation.RotateY += 1f * eventArgs.Delta;
                    }
                };
                cube.KeyUp += (sender, eventArgs) =>
                {
                    if (eventArgs.Key == Key.V)
                    {
                        cube.Visible = !cube.Visible;
                    }
                };
            };
        }

        public void OnKeyPressed(GameCore gameCore, KeyTypeEvent keyTypeEvent, Key key)
        {
            switch (keyTypeEvent)
            {
                case KeyTypeEvent.UP:
                    var l1 = new GlKeyEventArgs(key);
                    Console.WriteLine("Up " + key);
                    KeyUp?.Invoke(l1);
                    if (DownKeys.ContainsKey(key))
                    {
                        DownKeys.TryRemove(key, out DateTime t);
                        Console.WriteLine("Key " + key + " pressed for " + (DateTime.Now - t).TotalMilliseconds + "ms");
                    }
                    break;
                case KeyTypeEvent.DOWN:
                    if (!DownKeys.ContainsKey(key))
                    {
                        var l2 = new GlKeyEventArgs(key);
                        Console.WriteLine("Down " + key);
                        DownKeys.TryAdd(key, DateTime.Now);
                        KeyDown?.Invoke(l2);
                    }
                    break;
            }
        }

        public void InvokeGlobalEvents(int delta)
        {
            if (KeyPressed == null) { return; }
            foreach (var entry in DownKeys.ToArray())
            {
                Console.WriteLine($@"Invoke  {entry.Key} for {(DateTime.Now - entry.Value).Ticks} from {DownKeys.Count} keys");
                var l = new GlKeyEventArgs(entry.Key, delta);
                KeyPressed.Invoke(l);
            }
        }

        #region Events
        public event OnGlobalKeyPressed KeyDown;
        public event OnGlobalKeyPressed KeyPressed;
        public event OnGlobalKeyPressed KeyUp;
        #endregion

        #region Properties
        public static KeyboardHandler Instance { get; private set; }
        public ConcurrentDictionary<Key, DateTime> DownKeys { get; } = new ConcurrentDictionary<Key, DateTime>();
        #endregion
    }
}
