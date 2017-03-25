using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Windows.Input;
using OpenGlProject.Core.Objects;
using Application = System.Windows.Application;
using KeyEventArgs = System.Windows.Input.KeyEventArgs;

namespace OpenGlProject.Core
{
    public class KeyboardHandler
    {
        private readonly Dictionary<Key, Action> DownKeyListeners = new Dictionary<Key, Action>();
        private readonly Dictionary<Key, Action> UpKeyListeners = new Dictionary<Key, Action>();

        public KeyboardHandler()
        {
            Instance = this;
            DownKeyListeners.Add(Key.Escape, () => Application.Current.Shutdown());
            DownKeyListeners.Add(Key.C, () =>
            {
                var cube = new Cube(2);
                cube.KeyDown += (sender, args) =>
                {
                    if (args.KeyCode == Keys.D)
                    {
                        cube.Rotation.Rx += 1f;
                    }
                    if (args.KeyCode == Keys.A)
                    {
                        cube.Rotation.Ry += 1f;
                    }
                    if (args.KeyCode == Keys.V)
                    {
                        cube.Visible = !cube.Visible;
                    }
                };
            });
        }

        public void AddUpKeyListener(Key key, Action action)
        {
            UpKeyListeners.Add(key, action);
        }

        public void AddDownKeyListener(Key key, Action action)
        {
            DownKeyListeners.Add(key, action);
        }

        public void KeyUp(object sender, KeyEventArgs e)
        {
            Console.WriteLine("Up " + e.Key);
        }

        public void KeyDown(object sender, KeyEventArgs e)
        {
            Console.WriteLine("Down " + e.Key);
            if (DownKeyListeners.ContainsKey(e.Key))
            {
                DownKeyListeners[e.Key].Invoke();
            }
        }

        public static KeyboardHandler Instance { get; set; }
    }
}
