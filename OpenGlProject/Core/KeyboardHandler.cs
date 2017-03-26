using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Input;
using OpenGlProject.Core.Misc;
using OpenGlProject.Core.Objects;
using KeyEventHandler = OpenGlProject.Core.Misc.KeyEventHandler;

namespace OpenGlProject.Core
{
    public class KeyboardHandler
    {
        private readonly ConcurrentDictionary<Key, GlKeyListener> KeyListeners = new ConcurrentDictionary<Key, GlKeyListener>();

        public KeyboardHandler()
        {
            Instance = this;
            AddKeyEventHandler(Key.Escape, KeyTypeEvent.DOWN, (sender, args) =>
             Application.Current.Shutdown(), false);
            AddKeyEventHandler(Key.C, KeyTypeEvent.DOWN, (s, a) =>
            {
                var cube = new Cube(2);
                cube.KeyDown += (sender, args) =>
                {
                    if (args.Key == Key.D)
                    {
                        cube.Rotation.Rx += 1f;
                    }
                    if (args.Key == Key.A)
                    {
                        cube.Rotation.Ry += 1f;
                    }
                    if (args.Key == Key.V)
                    {
                        cube.Visible = !cube.Visible;
                    }
                };
            }, false);
        }

        public void AddKeyEventHandler(Key key, KeyTypeEvent type, KeyEventHandler action, bool repeatable)
        {
            var l = KeyListeners.GetOrAdd(key, new GlKeyListener(key, type, repeatable));
            switch (type)
            {
                case KeyTypeEvent.UP:
                    l.KeyUp += action;
                    break;
                case KeyTypeEvent.DOWN:
                    l.KeyDown += action;
                    break;
            }
        }

        public void RemoveListeners(Key key)
        {
            GlKeyListener l;
            KeyListeners.TryRemove(key, out l);
        }

        public void KeyUp(object sender, KeyEventArgs e)
        {
            Console.WriteLine("Up " + e.Key);
            if (KeyListeners.ContainsKey(e.Key))
            {
                KeyListeners[e.Key].InvokeUpKey(this, e);
            }
            if (DownKeys.ContainsKey(e.Key))
            {
                DownKeys.Remove(e.Key);
            }
        }

        public void KeyDown(object sender, KeyEventArgs e)
        {
            if (!DownKeys.ContainsKey(e.Key))
            {
                Console.WriteLine("Down " + e.Key);
                DownKeys.Add(e.Key, DateTime.Now);
                if (KeyListeners.ContainsKey(e.Key) && !KeyListeners[e.Key].Repeatable)
                {
                    KeyListeners[e.Key].InvokeDownKey(this, e);
                }
            }
        }

        public void InvokeGlobalEvents()
        {
            foreach (var entry in DownKeys.ToArray())
            {
                if (KeyListeners.ContainsKey(entry.Key))
                {
                    Console.WriteLine($"Invoke  {entry.Key} for {(DateTime.Now - entry.Value).Ticks} from {DownKeys.Count} keys");
                    KeyListeners[entry.Key].InvokeDownKey(this, null);
                }
            }
        }

        public static KeyboardHandler Instance { get; private set; }
        public Dictionary<Key, DateTime> DownKeys { get; } = new Dictionary<Key, DateTime>();
    }
}
