using System;
using System.Collections.Generic;
using System.Windows.Input;

namespace OpenGlProject.Core.Misc
{
    public delegate void KeyEventHandler(object sender, GlKeyEventArgs args);

    public class GlKeyListener
    {
        public GlKeyListener(Key key, KeyTypeEvent type = KeyTypeEvent.DOWN, bool repeatable = false)
        {
            Type = type;
            Repeatable = repeatable;
            Key = key;
        }

        public virtual void OnKeyDown(GlKeyEventArgs e)
        {
            KeyDown?.Invoke(this, e);
        }

        public virtual void OnKeyUp(GlKeyEventArgs e)
        {
            KeyUp?.Invoke(this, e);
        }

        public void InvokeUpKey(object sender, KeyEventArgs args)
        {
            KeyUp?.Invoke(sender,new GlKeyEventArgs(Key));
        }

        public void InvokeDownKey(object sender, KeyEventArgs args)
        {
            KeyDown?.Invoke(sender, new GlKeyEventArgs(Key));
        }

        public event KeyEventHandler KeyDown;
        public event KeyEventHandler KeyUp;

        public Key Key { get; }
        public KeyTypeEvent Type { get; }
        public bool Repeatable { get; }
    }

    public enum KeyTypeEvent
    {
        UP, DOWN
    }
}