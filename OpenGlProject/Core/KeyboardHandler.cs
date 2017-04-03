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
    public class KeyboardHandler
    {

        public KeyboardHandler()
        {
            Instance = this;
            KeyDown += l =>
            {
                if (l.Key == Key.Escape)
                {
                    
                    Application.Current.Shutdown();
                }
            };
            KeyPressed += l =>
            {
                if (l.Key != Key.C) { return; }
                var cube = new Cube(2);
                cube.KeyDown += (sender, args) =>
                {
                    if (args.Key == Key.D)
                    {
                        cube.Rotation.Rx += 1f * args.Delta;
                    }
                    if (args.Key == Key.A)
                    {
                        cube.Rotation.Ry += 1f * args.Delta;
                    }
                };
                cube.KeyUp += (sender, args) =>
                {
                    if (args.Key == Key.V && !args.Repeatable)
                    {
                        cube.Visible = !cube.Visible;
                    }
                };
            };
        }

        public void OnKeyPressed(AppCore appCore, KeyTypeEvent keyTypeEvent, Key key)
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

        public event OnGlobalKeyPressed KeyDown;
        public event OnGlobalKeyPressed KeyPressed;
        public event OnGlobalKeyPressed KeyUp;

        public static KeyboardHandler Instance { get; private set; }
        public ConcurrentDictionary<Key, DateTime> DownKeys { get; } = new ConcurrentDictionary<Key, DateTime>();
    }
}
