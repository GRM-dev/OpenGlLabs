using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;

namespace OpenGlProject
{
    public class KeyboardHandler
    {
        private readonly Dictionary<Key, Action> DownKeyListeners = new Dictionary<Key, Action>();
        private readonly Dictionary<Key, Action> UpKeyListeners = new Dictionary<Key, Action>();

        public KeyboardHandler()
        {
            Instance = this;
            DownKeyListeners.Add(Key.Escape, () => Application.Current.Shutdown());
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
